package com.example.nutrisaver.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.repositories.AdminRepo
import kotlinx.coroutines.launch

class AdminUsersViewModel (
    private val adminRepo: AdminRepo,
    application: Application
): AndroidViewModel(application) {

    private val _allUsers = MutableLiveData<List<User?>>()
    val allUsers: LiveData<List<User?>> = _allUsers

    fun init() {
        getAllUsers()
    }

    fun getAllUsers() {
        viewModelScope.launch {
            try {
                val users = adminRepo.getUsers()
                _allUsers.value = users
            } catch (e: Exception) {
                Log.e("AdminUsersViewModel", "getAllUsers() - Error fetching users: ${e.message}", e)
                _allUsers.value = emptyList() // Ensure UI is updated to empty on error
            }
        }
    }

    fun deleteUser(id: Int) {
        viewModelScope.launch {
            try {
                adminRepo.deleteUser(id)
                getAllUsers()
            } catch (e: Exception) {
                Log.e("DeleteFlow", "ViewModel: Error in deleteUser($id): ${e.message}", e)
            }
        }
    }

}