package com.zcard.data.storageimpl

import android.content.Context
import com.zcard.domain.storage.CardFileStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class CardFileStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CardFileStorage {

    companion object {
        private const val GLB_FILE_PATH = "glb_exports/"
        private fun getThumbFileName(cardId: Long): String =
            "thumbnails/thumb_card_${cardId}.jpg"
    }

    override suspend fun getThumbnailFile(cardId: Long): File? = withContext(Dispatchers.IO) {
        File(context.filesDir, getThumbFileName(cardId)).takeIf { it.exists() }
    }

    override suspend fun getGlbFile(fileName: String): File? = withContext(Dispatchers.IO) {
        val externalDir = context.getExternalFilesDir(null) ?: return@withContext null
        File(externalDir, "$GLB_FILE_PATH$fileName").takeIf { it.exists() }
    }

    override suspend fun deleteGlbFile(fileName: String): Result<Unit> = withContext(Dispatchers.IO) {
        val externalDir = context.getExternalFilesDir(null) ?: return@withContext Result.failure(Throwable("External directory not found"))
        if(File(externalDir, "$GLB_FILE_PATH$fileName").delete()) {
            Result.success(Unit)
        } else {
            Result.failure(Throwable("Failed to delete file"))
        }
    }
}
