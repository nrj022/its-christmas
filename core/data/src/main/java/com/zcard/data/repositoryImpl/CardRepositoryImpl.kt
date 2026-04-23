package com.zcard.data.repositoryImpl

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.zcard.data.mapper.toDomain
import com.zcard.data.mapper.toEntity
import com.zcard.data.repositoryImpl.common.ioCatching
import com.zcard.database.dao.CardDao
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
import java.io.File
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao,
    private val firebaseStorage: FirebaseStorage,
    private val cardFileStorage: CardFileStorage
) : CardRepository {

    override suspend fun insertCard(card: Card): Result<Long> =
        ioCatching {
            cardDao.insertCard(card.toEntity())
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
                        thumbnailFile = thumbnail
                    )
                })
            }
            .catch { e -> emit(Result.failure(e)) }
            .flowOn(Dispatchers.IO)

    override suspend fun getCardById(cardId: Long): Result<Card> =
        ioCatching {
            cardDao.getCardById(cardId).toDomain()
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

    override suspend fun uploadGlbToFirebase(fileName: String): Flow<UploadState> {
        val file = cardFileStorage.getGlbFile(fileName)
            ?: return flowOf(UploadState.Failure(Throwable("File not found")))

        return callbackFlow {

            val storageRef = firebaseStorage.reference
            val uploadRef = storageRef.child("models/$fileName")

            val uploadTask = uploadRef.putFile(Uri.fromFile(file))

            uploadTask.addOnProgressListener {
                val percent = ((100.0 * it.bytesTransferred) / it.totalByteCount).toInt()
                trySend(UploadState.Progress(percent))
            }

            uploadTask.addOnSuccessListener {
                trySend(UploadState.Success)
                close()
            }

            uploadTask.addOnFailureListener { e ->
                trySend(UploadState.Failure(e))
                close()
            }

            awaitClose {
                uploadTask.cancel()
            }
        }
    }
}