package com.example.nutrisaver

import android.content.Context // Biarkan ini
import android.app.Activity // Tambahkan import ini
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.credentials.CustomCredential
import androidx.credentials.GetCredentialRequest
import androidx.credentials.GetCredentialResponse
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.tasks.await

class GoogleAuthClient(
    // Context ini bisa ApplicationContext, aman untuk membuat CredentialManager
    applicationContext : Context
) {
    private val tag = "GoogleAuthClient: " // Anda bisa ganti nama tag jika mau

    // CredentialManager aman dibuat dengan ApplicationContext
    private val credentialManager  = CredentialManager.create(applicationContext)
    private val firebaseAuth = FirebaseAuth.getInstance()

    fun isSignedIn(): Boolean{
        if (firebaseAuth.currentUser != null){
            println(tag+"already signed in")
            return true
        }
        return false
    }

    // Modifikasi signIn untuk menerima Activity context
    suspend fun signIn(activityContext: Activity): Boolean{ // Ubah tipe Context menjadi Activity
        if(isSignedIn()){
            return true
        }

        try {
            // Teruskan activityContext ke buildCredentialRequest
            val result = buildCredentialRequest(activityContext)
            return handleSignIn(result)

        } catch (e:Exception){
            e.printStackTrace()
            if(e is CancellationException) throw e

            println(tag+"signIn error: ${e.message}") // ganti nama tag agar konsisten
            return false
        }
    }

    private suspend fun handleSignIn(result: GetCredentialResponse) : Boolean {
        val credential = result.credential

        if(
            credential is CustomCredential &&
            credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL
        ){
            try{
                val tokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                println(tag+"name: ${tokenCredential.displayName}")
                println(tag+"email: ${tokenCredential.id}")
                println(tag+"image: ${tokenCredential.profilePictureUri}")

                val authCredential = GoogleAuthProvider.getCredential(
                    tokenCredential.idToken, null
                )

                val authResult = firebaseAuth.signInWithCredential(authCredential).await()

                return authResult.user != null

            } catch (e: GoogleIdTokenParsingException){
                println(tag+"GoogleIdTokenParsingException: ${e.message}")
                return false
            }
        }
        else{
            println(tag+"credential is not GoogleIdTokenCredential")
            return false
        }
    }

    // Modifikasi buildCredentialRequest untuk menerima dan menggunakan Activity context
    private suspend fun buildCredentialRequest(activityContext: Activity) :GetCredentialResponse { // Ubah tipe Context menjadi Activity
        val request = GetCredentialRequest.Builder()
            .addCredentialOption(
                GetGoogleIdOption.Builder()
                    .setFilterByAuthorizedAccounts(false)
                    .setServerClientId(
                        // Pastikan ID ini adalah Web Client ID Anda dari Google Cloud Console
                        "833336552937-7md7l8q5209ev3vl1a0djv2o8cdt048a.apps.googleusercontent.com"
                    )
                    .setAutoSelectEnabled(false)
                    .build()
            )
            .build()

        // Gunakan activityContext di sini!
        return credentialManager.getCredential(
            request = request, context = activityContext
        )
    }

    suspend fun signOut(){
        // clearCredentialState tidak memerlukan Activity context
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest()
        )
        firebaseAuth.signOut()
    }
}