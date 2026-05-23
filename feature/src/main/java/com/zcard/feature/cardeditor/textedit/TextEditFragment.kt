package com.zcard.feature.cardeditor.textedit

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zcard.feature.cardeditor.ui.CardEditorBottomScreen
import com.zcard.feature.cardeditor.ui.CardEditorTextScreen
import com.zcard.feature.R
import com.zcard.designsystem.theme.ZCardTheme
import com.zcard.feature.MainIntent
import com.zcard.feature.MainViewModel
import com.zcard.feature.databinding.FragmentTextEditBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class TextEditFragment : Fragment() {

    private lateinit var binding: FragmentTextEditBinding
    private lateinit var layoutParams: ConstraintLayout.LayoutParams

    private val viewModel: TextEditViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private var cardId: Long = -1L

    companion object {
        private const val ARG_CARD_ID = "cardId"

        fun newInstance(cardId: Long) =
            TextEditFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CARD_ID, cardId)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.onIntent(MainIntent.OnEditorCreated)

        cardId = requireArguments().getLong(ARG_CARD_ID)    // newInstance로만 생성되므로 없으면 즉시 크래시
        viewModel.onIntent(TextEditIntent.Init(cardId))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTextEditBinding.inflate(inflater, container, false)
        layoutParams = binding.unityContainer.layoutParams as ConstraintLayout.LayoutParams

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val imeVisible = windowInsets.isVisible(WindowInsetsCompat.Type.ime())
            val navInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())

            viewModel.setImeVisible(imeVisible)
            v.setPadding(0, 0, 0, navInsets.bottom)

            windowInsets
        }

        initListener()

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Toast.makeText(requireContext(), getString(R.string.editor_msg_block_system_back), Toast.LENGTH_SHORT).show()
            }
        })

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.textEditSideEffect.collect { sideEffect ->
                        when (sideEffect) {
                            is TextEditSideEffect.Finish -> { parentFragmentManager.popBackStack() }
                            is TextEditSideEffect.ToastMessage -> {
                                Toast.makeText(requireContext(), getString(sideEffect.msgRes),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                launch {
                    viewModel.unityContainerHeightFractionFlow.collect {
                        mainViewModel.onIntent(MainIntent.OnUnityContainerHeightChanged(it))
                        updateUnityContainerHeight(it)
                    }
                }
            }
        }

        binding.composeContainerText.setContent {
            ZCardTheme { CardEditorTextScreen() }
        }

        binding.composeContainer.setContent {
            ZCardTheme { CardEditorBottomScreen() }
        }

        return binding.root
    }

    override fun onDestroy() {
        mainViewModel.onIntent(MainIntent.OnEditorDestroyed(cardId))
        super.onDestroy()
    }

    private fun initListener() {
        binding.imgBtnAddText.setOnClickListener {
            viewModel.onIntent(TextEditIntent.AddText)
        }

        binding.imgBtnDeleteText.setOnClickListener {
            val textId = viewModel.textEditState.value.selectedTextTempId ?: return@setOnClickListener
            viewModel.onIntent(TextEditIntent.DeleteText(textId))
        }

        binding.imgBtnCameraFocusReset.setOnClickListener {
            viewModel.onIntent(TextEditIntent.ResetCamera)
        }
    }

    private fun updateUnityContainerHeight(heightFraction: Float) {
        if(layoutParams.matchConstraintPercentHeight != heightFraction) {
            layoutParams.matchConstraintPercentHeight = heightFraction
            binding.unityContainer.layoutParams = layoutParams
        }
    }
}