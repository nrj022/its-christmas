package com.zcard.card.cardeditor.model

enum class Direction(val dx: Float, val dy: Float, val dz: Float) {
    UP(0f, .1f, 0f),
    DOWN(0f, -.1f, 0f),
    LEFT(-.1f, 0f, 0f),
    RIGHT(.1f, 0f, 0f),
    FORWARD(0f, 0f, -.1f),
    BACKWARD(0f, 0f, .1f)
}