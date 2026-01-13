package com.arshop.repository

import com.arshop.data.model.User
import com.arshop.data.remote.FirestoreCollections
import com.arshop.di.IoDispatcher
import com.arshop.util.Result
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepositoryImpl @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher
) : AuthRepository {

    override fun signInWithEmail(email: String, password: String): Flow<Result<User>> = flow {
        try {
            emit(Result.Loading)

            // Validate inputs
            if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                throw Exception("Please enter a valid email address")
            }
            if (password.length < 6) {
                throw Exception("Password must be at least 6 characters")
            }

            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Authentication failed")

            // Fetch or create user document
            val userDoc = firestore.collection(FirestoreCollections.USERS)
                .document(firebaseUser.uid)
                .get()
                .await()

            val user = if (userDoc.exists()) {
                User.fromFirestore(userDoc) ?: throw Exception("Failed to load user data")
            } else {
                // Create user document if doesn't exist
                val newUser = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: email,
                    displayName = firebaseUser.displayName,
                    photoUrl = firebaseUser.photoURL?.toString(),
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )
                firestore.collection(FirestoreCollections.USERS)
                    .document(firebaseUser.uid)
                    .set(newUser.toMap())
                    .await()
                newUser
            }

            emit(Result.Success(user))
        } catch (e: Exception) {
            emit(Result.Failure(Exception(mapFirebaseError(e))))
        }
    }.flowOn(ioDispatcher)

    override fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Flow<Result<User>> = flow {
        try {
            emit(Result.Loading)

            // Validate inputs
            if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                throw Exception("Please enter a valid email address")
            }
            if (password.length < 6) {
                throw Exception("Password must be at least 6 characters")
            }
            if (displayName.isBlank() || displayName.length < 2) {
                throw Exception("Display name must be at least 2 characters")
            }

            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw Exception("Failed to create account")

            try {
                // Update profile with display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                firebaseUser.updateProfile(profileUpdates).await()

                // Create user document in Firestore
                val user = User(
                    id = firebaseUser.uid,
                    email = email,
                    displayName = displayName,
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )

                firestore.collection(FirestoreCollections.USERS)
                    .document(firebaseUser.uid)
                    .set(user.toMap())
                    .await()

                emit(Result.Success(user))
            } catch (e: Exception) {
                // Rollback: delete auth user if Firestore operation fails
                firebaseUser.delete().await()
                throw e
            }
        } catch (e: Exception) {
            emit(Result.Failure(Exception(mapFirebaseError(e))))
        }
    }.flowOn(ioDispatcher)

    override fun signInWithGoogle(idToken: String): Flow<Result<User>> = flow {
        try {
            emit(Result.Loading)

            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val authResult = auth.signInWithCredential(credential).await()
            val firebaseUser = authResult.user ?: throw Exception("Google Sign-In failed")

            // Check if user document exists
            val userDoc = firestore.collection(FirestoreCollections.USERS)
                .document(firebaseUser.uid)
                .get()
                .await()

            val user = if (userDoc.exists()) {
                User.fromFirestore(userDoc) ?: throw Exception("Failed to load user data")
            } else {
                // Create new user document
                val newUser = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    displayName = firebaseUser.displayName,
                    photoUrl = firebaseUser.photoURL?.toString(),
                    createdAt = Timestamp.now(),
                    updatedAt = Timestamp.now()
                )
                firestore.collection(FirestoreCollections.USERS)
                    .document(firebaseUser.uid)
                    .set(newUser.toMap())
                    .await()
                newUser
            }

            emit(Result.Success(user))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Google Sign-In failed: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun signOut(): Flow<Result<Unit>> = flow {
        try {
            auth.signOut()
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }.flowOn(ioDispatcher)

    override fun getCurrentUser(): Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                // Fetch user from Firestore
                firestore.collection(FirestoreCollections.USERS)
                    .document(firebaseUser.uid)
                    .get()
                    .addOnSuccessListener { doc ->
                        val user = User.fromFirestore(doc)
                        trySend(user)
                    }
                    .addOnFailureListener {
                        trySend(null)
                    }
            } else {
                trySend(null)
            }
        }

        auth.addAuthStateListener(listener)
        awaitClose { auth.removeAuthStateListener(listener) }
    }.flowOn(ioDispatcher)

    override fun resetPassword(email: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Loading)

            if (email.isBlank() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                throw Exception("Please enter a valid email address")
            }

            auth.sendPasswordResetEmail(email).await()
            emit(Result.Success(Unit))
        } catch (e: Exception) {
            emit(Result.Failure(Exception("Failed to send reset email: ${e.message}")))
        }
    }.flowOn(ioDispatcher)

    override fun updateProfile(displayName: String?, photoUrl: String?): Flow<Result<User>> = flow {
        try {
            emit(Result.Loading)

            val firebaseUser = auth.currentUser ?: throw Exception("No user signed in")

            val profileUpdates = UserProfileChangeRequest.Builder()
                .apply {
                    displayName?.let { setDisplayName(it) }
                    photoUrl?.let { setPhotoUri(android.net.Uri.parse(it)) }
                }
                .build()

            firebaseUser.updateProfile(profileUpdates).await()

            // Update Firestore
            val updates = mutableMapOf<String, Any>("updatedAt" to Timestamp.now())
            displayName?.let { updates["displayName"] = it }
            photoUrl?.let { updates["photoUrl"] = it }

            firestore.collection(FirestoreCollections.USERS)
                .document(firebaseUser.uid)
                .update(updates)
                .await()

            // Fetch updated user
            val userDoc = firestore.collection(FirestoreCollections.USERS)
                .document(firebaseUser.uid)
                .get()
                .await()

            val user = User.fromFirestore(userDoc) ?: throw Exception("Failed to load updated user")
            emit(Result.Success(user))
        } catch (e: Exception) {
            emit(Result.Failure(e))
        }
    }.flowOn(ioDispatcher)

    private fun mapFirebaseError(exception: Exception): String {
        return when {
            exception.message?.contains("network", ignoreCase = true) == true ->
                "Please check your internet connection"
            exception.message?.contains("email", ignoreCase = true) == true &&
                exception.message?.contains("use", ignoreCase = true) == true ->
                "This email is already registered"
            exception.message?.contains("password", ignoreCase = true) == true &&
                exception.message?.contains("wrong", ignoreCase = true) == true ->
                "Incorrect email or password"
            exception.message?.contains("user", ignoreCase = true) == true &&
                exception.message?.contains("not found", ignoreCase = true) == true ->
                "No account found with this email"
            else -> exception.message ?: "Something went wrong. Please try again"
        }
    }
}
