package com.itschristmas.data.mapper

import com.itschristmas.database.dto.GlbAndBgFirebaseKeyDto
import com.itschristmas.domain.model.GlbAndBgFirebaseKey


fun GlbAndBgFirebaseKeyDto.toDomain(): GlbAndBgFirebaseKey {
    return GlbAndBgFirebaseKey(
        glbKey = glbKey,
        backgroundKey = backgroundKey
    )
}