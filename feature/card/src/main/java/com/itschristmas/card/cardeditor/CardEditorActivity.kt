package com.itschristmas.card.cardeditor

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.itschristmas.card.cardeditor.common.getDrawableIdByKey
import com.itschristmas.card.cardeditor.common.toBase62
import com.itschristmas.card.cardeditor.model.EditorDialogState
import com.itschristmas.card.databinding.ActivityCardEditorBinding
import com.itschristmas.designsystem.theme.ItsChristmasTheme
import com.unity3d.player.UnityPlayerForActivityOrService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardEditorBinding
    private lateinit var unityPlayer: UnityPlayerForActivityOrService
    private lateinit var layoutParams: ConstraintLayout.LayoutParams
    private val viewModel: CardEditorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardEditorBinding.inflate(layoutInflater)
        layoutParams = binding.unityContainer.layoutParams as ConstraintLayout.LayoutParams
        val cardId = intent.getLongExtra("cardId", 1)

        setContentView(binding.root)
        initUnity()
        initListener()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    viewModel.cardEditorState.collect {
                        updateUi(it)
                    }
                }
                launch {
                    viewModel.unityContainerHeightFractionFlow.collect {
                        updateUnityContainerHeight(it)
                    }
                }
            }
        }

        binding.composeContainer.setContent {
            ItsChristmasTheme {
                CardEditorBottomScreen(cardId)
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun initUnity() {
        unityPlayer = UnityPlayerForActivityOrService(this)
        (unityPlayer.view.parent as? ViewGroup)?.removeView(unityPlayer.view)

        binding.unityContainer.addView(
            unityPlayer.view,
            ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )
        )

        unityPlayer.view.setOnTouchListener(null)
        binding.unityContainer.setOnTouchListener { v, event ->
            unityPlayer.injectEvent(event)
            if (event.action == MotionEvent.ACTION_UP) v.performClick()
            true
        }

        unityPlayer.windowFocusChanged(true)
    }

    private fun initListener() {
        binding.btnComplete.setOnClickListener {
            viewModel.onIntent(CardEditorIntent.CompleteClicked)
        }

        binding.btnAdjust.setOnClickListener {
            viewModel.onIntent(CardEditorIntent.AdjustClicked)
        }

        binding.imgBtnDelete.setOnClickListener {
            viewModel.onIntent(CardEditorIntent.DialogStateChanged(EditorDialogState.DELETE_CONFIRM))
        }

        binding.imgBtnTransformReset.setOnClickListener {
            viewModel.onIntent(CardEditorIntent.TransformResetClicked)
        }

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { _, insets ->
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            viewModel.setImeVisible(imeVisible)
            insets
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
        binding.containerObjectOption.isVisible = state.showObjectOptionContainer

        updateTransformPanel(state)
    }

    private fun updateTransformPanel(state: CardEditorState) {
        val temp = state.tempTransformState
        val active = state.isTransformPanelActive && temp != null

        binding.containerTransformOption.isVisible = active
        binding.imgBtnTransformReset.isVisible = active && state.hasPendingTransform
        binding.imgObjectThumb.setImageResource(getDrawableIdByKey(this, temp?.thumbnailKey))
        binding.textElementKey.text = if(active) toBase62(temp.elementId) else ""
    }

    override fun onResume() {
        super.onResume()
        unityPlayer.resume()
    }

    override fun onPause() {
        unityPlayer.pause()
        super.onPause()
    }

    override fun onDestroy() {
        unityPlayer.destroy()
        super.onDestroy()
    }
}