package com.example.nutrisaver.data.sources.remote.admin

import android.util.Log
import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.sources.remote.Webservice

class AdminDataSourceImpl(
    private val webservice: Webservice
): AdminDataSource {
    override suspend fun getUsers(): List<User> {
        val usersJson = webservice.getUsers()
        return usersJson.map{ it ->
            User.fromUserJson(it)!!
        }
    }

    override suspend fun deleteUser(id: Int) {
        try {
            webservice.deleteUser(id.toString())
        } catch (e: Exception) {
            throw e // Re-throw to be caught by ViewModel
        }
    }

}