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
import com.zcard.designsystem.theme.ZCardTheme
import com.zcard.feature.MainIntent
import com.zcard.feature.MainViewModel
import com.zcard.feature.databinding.FragmentTextEditBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class TextEditFragment : Fragment() {

    private var _binding: FragmentTextEditBinding? = null
    private val binding get() = _binding!!
    private lateinit var layoutParams: ConstraintLayout.LayoutParams

    private val viewModel: TextEditViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    companion object {
        private const val ARG_CARD_ID = "cardId"

        fun newInstance(cardId: Long) =
            TextEditFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_CARD_ID, cardId)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTextEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        layoutParams = binding.unityContainer.layoutParams as ConstraintLayout.LayoutParams

        val cardId = requireArguments().getLong(ARG_CARD_ID, -1L)    // newInstance로만 생성되므로 없으면 즉시 크래시
        require(cardId != -1L) { "Missing required argument: $ARG_CARD_ID" }
        viewModel.onIntent(TextEditIntent.Init(cardId))

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.onIntent(TextEditIntent.Exit)
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val imeVisible = windowInsets.isVisible(WindowInsetsCompat.Type.ime())
            val navInsets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())

            viewModel.onIntent(TextEditIntent.ImeVisible(imeVisible))
            v.setPadding(0, 0, 0, navInsets.bottom)

            windowInsets
        }

        initListener()

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
                    viewModel.textEditState.collect {
                        mainViewModel.onIntent(MainIntent.OnUnityContainerHeightChanged(it.unityContainerHeightFraction))
                        updateUnityContainerHeight(it.unityContainerHeightFraction)
                    }
                }
            }
        }

        binding.composeContainerText.setContent {
            ZCardTheme { TextListPanel() }
        }

        binding.composeContainer.setContent {
            ZCardTheme { TextEditScreen() }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
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