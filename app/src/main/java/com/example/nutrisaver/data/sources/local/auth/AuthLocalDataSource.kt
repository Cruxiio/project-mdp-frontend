package com.example.nutrisaver.data.sources.local.auth

import com.example.nutrisaver.data.model.Allergen
import com.example.nutrisaver.data.model.User
import com.example.nutrisaver.data.sources.local.dao.UserDao
import com.example.nutrisaver.data.sources.local.entity.AllergenEntity
import com.example.nutrisaver.data.sources.local.entity.UserAllergenCrossRef
import com.example.nutrisaver.data.sources.local.entity.UserEntity
import com.example.nutrisaver.data.sources.local.entity.toUser

interface AuthLocalDataSource {
    suspend fun saveUser(user: User)
    suspend fun getUser(uuid: String): User?
}

class AuthLocalDataSourceImpl(private val userDao: UserDao) : AuthLocalDataSource {

    override suspend fun saveUser(user: User) {
        val userEntity = UserEntity.fromUser(user)
        val allergenEntities = user.allergen.map { AllergenEntity.fromAllergen(it) }

        userDao.insertUser(userEntity)
        if (allergenEntities.isNotEmpty()) {
            userDao.insertAllergens(allergenEntities)
        }
        userDao.deleteCrossRefsForUser(userEntity.uuid)
        if (allergenEntities.isNotEmpty()) {
            val crossRefs = allergenEntities.map {
                UserAllergenCrossRef(uuid = userEntity.uuid, allergenId = it.allergenId)
            }
            userDao.insertCrossRefs(crossRefs)
        }
    }

    override suspend fun getUser(uuid: String): User? {
        // Query 1: Ambil data user dasar
        val userEntity = userDao.getUserByUuid(uuid) ?: return null

        // Query 2: Ambil semua ID alergen yang berelasi dengan user
        val allergenIds = userDao.getAllergenIdsForUser(uuid)

        val allergens: List<Allergen>
        if (allergenIds.isNotEmpty()) {
            // Query 3: Ambil data lengkap alergen berdasarkan ID-nya
            allergens = userDao.getAllergensByIds(allergenIds).map { it.toAllergen() }
        } else {
            allergens = emptyList()
        }

        // Gabungkan userEntity dan list alergen menjadi satu objek User
        return userEntity.toUser(allergens)
    }
}