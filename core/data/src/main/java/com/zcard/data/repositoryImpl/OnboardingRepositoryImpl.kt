package com.zcard.data.repositoryImpl

import android.content.SharedPreferences
import androidx.core.content.edit
import com.zcard.domain.repository.OnboardingRepository
import javax.inject.Inject

class OnboardingRepositoryImpl @Inject constructor(
    private val prefs: SharedPreferences
) : OnboardingRepository {

    companion object {
        private const val KEY_TUTORIAL_COMPLETED = "tutorial_completed"
    }

    override fun isTutorialCompleted(): Boolean =
        prefs.getBoolean(KEY_TUTORIAL_COMPLETED, false)

    override fun setTutorialCompleted() =
        prefs.edit { putBoolean(KEY_TUTORIAL_COMPLETED, true) }
}
