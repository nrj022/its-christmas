package com.zcard.feature.setting

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zcard.designsystem.theme.ZCardTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.core.net.toUri
import com.zcard.feature.R

private const val CREDITS_URL = "https://www.notion.so/3D-Asset-Attributions-365db7d5b54781dcb929ed1eab7eaef5?source=copy_link"

@AndroidEntryPoint
class SettingFragment : Fragment() {

    private val viewModel: SettingViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ZCardTheme {
                    SettingScreen()
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.settingSideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        is SettingSideEffect.NavigateBack -> {
                            parentFragmentManager.popBackStack()
                        }
                        is SettingSideEffect.ToastMessage -> {
                            Toast.makeText(requireContext(), getString(sideEffect.msgRes), Toast.LENGTH_SHORT).show()
                        }
                        is SettingSideEffect.OpenCreditsDocs -> {
                            openCreditsDocs()
                        }
                    }
                }
            }
        }
    }

    private fun openCreditsDocs() {
        try {
            val intent = Intent(Intent.ACTION_VIEW, CREDITS_URL.toUri())
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(requireContext(), getString(R.string.setting_msg_fail_open_link), Toast.LENGTH_SHORT).show()
        }
    }
}
