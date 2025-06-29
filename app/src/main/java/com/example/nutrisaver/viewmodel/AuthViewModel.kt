package com.example.nutrisaver.viewmodel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutrisaver.GoogleAuthClient
import com.example.nutrisaver.MockDB
import com.example.nutrisaver.data.model.Allergen
import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.repositories.AuthRepo
import com.example.nutrisaver.data.repositories.CommonRepo
import com.example.nutrisaver.ui.screens.auth.authDTO.RegisterDetailInp
import com.example.nutrisaver.ui.screens.auth.authDTO.RegisterInp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class AuthViewModel(
    private val commonRepo: CommonRepo,
    private val authRepo: AuthRepo,
    application: Application
) : AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState

    private val _alergenState = MutableLiveData<List<Allergen>>()
    val alergenState: LiveData<List<Allergen>> = _alergenState

    var registerInp: RegisterInp = RegisterInp()

    private val googleAuthClient = GoogleAuthClient(application)
    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    private val _rememberedEmail = MutableLiveData<String?>()
    val rememberedEmail: LiveData<String?> = _rememberedEmail

    init {
        checkAuthStatus()
        loadRememberedEmail()
    }

    // DIUBAH: Logika untuk memeriksa status login saat aplikasi dibuka
    fun checkAuthStatus() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            // Jika ada user, ambil profilnya untuk mendapatkan peran (role)
            fetchUserProfileAndSetState(currentUser)
        } else {
            _authState.value = AuthState.Unauthenticated
        }
        getAlergen("")
    }

    // ======================== common repo func ==========================
    fun getAlergen(keyword: String) {
        Log.d("VIEWMODEL_DEBUG", "1. getAlergen DIPANGGIL dengan keyword: '$keyword'")
        viewModelScope.launch {
            try {
                Log.d("VIEWMODEL_DEBUG", "2. Memanggil commonRepo.getAllergen...")
                val result = commonRepo.getAllergen(keyword)
                Log.d("VIEWMODEL_DEBUG", "3. SUKSES dari repo. Ukuran list: ${result.size}")
                _alergenState.value = result
                Log.d("VIEWMODEL_DEBUG", "5. LiveData SETELAH update. Ukuran: ${_alergenState.value?.size ?: "null"}")
            } catch (e: Exception) {
                Log.e("VIEWMODEL_DEBUG", "6. TERJADI ERROR saat mengambil alergen", e)
            }
        }
    }

    // ======================== auth repo func ============================
    private fun loadRememberedEmail() {
        _rememberedEmail.value = sharedPreferences.getString("REMEMBERED_EMAIL", null)
    }

    // DIUBAH: Logika login untuk mengambil profil setelah berhasil
    fun login(email: String, password: String, rememberMe: Boolean) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email dan Password wajib diisi!")
            return
        }
        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (rememberMe) {
                        sharedPreferences.edit().putString("REMEMBERED_EMAIL", email).apply()
                    } else {
                        sharedPreferences.edit().remove("REMEMBERED_EMAIL").apply()
                    }
                    // Panggil fungsi helper untuk mengambil profil dan role
                    task.result?.user?.let { firebaseUser ->
                        fetchUserProfileAndSetState(firebaseUser)
                    } ?: run {
                        _authState.value = AuthState.Error("Terjadi masalah saat login, user tidak ditemukan.")
                    }
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Terjadi masalah saat login")
                }
            }
    }

    // DIUBAH: Logika Google Sign-In untuk langsung menetapkan state dengan role
    fun signInWithGoogle(activityContext: Activity) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val signInSuccess = googleAuthClient.signIn(activityContext)
                if (!signInSuccess) throw Exception("Gagal login dengan Google.")

                val firebaseUser = auth.currentUser ?: throw Exception("Sesi Firebase tidak ditemukan.")
                val token = firebaseUser.getIdToken(true).await().token ?: throw Exception("Token tidak valid.")
                val uid = firebaseUser.uid

                Log.d("AuthViewModel", "Google Sign-In success. Checking user in backend with UID: $uid")
                val existingUser = authRepo.getUserProfileForRegister(token, uid)

                if (existingUser != null) {
                    // User sudah ada, langsung set state Authenticated dengan rolenya
                    Log.d("AuthViewModel", "User already exists. Role: ${existingUser.role}. Navigating...")
                    _authState.value = AuthState.Authenticated(existingUser.role)
                } else {
                    // User BARU, arahkan untuk melengkapi data
                    Log.d("AuthViewModel", "New user. Navigating to complete registration details.")
                    _authState.value = AuthState.NeedsRegistrationDetails
                }
            } catch (e: CancellationException) {
                _authState.value = AuthState.Unauthenticated
                Log.w("AuthViewModel", "Google Sign-In dibatalkan", e)
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Terjadi error saat Sign In dengan Google.")
            }
        }
    }

    fun registerPage1(username: String, email: String, password: String, confPass: String) {
        if (email.isEmpty() || password.isEmpty() || username.isEmpty() || confPass.isEmpty()) {
            _authState.value = AuthState.Error("Input register wajib diisi!")
            return
        }
        if (password != confPass) {
            _authState.value = AuthState.Error("Password dan confirm password harus sama!")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _authState.value = AuthState.Error("Format email tidak sesuai!")
            return
        }
        registerInp = RegisterInp(username, email, password)
        _authState.value = AuthState.ToRegisterPage2
    }

    fun signup(inp: RegisterDetailInp) {
        val err = inp.checkKosong()
        if (err.isNotEmpty()) {
            _authState.value = AuthState.Error(err)
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val userRole = "user" // Role default untuk semua pendaftar baru
                if (registerInp.password.isNotEmpty()) {
                    // --- ALUR REGISTRASI MANUAL ---
                    Log.d("AuthViewModel", "Signup path: Manual Registration")
                    val authResult = auth.createUserWithEmailAndPassword(registerInp.email, registerInp.password).await()
                    val firebaseUser = authResult.user ?: throw Exception("Gagal membuat user di Firebase.")

                    val userToRegister = User(
                        id = null, uuid = firebaseUser.uid, role = userRole, name = inp.name,
                        username = registerInp.username, email = registerInp.email,
                        dateOfBirth = MockDB.dateFormater(inp.dateOfBirth), gender = inp.gender,
                        weight = inp.weight, height = inp.height, profilePicture = null,
                        goal = inp.goalOption, targetWeight = inp.targetWeight,
                        dietType = inp.dietTypeOption, proteinRatio = inp.protein,
                        carbsRatio = inp.carbs, fatRatio = inp.fat, allergen = inp.allergen,
                        nutritionNeeds = null, firebaseToken = null, createdAt = null,
                        updatedAt = null, deletedAt = null
                    )
                    Log.d("AuthViewModel", "Registering manual user to backend: $userToRegister")
                    authRepo.register(userToRegister)

                } else {
                    // --- ALUR MELENGKAPI PROFIL GOOGLE ---
                    Log.d("AuthViewModel", "Signup path: Google Profile Completion")
                    val firebaseUser = auth.currentUser ?: throw Exception("Sesi login Google tidak ditemukan.")

                    val userToRegister = User(
                        id = null, uuid = firebaseUser.uid, role = userRole,
                        name = inp.name.ifEmpty { firebaseUser.displayName ?: "" },
                        username = firebaseUser.displayName?.split(" ")?.first() ?: "user",
                        email = firebaseUser.email!!,
                        dateOfBirth = MockDB.dateFormater(inp.dateOfBirth), gender = inp.gender,
                        weight = inp.weight, height = inp.height,
                        profilePicture = firebaseUser.photoUrl?.toString(),
                        goal = inp.goalOption, targetWeight = inp.targetWeight,
                        dietType = inp.dietTypeOption, proteinRatio = inp.protein,
                        carbsRatio = inp.carbs, fatRatio = inp.fat, allergen = inp.allergen,
                        nutritionNeeds = null, firebaseToken = null, createdAt = null,
                        updatedAt = null, deletedAt = null
                    )
                    Log.d("AuthViewModel", "Registering Google user to backend: $userToRegister")
                    authRepo.register(userToRegister)
                }

                // DIUBAH: Setelah registrasi berhasil, set state Authenticated dengan role 'user'
                _authState.value = AuthState.Authenticated(role = userRole)
                registerInp = RegisterInp()

            } catch (e: Exception) {
                if (e is CancellationException) throw e
                val errorMessage = when (e) {
                    is com.google.firebase.auth.FirebaseAuthUserCollisionException -> "Email ini sudah terdaftar. Silakan login atau gunakan email lain."
                    else -> e.message ?: "Terjadi kesalahan saat registrasi."
                }
                Log.e("AuthViewModel", "Signup failed", e)
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    fun signOut() {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                googleAuthClient.signOut()
                auth.signOut()
                sharedPreferences.edit().remove("REMEMBERED_EMAIL").apply()
                _authState.value = AuthState.Unauthenticated
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _authState.value = AuthState.Error(e.message ?: "Terjadi error saat Sign Out.")
            }
        }
    }

    // FUNGSI HELPER BARU: Untuk mengambil profil dan menetapkan state otentikasi
    private fun fetchUserProfileAndSetState(firebaseUser: FirebaseUser) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val token = firebaseUser.getIdToken(true).await().token ?: throw Exception("Token tidak valid.")
                val uid = firebaseUser.uid
                Log.d("AuthViewModel", "Fetching profile for UID: $uid")

                // Langkah 1: Panggil repo untuk mendapatkan profil.
                // Bisa mengembalikan User (jika ditemukan) atau null (jika 404).
                val userProfile = authRepo.getUserProfileForRegister(token, uid)

                if (userProfile != null) {
                    // --- PENGGUNA DITEMUKAN DI BACKEND ---

                    // Langkah 2: ATURAN EMAS - Cek apakah pengguna adalah admin.
                    if (userProfile.role == "admin") {
                        // Jika admin, JANGAN pedulikan field lain. Langsung masuk.
                        Log.d("AuthViewModel", "User is an ADMIN. Authenticating immediately.")
                        _authState.value = AuthState.Authenticated("admin")
                    } else {
                        // Jika bukan admin (berarti 'user'), cek kelengkapan profilnya.
                        // (Asumsi Anda sudah menambahkan fungsi isProfileComplete() di model User)
                        if (userProfile.isProfileComplete()) {
                            // Profil user lengkap, arahkan ke dasbor user.
                            Log.d("AuthViewModel", "User profile is complete. Authenticating.")
                            _authState.value = AuthState.Authenticated("user")
                        } else {
                            // Profil user ada tapi tidak lengkap (misal, keluar di tengah jalan saat registrasi).
                            // Arahkan untuk melengkapi.
                            Log.d("AuthViewModel", "User profile is INCOMPLETE. Needs registration details.")
                            _authState.value = AuthState.NeedsRegistrationDetails
                        }
                    }
                } else {
                    // --- PENGGUNA TIDAK DITEMUKAN (404 dari server) ---
                    // Ini adalah pengguna yang benar-benar baru.
                    Log.d("AuthViewModel", "User profile not found (404). Needs registration details.")
                    _authState.value = AuthState.NeedsRegistrationDetails
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("AuthViewModel", "Failed to fetch user profile", e)
                _authState.value = AuthState.Error("Gagal mengambil data profil. Silakan coba login kembali.")
            }
        }
    }
}

// Sealed class untuk AuthState (DIUBAH)
sealed class AuthState {
    data class Authenticated(val role: String) : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    object ToRegisterPage2 : AuthState()
    object NeedsRegistrationDetails : AuthState()
    data class Error(val message: String) : AuthState()
}