package com.zcard.domain.repository

interface OnboardingRepository {
    fun isTutorialCompleted(): Boolean
    fun setTutorialCompleted()
}
