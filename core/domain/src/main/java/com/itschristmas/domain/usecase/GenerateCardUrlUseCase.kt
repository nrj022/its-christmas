package com.itschristmas.domain.usecase

import android.net.Uri
import javax.inject.Inject

class GenerateCardUrlUseCase @Inject constructor() {

    private val baseCardUrl = "https://its-christmas-1f0ea.web.app"

    operator fun invoke(
        cardTitle: String,
        fileName: String,
        token: String,
        bgFileName: String,
        bgToken: String
    ): String {
        val encodedTitle = Uri.encode(cardTitle)
        return "$baseCardUrl?title=$encodedTitle&glb=$fileName&glbToken=$token&bg=$bgFileName&bgToken=$bgToken"
    }
}
