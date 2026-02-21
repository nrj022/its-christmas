package com.zcard.data.mapper

import com.zcard.database.dto.GlbAndBgFirebaseDto
import com.zcard.domain.model.GlbAndBgFirebase


fun GlbAndBgFirebaseDto.toDomain(): GlbAndBgFirebase {
    return GlbAndBgFirebase(
        glbFileName = glbFileName,
        glbToken = glbToken,
        backgroundFileName = backgroundFileName,
        backgroundToken = backgroundToken
    )
}