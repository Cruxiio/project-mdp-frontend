package com.example.nutrisaver.viewmodel

import android.app.Activity
import android.app.Application // Tambahkan import ini
import android.content.Context
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel // Ubah ViewModel menjadi AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope // Tambahkan import ini
import com.example.nutrisaver.GoogleAuthClient
import com.example.nutrisaver.MockDB
import com.example.nutrisaver.data.repositories.AuthRepo
import com.example.nutrisaver.data.repositories.CommonRepo
import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.model.Allergen
import com.example.nutrisaver.ui.screens.auth.authDTO.RegisterDetailInp
import com.example.nutrisaver.ui.screens.auth.authDTO.RegisterInp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch // Tambahkan import ini
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

    // GoogleAuthClient diinisialisasi di sini (asumsi kelasnya sudah ada)
    private val googleAuthClient = GoogleAuthClient(application)

    private val sharedPreferences = application.getSharedPreferences("auth_prefs", Context.MODE_PRIVATE)

    // LiveData untuk menyimpan email yang diingat
    private val _rememberedEmail = MutableLiveData<String?>()
    val rememberedEmail: LiveData<String?> = _rememberedEmail

    init {
        checkAuthStatus()
        loadRememberedEmail()
    }

    fun checkAuthStatus() {
        if (auth.currentUser != null) {
            _authState.value = AuthState.Authenticated
        } else {
            _authState.value = AuthState.Unauthenticated
        }
        getAlergen("")
    }

    // ======================== common repo func ==========================
    fun getAlergen(keyword: String) {
        // Log #1: Memastikan fungsi ini dipanggil dari UI
        Log.d("VIEWMODEL_DEBUG", "1. getAlergen DIPANGGIL dengan keyword: '$keyword'")

        viewModelScope.launch {
            try {
                // Log #2: Tepat sebelum memanggil repository
                Log.d("VIEWMODEL_DEBUG", "2. Memanggil commonRepo.getAllergen...")
                val result = commonRepo.getAllergen(keyword)

                // Log #3: HASIL MENTAH DARI REPOSITORY - INI LOG PALING PENTING
                Log.d("VIEWMODEL_DEBUG", "3. SUKSES dari repo. Ukuran list: ${result.size}")
                if (result.isNotEmpty()) {
                    Log.d("VIEWMODEL_DEBUG", "   -> Item pertama: ${result.first().name}")
                }

                // Log #4: Nilai LiveData SEBELUM di-update
                Log.d("VIEWMODEL_DEBUG", "4. LiveData SEBELUM update. Ukuran: ${_alergenState.value?.size ?: "null"}")

                // Ini adalah operasi update LiveData
                _alergenState.value = result

                // Log #5: Nilai LiveData SETELAH di-update
                Log.d("VIEWMODEL_DEBUG", "5. LiveData SETELAH update. Ukuran: ${_alergenState.value?.size ?: "null"}")

            } catch (e: Exception) {
                // Log #6: Jika terjadi error di mana pun dalam blok try
                Log.e("VIEWMODEL_DEBUG", "6. TERJADI ERROR saat mengambil alergen", e)
            }
        }
    }

    // ======================== auth repo func ============================

    /**
     * NOTE: Fungsi login ini menggunakan Firebase Auth secara langsung.
     * Ini cocok jika Anda tidak memiliki sistem login custom di backend.
     * Jika Anda punya sistem login custom yang mengembalikan custom token,
     * lihat catatan di bawah.
     */
    private fun loadRememberedEmail() {
        _rememberedEmail.value = sharedPreferences.getString("REMEMBERED_EMAIL", null)
    }

    // DIUBAH: Fungsi login sekarang juga mengatur SharedPreferences
    fun login(email: String, password: String, rememberMe: Boolean) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email dan Password wajib diisi!")
            return
        }
        _authState.value = AuthState.Loading

        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Jika login berhasil, atur SharedPreferences berdasarkan checkbox
                    if (rememberMe) {
                        // Simpan email
                        sharedPreferences.edit().putString("REMEMBERED_EMAIL", email).apply()
                    } else {
                        // Hapus email yang tersimpan
                        sharedPreferences.edit().remove("REMEMBERED_EMAIL").apply()
                    }
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value =
                        AuthState.Error(task.exception?.message ?: "Terjadi masalah saat login")
                }
            }
    }

    fun signInWithGoogle(activityContext: Activity) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                // 1. Lakukan sign-in Google seperti biasa
                val signInSuccess = googleAuthClient.signIn(activityContext)
                if (!signInSuccess) throw Exception("Gagal login dengan Google.")

                // 2. Ambil user & token dari Firebase
                val firebaseUser = auth.currentUser ?: throw Exception("Sesi Firebase tidak ditemukan.")
                val token = firebaseUser.getIdToken(true).await().token ?: throw Exception("Token tidak valid.")
                val uid = firebaseUser.uid

                // 3. Gunakan FUNGSI BARU untuk cek ke backend
                Log.d("AuthViewModel", "Google Sign-In success. Checking user in backend with UID: $uid")
                val existingUser = authRepo.getUserProfileForRegister(token, uid)

                // 4. Tentukan alur berikutnya berdasarkan hasil
                if (existingUser != null) {
                    // User sudah ada, langsung ke dashboard
                    Log.d("AuthViewModel", "User already exists. Navigating to dashboard.")
                    _authState.value = AuthState.Authenticated
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
                // ================== LOGIKA PERCABANGAN KUNCI ==================
                // Cek apakah ini alur registrasi manual (ditandai dengan adanya password).
                if (registerInp.password.isNotEmpty()) {
                    // --- INI ADALAH ALUR REGISTRASI MANUAL ---
                    Log.d("AuthViewModel", "Signup path: Manual Registration")

                    // 1. Buat user baru di Firebase Auth menggunakan data dari halaman pertama.
                    val authResult = auth.createUserWithEmailAndPassword(registerInp.email, registerInp.password).await()
                    val firebaseUser = authResult.user ?: throw Exception("Gagal membuat user di Firebase.")

                    // 2. Bangun objek User untuk dikirim ke backend.
                    val userToRegister = User(
                        id = null,
                        uuid = firebaseUser.uid,
                        role = "user",
                        name = inp.name, // Ambil dari form detail
                        username = registerInp.username, // Ambil dari form pertama
                        email = registerInp.email, // Ambil dari form pertama
                        dateOfBirth = MockDB.dateFormater(inp.dateOfBirth),
                        gender = inp.gender,
                        weight = inp.weight,
                        height = inp.height,
                        profilePicture = null, // User manual tidak punya foto profil awal
                        goal = inp.goalOption,
                        targetWeight = inp.targetWeight,
                        dietType = inp.dietTypeOption,
                        proteinRatio = inp.protein,
                        carbsRatio = inp.carbs,
                        fatRatio = inp.fat,
                        allergen = inp.allergen,
                        // field lain
                        nutritionNeeds = null,
                        firebaseToken = null,
                        createdAt = null,
                        updatedAt = null,
                        deletedAt = null
                    )

                    // 3. Kirim data ke backend Anda.
                    Log.d("AuthViewModel", "Registering manual user to backend: $userToRegister")
                    authRepo.register(userToRegister)

                } else {
                    // --- INI ADALAH ALUR MELENGKAPI PROFIL GOOGLE ---
                    Log.d("AuthViewModel", "Signup path: Google Profile Completion")

                    // 1. User sudah ada, cukup ambil data user yang sedang aktif.
                    val firebaseUser = auth.currentUser ?: throw Exception("Sesi login Google tidak ditemukan. Silakan coba lagi.")

                    // 2. Bangun objek User untuk dikirim ke backend.
                    val userToRegister = User(
                        id = null,
                        uuid = firebaseUser.uid,
                        role = "user",
                        name = inp.name.ifEmpty { firebaseUser.displayName ?: "" }, // Gunakan nama dari Google jika form kosong
                        username = firebaseUser.displayName?.split(" ")?.first() ?: "user", // Default username dari Google
                        email = firebaseUser.email!!, // Email pasti ada dari Google
                        dateOfBirth = MockDB.dateFormater(inp.dateOfBirth),
                        gender = inp.gender,
                        weight = inp.weight,
                        height = inp.height,
                        profilePicture = firebaseUser.photoUrl?.toString(), // Ambil foto dari Google
                        goal = inp.goalOption,
                        targetWeight = inp.targetWeight,
                        dietType = inp.dietTypeOption,
                        proteinRatio = inp.protein,
                        carbsRatio = inp.carbs,
                        fatRatio = inp.fat,
                        allergen = inp.allergen,
                        // field lain
                        nutritionNeeds = null,
                        firebaseToken = null,
                        createdAt = null,
                        updatedAt = null,
                        deletedAt = null
                    )

                    // 3. Kirim data ke backend Anda.
                    Log.d("AuthViewModel", "Registering Google user to backend: $userToRegister")
                    authRepo.register(userToRegister)
                }

                // Setelah salah satu alur berhasil, set state ke Authenticated.
                _authState.value = AuthState.Authenticated

                // (Sangat disarankan) Bersihkan state registerInp setelah selesai.
                registerInp = RegisterInp()

            } catch (e: Exception) {
                if (e is CancellationException) throw e

                // Memberikan pesan error yang lebih baik kepada user
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
                googleAuthClient.signOut() // Asumsi ini juga memanggil auth.signOut()
                auth.signOut() // Panggil signOut Firebase secara eksplisit

                // Hapus email yang diingat saat logout
                sharedPreferences.edit().remove("REMEMBERED_EMAIL").apply()

                _authState.value = AuthState.Unauthenticated
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _authState.value = AuthState.Error(e.message ?: "Terjadi error saat Sign Out.")
            }
        }
    }
}

// Sealed class untuk AuthState (tetap sama)
sealed class AuthState {
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    object ToRegisterPage2 : AuthState()
    object NeedsRegistrationDetails : AuthState()
    data class Error(val message: String) : AuthState()
}
