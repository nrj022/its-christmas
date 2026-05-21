package com.zcard.feature.tutorial

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.WindowCompat
import com.zcard.designsystem.theme.ZCardTheme
import com.zcard.feature.MainIntent
import com.zcard.feature.MainViewModel

class TutorialFragment : Fragment() {

    private val viewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ZCardTheme {
                    TutorialScreen(
                        onStart = { viewModel.onIntent(MainIntent.CompleteTutorial) })
                }
            }
        }
    }
}
