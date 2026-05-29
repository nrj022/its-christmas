package com.zcard.feature.cardshare

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zcard.designsystem.theme.ZCardTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardShareFragment : Fragment() {

    private val viewModel: CardShareViewModel by viewModels()

    companion object {
        private const val ARG_CARD_URL = "cardUrl"
        fun newInstance(cardUrl: String) =
            CardShareFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_CARD_URL, cardUrl)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val cardUrl = requireArguments().getString(ARG_CARD_URL, "")    // newInstance로만 생성되므로 없으면 즉시 크래시
        require(cardUrl.isNotEmpty()) { "Missing required argument: $ARG_CARD_URL" }

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ZCardTheme {
                    CardShareScreen(cardUrl = cardUrl)
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.sideEffect.collect { sideEffect ->
                    when (sideEffect) {
                        is CardShareSideEffect.NavigateBack -> {
                            parentFragmentManager.popBackStack()
                        }
                        is CardShareSideEffect.NavigateToHome -> {
                            navigationToMain()
                        }
                        is CardShareSideEffect.ShareCardLink -> {
                            shareCardLink(sideEffect.cardUrl)
                        }
                    }
                }
            }
        }

        viewModel.onIntent(CardShareIntent.CheckNetwork)
    }

    private fun navigationToMain() {
        parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
    }

    private fun shareCardLink(cardUrl: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, cardUrl)
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, null))
    }
}