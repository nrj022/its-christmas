package com.zcard.feature.tutorial

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.view.WindowCompat
import com.zcard.designsystem.theme.ZCardTheme
import com.zcard.feature.R
import com.zcard.feature.home.HomeFragment

class TutorialFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ZCardTheme {
                    TutorialScreen(::navigateToHome)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        WindowCompat.getInsetsController(requireActivity().window, requireView())
            .isAppearanceLightStatusBars = false  // false = 아이콘 흰색 (어두운 배경용)
    }

    override fun onDestroyView() {
        WindowCompat.getInsetsController(requireActivity().window, requireView())
            .isAppearanceLightStatusBars = true  // 복구

        super.onDestroyView()
    }

    private fun navigateToHome() {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.fragment_container, HomeFragment())
            .commit()
    }
}