package com.zcard.domain.storage

import java.io.File

interface CardFileStorage {
    fun getThumbnailFile(cardId: Long): File?
    fun getGlbFile(fileName: String): File?
    fun deleteGlbFile(fileName: String): Boolean
}