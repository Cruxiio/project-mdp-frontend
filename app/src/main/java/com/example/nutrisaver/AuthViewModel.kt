package com.example.nutrisaver

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.nutrisaver.ui.screens.auth.authDTO.RegisterDetailInp
import com.example.nutrisaver.ui.screens.auth.authDTO.RegisterInp
import com.google.firebase.auth.FirebaseAuth

class AuthViewModel : ViewModel() {
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> = _authState
    var registerInp: RegisterInp = RegisterInp() // buat simpan sementara data dari hal register 1

    init {
        checkAuthStatus()
    }

    fun checkAuthStatus(){
        if (auth.currentUser != null){
            // klo udh login
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

        auth.signInWithEmailAndPassword(email,password)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }
                else{
                    _authState.value = AuthState.Error(task.exception?.message?: "Terjadi masalah")
                }
            }
    }

    fun registerPage1(username: String,email : String, password : String, confPass:String) {
        // check input kosong
        if(email.isEmpty() || password.isEmpty() || username.isEmpty() || confPass.isEmpty()){
            _authState.value = AuthState.Error("Input register wajib diisi!")
            return
        }

        //check password dan confPass
        if (password != confPass){
            _authState.value = AuthState.Error("Password dan confirm password harus sama!")
            return
        }

        // check format email
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            _authState.value = AuthState.Error("Format email tidak sesuai!")
            return
        }

        // check email sudah terdaftar atau blom

        // check username sudah terdaftar atau blom

        // simpan data register ke temp variabel
        registerInp = RegisterInp(username,email,password)

        // ganti state
        _authState.value = AuthState.ToRegisterPage2
    }

    fun signup(inp: RegisterDetailInp){
        // check input kosong
        val err = inp.checkKosong();
        if(err != ""){
            _authState.value = AuthState.Error(err)
            return
        }

        // create user using firebase
        auth.createUserWithEmailAndPassword(registerInp.email,registerInp.password)
            .addOnCompleteListener{task ->
                if(task.isSuccessful){
                    _authState.value = AuthState.Authenticated
                }
                else{
                    _authState.value = AuthState.Error(task.exception?.message?: "Terjadi masalah")
                }
            }
    }

    fun signout(){
        auth.signOut()
        _authState.value = AuthState.Unauthenticated
    }
}

sealed class AuthState{
    object Authenticated : AuthState()
    object Unauthenticated : AuthState()
    object Loading : AuthState()
    object ToRegisterPage2: AuthState()
    data class Error (val message : String) : AuthState()
}