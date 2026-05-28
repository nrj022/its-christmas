package com.zcard.feature.cardeditor

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context.CLIPBOARD_SERVICE
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.compose.runtime.mutableStateOf
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zcard.feature.R
import com.zcard.feature.cardshare.CardShareFragment
import com.zcard.feature.databinding.FragmentCardEditorBinding
import com.zcard.designsystem.theme.ZCardTheme
import com.zcard.feature.MainIntent
import com.zcard.feature.MainViewModel
import com.zcard.feature.cardeditor.textedit.TextEditFragment
import com.zcard.feature.cardeditor.transform.TransformFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class CardEditorFragment : Fragment() {

    private lateinit var binding: FragmentCardEditorBinding
    private lateinit var layoutParams: ConstraintLayout.LayoutParams

    private val viewModel: CardEditorViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    private val loadingOverlayVisible = mutableStateOf(true)
    private val loadingText = mutableStateOf("Loading")
    private var cardId: Long = -1L

    companion object {
        private const val ARG_CARD_ID = "cardId"
        private const val UNITY_HEIGHT_RATIO = 0.52f

        fun newInstance(cardId: Long) =
            CardEditorFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CARD_ID, cardId)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainViewModel.onIntent(MainIntent.OnEditorCreated)

        lifecycleScope.launch {
            mainViewModel.unityMessage.collect {
                viewModel.onIntent(CardEditorIntent.OnUnityMessage(it))
            }
        }

        cardId = requireArguments().getLong(ARG_CARD_ID)    // newInstance로만 생성되므로 없으면 즉시 크래시
        viewModel.onIntent(CardEditorIntent.Init(cardId))
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCardEditorBinding.inflate(inflater, container, false)
        layoutParams = binding.unityContainer.layoutParams as ConstraintLayout.LayoutParams

        val initialTopPadding = binding.containerTop.paddingTop

        mainViewModel.onIntent(MainIntent.OnUnityContainerHeightChanged(UNITY_HEIGHT_RATIO))

        ViewCompat.setOnApplyWindowInsetsListener(binding.containerTop) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.statusBars())
            v.setPadding(0, initialTopPadding + insets.top, 0, 0)
            windowInsets
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val navInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
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
                    viewModel.cardEditorState.collect {
                        updateUi(it)
                    }
                }
                launch {
                    viewModel.cardEditorSideEffect.collect { sideEffect ->
                        when (sideEffect) {
                            is CardEditorSideEffect.NavigateToCardShare -> {
                                navigateToCardShare(sideEffect.cardUrl)
                            }
                            is CardEditorSideEffect.NavigateToTransform -> {
                                navigateToTransform(sideEffect.elementId)
                            }
                            is CardEditorSideEffect.NavigateToTextEdit -> {
                                navigateToTextEdit()
                            }
                            is CardEditorSideEffect.Finish -> { parentFragmentManager.popBackStack() }
                            is CardEditorSideEffect.ToastMessage -> {
                                Toast.makeText(requireContext(), getString(sideEffect.msgRes),Toast.LENGTH_SHORT).show()
                            }
                            is CardEditorSideEffect.CopyCardLink -> {
                                val clipboard = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("", sideEffect.cardUrl))
                                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                                    Toast.makeText(requireContext(), getString(R.string.editor_msg_copy_success),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

        binding.composeContainer.setContent {
            ZCardTheme { CardEditorScreen(viewModel) }
        }

        binding.composeLoading.setContent {
            ZCardTheme {
                if (loadingOverlayVisible.value) {
                    CardEditorLoadingOverlay(text = loadingText.value)
                }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        mainViewModel.onIntent(MainIntent.OnEditorDestroyed(cardId))
        super.onDestroy()
    }

    private fun initListener() {
        binding.imgBtnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }

        binding.imgBtnLink.setOnClickListener {
            viewModel.onIntent(CardEditorIntent.OpenCardLinkDetail)
        }

        binding.btnComplete.setOnClickListener {
            viewModel.onIntent(CardEditorIntent.FinishEditing)
        }

        binding.btnAdjust.setOnClickListener {
            viewModel.onIntent(CardEditorIntent.EnterTransformMode)
        }

        binding.imgBtnDelete.setOnClickListener {
            viewModel.onIntent(CardEditorIntent.ChangeDialogState(CardEditorState.DialogState.DELETE_CONFIRM))
        }
    }

    private fun updateUi(state: CardEditorState) {
        binding.imgBtnLink.isVisible = state.showLinkDetailButton
        binding.containerObjectOption.isVisible = state.showObjectOptionContainer
        loadingOverlayVisible.value = state.isLoading
        loadingText.value = state.loadingText
        binding.frameLoading.isVisible = state.isLoading
    }

    private fun navigateToCardShare(cardUrl: String) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(
                R.anim.slide_in_right,
                R.anim.slide_out_left,
                R.anim.slide_in_left,
                R.anim.slide_out_right
            )
            .replace(R.id.fragment_container, CardShareFragment.newInstance(cardUrl))
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToTransform(elementId: Long) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, TransformFragment.newInstance(elementId))
            .addToBackStack(null)
            .commit()
    }

    private fun navigateToTextEdit() {
        parentFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, TextEditFragment.newInstance(cardId))
            .addToBackStack(null)
            .commit()
    }
}