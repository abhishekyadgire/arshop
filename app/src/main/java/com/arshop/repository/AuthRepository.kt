package com.arshop.repository

import com.arshop.data.model.User
import com.arshop.util.Result
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun signInWithEmail(email: String, password: String): Flow<Result<User>>
    fun signUpWithEmail(email: String, password: String, displayName: String): Flow<Result<User>>
    fun signInWithGoogle(idToken: String): Flow<Result<User>>
    fun signOut(): Flow<Result<Unit>>
    fun getCurrentUser(): Flow<User?>
    fun resetPassword(email: String): Flow<Result<Unit>>
    fun updateProfile(displayName: String?, photoUrl: String?): Flow<Result<User>>
}
