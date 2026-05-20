package com.zcard.domain.repository

interface FeedbackRepository {
    fun submitFeedback(text: String)
}