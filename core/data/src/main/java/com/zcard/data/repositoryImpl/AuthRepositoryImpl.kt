package com.zcard.data.repositoryImpl

import com.google.firebase.auth.FirebaseAuth
import com.zcard.data.repositoryImpl.common.ioCatching
import com.zcard.domain.repository.AuthRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) : AuthRepository {

    override suspend fun signInAnonymously(): Result<Unit> =
        ioCatching {
            if (firebaseAuth.currentUser != null) return@ioCatching

            firebaseAuth.signInAnonymously().await()
        }
}