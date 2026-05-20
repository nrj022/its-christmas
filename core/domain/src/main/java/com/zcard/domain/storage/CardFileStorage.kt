package com.zcard.domain.storage

import java.io.File

interface CardFileStorage {
    suspend fun getThumbnailFile(cardId: Long): File?
    suspend fun deleteThumbnailFile(cardId: Long): Result<Unit>
    suspend fun getGlbFile(fileName: String): File?
    suspend fun deleteGlbFile(fileName: String): Result<Unit>
}
