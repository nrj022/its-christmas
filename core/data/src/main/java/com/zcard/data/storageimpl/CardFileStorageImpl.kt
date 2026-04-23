package com.zcard.data.storageimpl

import android.content.Context
import com.zcard.domain.storage.CardFileStorage
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject

class CardFileStorageImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : CardFileStorage {
    override fun getThumbnailFile(cardId: Long): File? {
        val fileName = "thumb_card_${cardId}.jpg"
        return File(context.filesDir, fileName).takeIf { it.exists() }
    }
}