package com.example.bookxpert.domain.repository

import com.example.bookxpert.data.local.dao.UserDao
import com.example.bookxpert.domain.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val userDao: UserDao
) {
    suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: return Result.failure(Exception("User not found"))

            val user = User(
                id = firebaseUser.uid,
                name = firebaseUser.displayName,
                email = firebaseUser.email,
                photoUrl = firebaseUser.photoUrl?.toString(),
                isLoggedIn = true
            )

            userDao.insertUser(user)
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signOut() {
        firebaseAuth.signOut()
        firebaseAuth.currentUser?.uid?.let { userId ->
            userDao.deleteUser(userId)
        }
    }

    fun getCurrentUser(userId: String): Flow<User?> {
        return userDao.getUser(userId)
    }
}