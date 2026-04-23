package com.zcard.domain.storage

import java.io.File

interface CardFileStorage {
    fun getThumbnailFile(cardId: Long): File?
}