package com.example.nutrisaver.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.repositories.AdminRepo
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.cancellation.CancellationException

sealed class AdminUserState {
    object Idle : AdminUserState()
    object Loading : AdminUserState()
    object Success : AdminUserState()
    data class Error(val message: String) : AdminUserState()
}

class AdminUsersViewModel (
    private val adminRepo: AdminRepo,
    application: Application
): AndroidViewModel(application) {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance() // Get FirebaseAuth instance

    private val _allUsers = MutableLiveData<List<User?>>()
    val allUsers: LiveData<List<User?>> = _allUsers

    private val _adminUserState = MutableLiveData<AdminUserState>()
    val adminUserState: LiveData<AdminUserState> =
        _adminUserState // Renamed from _feedbackState for clarity

    fun init() {
        getAllUsers()
    }

    fun getAllUsers() {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            _adminUserState.value = AdminUserState.Error("Session expired. Please log in again.")
            return
        }
        _adminUserState.value = AdminUserState.Loading
        viewModelScope.launch {
            try {
                val token = firebaseUser.getIdToken(true).await().token
                if (token != null) {
                    val users = adminRepo.getUsers(token)
                    _allUsers.value = users
                    _adminUserState.value = AdminUserState.Success
                } else {
                    Log.e("AdminUsersViewModel", "getAllUsers() - Firebase token is null.")
                    _allUsers.value = emptyList()
                    _adminUserState.value = AdminUserState.Error("Authentication token is missing.")
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e(
                    "AdminUsersViewModel",
                    "getAllUsers() - Error fetching users: ${e.message}",
                    e
                )
                if (e is retrofit2.HttpException && e.code() == 401) {
                    _adminUserState.value =
                        AdminUserState.Error("Authentication failed. Please try logging in again.")
                } else if (e is retrofit2.HttpException && e.code() == 403) {
                    _adminUserState.value =
                        AdminUserState.Error("Forbidden: You don't have admin access.")
                } else {
                    _adminUserState.value =
                        AdminUserState.Error(e.message ?: "Failed to load users.")
                }
                _allUsers.value = emptyList()
            }
        }
    }

    fun deleteUser(id: Int) {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            _adminUserState.value =
                AdminUserState.Error("Session expired. Please log in again.")
            return
        }
        _adminUserState.value = AdminUserState.Loading
        viewModelScope.launch {
            try {
                val token = firebaseUser.getIdToken(true).await().token
                if (token != null) {
                    adminRepo.deleteUser(token, id)
                    _adminUserState.value = AdminUserState.Success
                    getAllUsers() // Refresh list after deletion
                } else {
                    Log.e("DeleteFlow", "ViewModel: Firebase token is null for deletion.")
                    _adminUserState.value =
                        AdminUserState.Error("Authentication token is missing for deletion.")
                }
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                Log.e("DeleteFlow", "ViewModel: Error in deleteUser($id): ${e.message}", e)
                if (e is retrofit2.HttpException && e.code() == 401) {
                    _adminUserState.value =
                        AdminUserState.Error("Authentication failed. Please try logging in again.")
                } else if (e is retrofit2.HttpException && e.code() == 403) {
                    _adminUserState.value =
                        AdminUserState.Error("Forbidden: You don't have admin access to delete.")
                } else {
                    _adminUserState.value =
                        AdminUserState.Error(e.message ?: "Failed to delete user.")
                }
            }
        }
    }
}