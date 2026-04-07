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
import com.zcard.feature.R
import com.zcard.feature.home.HomeFragment
import com.zcard.designsystem.theme.ZCardTheme
import kotlin.apply

class CardShareFragment : Fragment() {

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
        val cardUrl = arguments?.getString(ARG_CARD_URL) ?: ""

        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                ZCardTheme {
                    CardShareScreen(
                        cardUrl = cardUrl,
                        onBackClicked = { parentFragmentManager.popBackStack() },
                        onHomeClicked = ::navigationToMain,
                        onShareClicked = { shareCardLink(cardUrl) }
                    )
                }
            }
        }
    }

    private fun navigationToMain() {
        parentFragmentManager.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE)
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .commit()
    }

    private fun shareCardLink(cardUrl: String) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.putExtra(Intent.EXTRA_TEXT, cardUrl)
        intent.type = "text/plain"
        startActivity(Intent.createChooser(intent, null))
    }
}