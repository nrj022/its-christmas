package com.itschristmas.data.mapper

import com.itschristmas.database.dto.GlbAndBgFirebaseDto
import com.itschristmas.domain.model.GlbAndBgFirebase


fun GlbAndBgFirebaseDto.toDomain(): GlbAndBgFirebase {
    return GlbAndBgFirebase(
        glbFileName = glbFileName,
        glbToken = glbToken,
        backgroundFileName = backgroundFileName,
        backgroundToken = backgroundToken
    )
}