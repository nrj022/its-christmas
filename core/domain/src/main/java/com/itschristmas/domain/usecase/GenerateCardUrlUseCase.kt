package com.itschristmas.domain.usecase

import android.net.Uri

class GenerateCardUrlUseCase {

    private val baseCardUrl = "https://its-christmas-1f0ea.web.app"

    operator fun invoke(
        cardTitle: String,
        fileName: String,
        token: String,
        bgFileName: String,
        bgToken: String
    ): String {
        return "$baseCardUrl?title=$cardTitle&glb=$fileName&glbToken=$token&bg=$bgFileName&bgToken=$bgToken"
    }
}
