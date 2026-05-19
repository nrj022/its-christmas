package com.zcard.data.repositoryImpl

import android.net.Uri
import android.util.Log
import com.google.firebase.storage.FirebaseStorage
import com.zcard.data.mapper.toDomain
import com.zcard.data.mapper.toEntity
import com.zcard.data.repositoryImpl.common.ioCatching
import com.zcard.database.dao.CardDao
import com.zcard.domain.analytics.PerformanceTracker
import com.zcard.domain.model.Card
import com.zcard.domain.model.CardPreview
import com.zcard.domain.model.UploadState
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.storage.CardFileStorage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private const val TAG = "CardRepositoryImpl"

class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao,
    private val firebaseStorage: FirebaseStorage,
    private val cardFileStorage: CardFileStorage,
    private val performanceTracker: PerformanceTracker
) : CardRepository {

    override suspend fun insertCard(card: Card): Result<Long> =
        ioCatching {
            cardDao.insertCard(card.toEntity())
        }

    override suspend fun deleteCard(cardId: Long): Result<Int> =
        ioCatching {
            val result = cardDao.deleteCard(cardId)
            if (result > 0) {
                cardFileStorage.deleteThumbnailFile(cardId)
                    .onFailure { e -> Log.w(TAG, "deleteThumbnail failed (cardId=$cardId): $e") }
            } else {
                throw Throwable("Failed to delete card")
            }
            result
        }

    override fun getCardPreviews(): Flow<Result<List<CardPreview>>> =
        cardDao.getAllCards()
            .map { cardList ->
                Result.success(cardList.map { it ->
                    val thumbnail = cardFileStorage.getThumbnailFile(it.cardId)
                    CardPreview(
                        cardId = it.cardId,
                        title = it.title,
                        updatedAt = it.updatedAt,
                        thumbnailFile = thumbnail,
                        thumbnailUpdatedAt = it.thumbnailUpdatedAt
                    )
                })
            }
            .catch { e -> emit(Result.failure(e)) }
            .flowOn(Dispatchers.IO)

    override suspend fun getCardById(cardId: Long): Result<Card> =
        ioCatching {
            cardDao.getCardById(cardId).toDomain()
        }

    override suspend fun updateCardThumbnail(cardId: Long): Result<Int> =
        ioCatching {
            cardDao.refreshThumbnailUpdatedAt(cardId)
        }

    override suspend fun updateCardTitle(cardId: Long, title: String): Result<Int> =
        ioCatching {
            cardDao.updateTitle(cardId, title)
        }

    override suspend fun updateBackgroundAssetId(cardId: Long, backgroundAssetId: Long): Result<Int> =
        ioCatching {
            cardDao.updateBackgroundAssetId(cardId, backgroundAssetId)
        }

    override suspend fun updateGlb(cardId: Long, glbFileName: String): Result<Int> =
        ioCatching {
            cardDao.updateGlb(cardId, glbFileName)
        }

    override suspend fun deleteCardGlb(fileName: String): Result<Unit> =
            cardFileStorage.deleteGlbFile(fileName)

    override suspend fun uploadGlbToFirebase(fileName: String): Flow<UploadState> {
        val file = cardFileStorage.getGlbFile(fileName)
            ?: return flowOf(UploadState.Failure(Throwable("File not found")))

        return callbackFlow {
            val fileSizeKb = file.length() / 1024
            val startTime = System.currentTimeMillis()

            val traceId = performanceTracker.startTrace("card_glb_upload")
            performanceTracker.putMetric(traceId, "file_size_kb", fileSizeKb)
            performanceTracker.putAttribute(traceId, "size_bucket", when {
                fileSizeKb < 1024 -> "small"
                fileSizeKb < 5120 -> "medium"
                else              -> "large"
            })

            val storageRef = firebaseStorage.reference
            val uploadRef = storageRef.child("models/$fileName")
            val uploadTask = uploadRef.putFile(Uri.fromFile(file))

            uploadTask.addOnProgressListener {
                val percent = ((100.0 * it.bytesTransferred) / it.totalByteCount).toInt()
                trySend(UploadState.Progress(percent))
            }

            uploadTask.addOnSuccessListener {
                val durationSec = (System.currentTimeMillis() - startTime) / 1000.0
                val speedKbps = if (durationSec > 0) (fileSizeKb / durationSec).toLong() else 0L
                performanceTracker.putMetric(traceId, "upload_speed_kb_per_s", speedKbps)
                performanceTracker.stopTrace(traceId)
                trySend(UploadState.Success)
                close()
            }

            uploadTask.addOnFailureListener { e ->
                performanceTracker.stopTrace(traceId, false, e.message?.take(100))
                trySend(UploadState.Failure(e))
                close()
            }

            awaitClose {
                uploadTask.cancel()

                if (!uploadTask.isComplete) {
                    performanceTracker.stopTrace(traceId, false, "Cancelled by user/system")
                }
            }
        }
    }
}