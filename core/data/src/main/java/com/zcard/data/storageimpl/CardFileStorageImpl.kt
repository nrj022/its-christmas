package com.zcard.data.storageimpl

import android.content.Context
import com.zcard.domain.storage.CardFileStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class CardFileStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CardFileStorage {

    companion object {
        const val GLB_FILE_PATH = "glb_exports/"
        fun getThumbFileName(cardId: Long): String =
            "thumbnails/thumb_card_${cardId}.jpg"
    }
    override fun getThumbnailFile(cardId: Long): File? {
        return File(context.filesDir, getThumbFileName(cardId)).takeIf { it.exists() }
    }

    override fun getGlbFile(fileName: String): File? {
        val externalDir = context.getExternalFilesDir(null) ?: return null
        return File(externalDir, "$GLB_FILE_PATH$fileName").takeIf { it.exists() }
    }

    override fun deleteGlbFile(fileName: String): Boolean {
        val externalDir = context.getExternalFilesDir(null) ?: return false
        return File(externalDir, "$GLB_FILE_PATH$fileName").delete()
    }
}