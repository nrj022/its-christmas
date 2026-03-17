package com.zcard.data.repositoryImpl

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import com.zcard.data.mapper.toDomain
import com.zcard.data.mapper.toEntity
import com.zcard.data.repositoryImpl.common.ioCatching
import com.zcard.database.dao.CardDao
import com.zcard.domain.model.Card
import com.zcard.domain.model.UploadState
import com.zcard.domain.repository.CardRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import java.io.File
import javax.inject.Inject

class CardRepositoryImpl @Inject constructor(
    private val cardDao: CardDao,
    private val firebaseStorage: FirebaseStorage,
) : CardRepository {

    override suspend fun insertCard(card: Card): Result<Long> =
        ioCatching {
            cardDao.insertCard(card.toEntity())
        }

    override suspend fun getAllCards(): Result<List<Card>> =
        ioCatching {
            cardDao.getAllCards().map { it.toDomain() }
        }

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

    override suspend fun updateGlb(cardId: Long, glbFileName: String, glbToken: String): Result<Int> =
        ioCatching {
            cardDao.updateGlb(cardId, glbFileName, glbToken)
        }

    override suspend fun uploadGlbToFirebase(file: File): Flow<UploadState> = callbackFlow {
        val storageRef = firebaseStorage.reference
        val uploadRef = storageRef.child("models/${file.name}")

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
            trySend(UploadState.Failure)
            close(e)
        }

        awaitClose {
            uploadTask.cancel()
        }
    }
}