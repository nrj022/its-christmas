package com.zcard.domain.repository

interface FeedbackRepository {
    suspend fun submitFeedback(text: String): Result<Unit>
}