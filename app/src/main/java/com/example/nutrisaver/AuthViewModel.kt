package com.example.nutrisaver

import android.app.Activity
import android.app.Application // Tambahkan import ini
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel // Ubah ViewModel menjadi AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope // Tambahkan import ini
import com.example.nutrisaver.data.repositories.AuthRepo
import com.example.nutrisaver.ui.screens.auth.authDTO.RegisterDetailInp
import com.example.nutrisaver.ui.screens.auth.authDTO.RegisterInp
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch // Tambahkan import ini

// Ubah AuthViewModel menjadi AndroidViewModel dan tambahkan Application sebagai parameter konstruktor
class AuthViewModel(authRepo:AuthRepo,application: Application) : AndroidViewModel(application) {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    var registerInp: RegisterInp = RegisterInp()


    // Instansiasi GoogleAuthClient menggunakan application context
    private val googleAuthClient = GoogleAuthClient(application)

    init {
        // Panggil checkAuthStatus dari GoogleAuthClient karena itu yang paling update
        // atau pastikan keduanya konsisten. Untuk sekarang, checkAuthStatus Firebase cukup.
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        // Anda bisa juga memanggil googleAuthClient.isSignedIn() di sini jika ingin
        // memastikan konsistensi penuh, tapi firebaseAuth.currentUser biasanya cukup.
        if (auth.currentUser != null){
            _authState.value = AuthState.Authenticated
        }
        else{
            _authState.value = AuthState.Unauthenticated
        }
    }

    fun login(email : String, password : String){
        if(email.isEmpty() || password.isEmpty()){
            _authState.value = AuthState.Error("Email dan Password wajib diisi!")
            return
        }
        _authState.value = AuthState.Loading // Set loading state
        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }
                else{
                    _authState.value = AuthState.Error(task.exception?.message?: "Terjadi masalah saat login")
                }
            }

    }

    // Modifikasi signInWithGoogle untuk menerima Activity
    fun signInWithGoogle(activityContext: Activity) { // Ubah tipe Context menjadi Activity
        _authState.value = AuthState.Loading
        viewModelScope.launch {
            try {
                // Teruskan activityContext ke googleAuthClient.signIn
                val success = googleAuthClient.signIn(activityContext)
                if (success) {
                    _authState.value = AuthState.Authenticated
                } else {
                    _authState.value = AuthState.Error("Google Sign-In Gagal. Periksa log untuk detail.")
                }
            } catch (e: CancellationException) {
                _authState.value = AuthState.Unauthenticated
                println("Google Sign-In dibatalkan oleh pengguna: ${e.message}")
            } catch (e: Exception) {
                e.printStackTrace()
                _authState.value = AuthState.Error(e.message ?: "Terjadi error saat Sign In dengan Google.")
            }
        }
    }


    fun registerPage1(username: String,email : String, password : String, confPass:String) {
        // ... (kode registerPage1 tetap sama)
        if(email.isEmpty() || password.isEmpty() || username.isEmpty() || confPass.isEmpty()){
            _authState.value = AuthState.Error("Input register wajib diisi!")
            return
        }
        if (password != confPass){
            _authState.value = AuthState.Error("Password dan confirm password harus sama!")
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _authState.value = AuthState.Error("Format email tidak sesuai!")
            return
        }
        registerInp = RegisterInp(username,email,password)
        _authState.value = AuthState.ToRegisterPage2
    }

    fun signup(inp: RegisterDetailInp){
        // ... (kode signup tetap sama)
        val err = inp.checkKosong()
        if(err != ""){
            _authState.value = AuthState.Error(err)
            return
        }
        _authState.value = AuthState.Loading // Set loading state
        auth.createUserWithEmailAndPassword(registerInp.email,registerInp.password)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }
                else{
                    _authState.value = AuthState.Error(task.exception?.message?: "Terjadi masalah saat registrasi")
                }
            }
    }

    // Ubah nama menjadi signOut dan panggil googleAuthClient.signOut()
    fun signOut(){
        _authState.value = AuthState.Loading // Opsional: Set loading state
        viewModelScope.launch {
            try {
                googleAuthClient.signOut() // Ini sudah termasuk firebaseAuth.signOut()
                _authState.value = AuthState.Unauthenticated
            } catch (e: Exception) {
                if (e is CancellationException) throw e // Rethrow jika cancellation
                e.printStackTrace()
                _authState.value = AuthState.Error(e.message ?: "Terjadi error saat Sign Out.")
                // Meskipun error, coba set ke Unauthenticated karena Firebase sign out mungkin berhasil
                // atau state bisa jadi tidak konsisten.
                // _authState.value = AuthState.Unauthenticated // Opsional, tergantung behavior yang diinginkan
            }
        }
    }
}

sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    object ToRegisterPage2: AuthState()
    data class Error (val message : String) : AuthState()
}