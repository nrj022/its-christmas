package com.zcard.domain.repository

interface AuthRepository {

    suspend fun signInAnonymously(): Result<Unit>
}