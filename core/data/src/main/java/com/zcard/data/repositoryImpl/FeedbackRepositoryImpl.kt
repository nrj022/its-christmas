package com.zcard.data.repositoryImpl

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.zcard.domain.repository.FeedbackRepository
import javax.inject.Inject

private const val TAG = "FeedbackRepositoryImpl"

class FeedbackRepositoryImpl @Inject constructor(
    private val firebaseFirestore: FirebaseFirestore
) : FeedbackRepository {

    companion object {
        private const val COLLECTION_FEEDBACKS = "feedbacks"
        private const val FIELD_TIMESTAMP = "timestamp"
        private const val FIELD_FEEDBACK = "feedback"
    }

    override fun submitFeedback(text: String) {
        val feedback = mapOf(
            FIELD_FEEDBACK to text,
            FIELD_TIMESTAMP to System.currentTimeMillis()
        )
        firebaseFirestore.collection(COLLECTION_FEEDBACKS).add(feedback)
        Log.d(TAG, "Feedback queued for submission")
    }
}