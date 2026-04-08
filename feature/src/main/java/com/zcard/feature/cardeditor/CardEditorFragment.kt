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
import com.zcard.feature.cardeditor.model.DialogState
import com.zcard.feature.cardeditor.ui.CardEditorBottomScreen
import com.zcard.feature.cardeditor.ui.CardEditorTextScreen
import com.zcard.feature.cardeditor.ui.common.BouncingLogoLoadingOverlay
import com.zcard.feature.cardeditor.util.getObjectThumbByKey
import com.zcard.feature.cardeditor.util.toBase62
import com.zcard.feature.R
import com.zcard.feature.cardshare.CardShareFragment
import com.zcard.feature.databinding.FragmentCardEditorBinding
import com.zcard.designsystem.theme.ZCardTheme
import com.zcard.domain.model.UnityMessage
import com.zcard.domain.model.UnityEventType
import com.zcard.feature.MainSideEffect
import com.zcard.feature.MainViewModel
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

    companion object {
        private const val ARG_CARD_ID = "cardId"

        fun newInstance(cardId: Long) =
            CardEditorFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CARD_ID, cardId)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCardEditorBinding.inflate(inflater, container, false)
        layoutParams = binding.unityContainer.layoutParams as ConstraintLayout.LayoutParams

        val cardId = arguments?.getLong(ARG_CARD_ID) ?: -1  // TODO: 새로운 카드 생성이 아닌 카드 조회 시 Card ID 누락에 대한 에러 처리 추가 (Log, Dialog)
        viewModel.onIntent(CardEditorIntent.Init(cardId))
        mainViewModel.emitSideEffect(MainSideEffect.ResumeUnity)

        initListener()

        requireActivity().onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
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
                    viewModel.cardEditorSideEffect.collect {
                        when (it) {
                            is CardEditorSideEffect.NavigateToCardShare -> {
                                navigateToCardShare(it.cardUrl)
                            }
                            is CardEditorSideEffect.Finish -> { parentFragmentManager.popBackStack() }
                            is CardEditorSideEffect.ShowToast -> {
                                Toast.makeText(requireContext(),it.message,Toast.LENGTH_SHORT).show()
                            }
                            is CardEditorSideEffect.CopyCardLink -> {
                                val clipboard = requireContext().getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("", it.cardUrl))
                                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2)
                                    Toast.makeText(requireContext(), getString(R.string.editor_msg_copy_success),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
                launch {
                    viewModel.unityContainerHeightFractionFlow.collect {
                        mainViewModel.emitUnityContainerHeightFraction(it)
                        updateUnityContainerHeight(it)
                    }
                }
                launch {
                    mainViewModel.mainSideEffect.collect {
                        if(it is MainSideEffect.ReceivedUnityMessage) {
                            handleUnityMessage(it.message)
                        }
                    }
                }
            }
        }

        binding.composeContainerText.setContent {
            ZCardTheme { CardEditorTextScreen(viewModel) }
        }

        binding.composeContainer.setContent {
            ZCardTheme { CardEditorBottomScreen(viewModel) }
        }

        binding.composeLoading.setContent {
            ZCardTheme {
                if (loadingOverlayVisible.value) {
                    BouncingLogoLoadingOverlay(text = loadingText.value)
                }
            }
        }

        return binding.root
    }

    override fun onDestroy() {
        mainViewModel.emitSideEffect(MainSideEffect.PauseUnity)
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
            viewModel.onIntent(CardEditorIntent.ChangeDialogState(DialogState.DELETE_CONFIRM))
        }

        binding.imgBtnTransformReset.setOnClickListener {
            viewModel.onIntent(CardEditorIntent.ResetTransform)
        }

        binding.imgBtnCameraFocusController.setOnClickListener {
            viewModel.onIntent(CardEditorIntent.CameraFocus)
        }

        binding.imgBtnAddText.setOnClickListener {
            viewModel.onIntent(CardEditorIntent.AddText)
        }

        binding.imgBtnDeleteText.setOnClickListener {
            val textId = viewModel.cardEditorState.value.selectedTextTempId ?: return@setOnClickListener
            viewModel.onIntent(CardEditorIntent.DeleteText(textId))
        }

        binding.imgBtnCameraFocusReset.setOnClickListener {
            viewModel.onIntent(CardEditorIntent.ResetCamera)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            viewModel.setImeVisible(imeVisible)
            insets
        }
    }

    private fun handleUnityMessage(msg: UnityMessage) {
        when (msg.type) {
            UnityEventType.CREATE_OBJECT -> {
                viewModel.onIntent(CardEditorIntent.CreateObjectResult(msg.status, msg.data))
            }
            UnityEventType.EXPORT_GLB -> {
                viewModel.onIntent(CardEditorIntent.ExportGlbResult(msg.status, msg.data))
            }
            else -> Unit
        }
    }

    private fun updateUnityContainerHeight(heightFraction: Float) {
        if(layoutParams.matchConstraintPercentHeight != heightFraction) {
            layoutParams.matchConstraintPercentHeight = heightFraction
            binding.unityContainer.layoutParams = layoutParams
        }
    }

    private fun updateUi(state: CardEditorState) {
        binding.imgBtnBack.isVisible = state.isAssetBrowserPanelActive
        binding.btnComplete.isVisible = state.isAssetBrowserPanelActive
        binding.imgBtnLink.isVisible = state.showLinkDetailButton
        binding.containerObjectOption.isVisible = state.showObjectOptionContainer
        binding.containerTextOption.isVisible = state.showTextOptionContainer
        loadingOverlayVisible.value = state.isLoading
        loadingText.value = state.loadingText
        binding.frameLoading.isVisible = state.isLoading

        updateTransformPanel(state)
    }

    private fun updateTransformPanel(state: CardEditorState) {
        val temp = state.tempTransform
        val active = state.isTransformPanelActive && temp != null

        binding.containerTransformOption.isVisible = active
        binding.imgBtnCameraFocusController.isVisible = active
        binding.imgBtnCameraFocusController.setImageResource(
            if(state.isTransformCameraFocus) R.drawable.ic_fit_screen else R.drawable.ic_target)
        binding.imgBtnTransformReset.isVisible = active && state.hasPendingTransform
        binding.imgObjectThumb.setImageResource(getObjectThumbByKey(requireContext(), temp?.thumbnailKey))
        binding.textElementKey.text = if(active) toBase62(temp.elementId) else ""
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
}