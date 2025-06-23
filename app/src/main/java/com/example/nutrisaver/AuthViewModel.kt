package com.example.nutrisaver

import android.app.Activity
import android.app.Application // Tambahkan import ini
import android.util.Log
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel // Ubah ViewModel menjadi AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope // Tambahkan import ini
import com.example.nutrisaver.data.repositories.AuthRepo
import com.example.nutrisaver.data.repositories.CommonRepo
import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.model.Allergen
import com.example.nutrisaver.ui.screens.auth.authDTO.RegisterDetailInp
import com.example.nutrisaver.ui.screens.auth.authDTO.RegisterInp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GetTokenResult
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

    private val _userProfile = MutableLiveData<User?>()
    val userProfile: LiveData<User?> = _userProfile

    var registerInp: RegisterInp = RegisterInp()

    // GoogleAuthClient diinisialisasi di sini (asumsi kelasnya sudah ada)
    private val googleAuthClient = GoogleAuthClient(application)

    init {
        checkAuthStatus()
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
    fun login(email: String, password: String) {
        if (email.isEmpty() || password.isEmpty()) {
            _authState.value = AuthState.Error("Email dan Password wajib diisi!")
            return
        }
        _authState.value = AuthState.Loading
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error(task.exception?.message ?: "Terjadi masalah saat login")
                }
            }
    }

    fun signInWithGoogle(activityContext: Activity) {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                val success = googleAuthClient.signIn(activityContext)
                if (success) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error("Google Sign-In Gagal.")
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
                // 1. Buat user di Firebase Authentication
                val authResult = auth.createUserWithEmailAndPassword(registerInp.email, registerInp.password).await()
                val firebaseUser: FirebaseUser = authResult.user ?: throw Exception("Gagal membuat user di Firebase.")

                // 2. Siapkan objek User untuk dikirim ke backend Anda
                //    Ini adalah PERBAIKAN KUNCI: pastikan semua field diisi sesuai konstruktor User.
                val userToRegister = User(
                    id = null, // ID akan dibuat oleh backend
                    uuid = firebaseUser.uid,
                    role = "user",
                    name = inp.name,
                    username = registerInp.username,
                    email = registerInp.email,
                    dateOfBirth = MockDB.dateFormater(inp.dateOfBirth), // Pastikan formatnya YYYY-MM-DD
                    gender = inp.gender,
                    weight = inp.weight,
                    height = inp.height,
                    profilePicture = null, // Diisi nanti
                    goal = inp.goalOption,
                    targetWeight = inp.targetWeight,
                    dietType = inp.dietTypeOption,
                    proteinRatio = inp.protein,
                    carbsRatio = inp.carbs,
                    fatRatio = inp.fat,
                    allergen = inp.allergen,
                    nutritionNeeds = null, // Ini akan dihitung dan diisi oleh backend
                    firebaseToken = null, // Tidak perlu untuk alur ini
                    createdAt = null, // Backend yang mengatur
                    updatedAt = null, // Backend yang mengatur
                    deletedAt = null
                )

                // 3. Panggil repository untuk menyimpan data user ke backend Anda
                Log.d("AuthViewModel", "Registering user to backend: $userToRegister")
                val newUserFromBackend = authRepo.register(userToRegister)
                Log.d("AuthViewModel", "Backend response: $newUserFromBackend")

                // 4. Jika semua berhasil, update state ke Authenticated
                _authState.value = AuthState.Authenticated

            } catch (e: Exception) {
                // IMPROVEMENT: Tangani semua kemungkinan error (network, duplikat, dll)
                if (e is CancellationException) throw e // Jangan tangani cancellation
                Log.e("AuthViewModel", "Signup failed", e)
                _authState.value = AuthState.Error(e.message ?: "Terjadi kesalahan saat registrasi.")
            }
        }
    }

    fun signOut() {
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                googleAuthClient.signOut()
                _authState.value = AuthState.Unauthenticated
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _authState.value = AuthState.Error(e.message ?: "Terjadi error saat Sign Out.")
            }
        }
    }

    fun fetchUserProfile() {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            _authState.value = AuthState.Error("User tidak login.")
            return
        }

        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                // 1. Ambil Firebase ID Token terbaru
                val token = firebaseUser.getIdToken(true).await().token

                // 2. Ambil UID dari user yang sedang login
                val uid = firebaseUser.uid

                if (token != null) {
                    // 3. Panggil repository dengan KEDUA parameter: token dan uid
                    val profile = authRepo.getUserProfile(token, uid)
                    _userProfile.value = profile
                    _authState.value = AuthState.Authenticated
                } else {
                    throw Exception("Gagal mendapatkan token autentikasi.")
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("AuthViewModel", "Gagal mengambil profil user", e)
                _authState.value = AuthState.Error(e.message ?: "Gagal memuat profil.")
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
    data class Error(val message: String) : AuthState()
}
