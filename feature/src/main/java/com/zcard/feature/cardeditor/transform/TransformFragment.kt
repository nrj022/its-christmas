package com.zcard.feature.cardeditor.transform

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zcard.feature.cardeditor.util.getObjectThumbByKey
import com.zcard.feature.R
import com.zcard.designsystem.theme.ZCardTheme
import com.zcard.feature.MainIntent
import com.zcard.feature.MainViewModel
import com.zcard.feature.databinding.FragmentTransformBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue

@AndroidEntryPoint
class TransformFragment : Fragment() {

    private lateinit var binding: FragmentTransformBinding
    private val viewModel: TransformViewModel by viewModels()
    private val mainViewModel: MainViewModel by activityViewModels()

    companion object {
        private const val ARG_ELEMENT_ID = "elementId"
        private const val UNITY_HEIGHT_RATIO = 0.6f

        fun newInstance(elementId: Long) =
            TransformFragment().apply {
                arguments = Bundle().apply {
                    putLong(ARG_ELEMENT_ID, elementId)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentTransformBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val elementId = requireArguments().getLong(ARG_ELEMENT_ID)
        viewModel.onIntent(TransformIntent.Init(elementId))
        mainViewModel.onIntent(MainIntent.OnUnityContainerHeightChanged(UNITY_HEIGHT_RATIO))

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                viewModel.onIntent(TransformIntent.Exit)
            }
        })

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.navigationBars())
            v.setPadding(0, 0, 0, insets.bottom)

            windowInsets
        }

        initListener()

        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.transformState.collect { updateUi(it) }
                }
                launch {
                    viewModel.transformSideEffect.collect { sideEffect ->
                        when (sideEffect) {
                            is TransformSideEffect.Finish -> { parentFragmentManager.popBackStack() }
                            is TransformSideEffect.ToastMessage -> {
                                Toast.makeText(requireContext(), getString(sideEffect.msgRes),Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }
            }
        }

        binding.composeContainer.setContent { ZCardTheme { TransformScreen() } }
    }

    private fun initListener() {
        binding.imgBtnTransformReset.setOnClickListener {
            viewModel.onIntent(TransformIntent.ResetTransform)
        }

        binding.imgBtnCameraFocusController.setOnClickListener {
            viewModel.onIntent(TransformIntent.ToggleCameraFocus)
        }
    }

    private fun updateUi(state: TransformState) {
        binding.imgBtnTransformReset.isVisible = state.hasPendingTransform
        binding.imgBtnCameraFocusController.setImageResource(
            if(state.isCameraFocus) R.drawable.ic_fit_screen else R.drawable.ic_target)
        binding.imgObjectThumb.setImageResource(getObjectThumbByKey(requireContext(), state.thumbnailKey))
        binding.textElementKey.text = state.elementKey
    }
}