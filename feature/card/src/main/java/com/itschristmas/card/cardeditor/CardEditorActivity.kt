package com.itschristmas.card.cardeditor

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
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
import com.itschristmas.card.cardeditor.util.getDrawableIdByKey
import com.itschristmas.card.cardeditor.util.toBase62
import com.itschristmas.card.cardeditor.model.DialogState
import com.itschristmas.card.cardeditor.ui.CardEditorBottomScreen
import com.itschristmas.card.cardeditor.ui.CardEditorTextScreen
import com.itschristmas.card.databinding.ActivityCardEditorBinding
import com.itschristmas.designsystem.theme.ItsChristmasTheme
import com.itschristmas.domain.model.UnityMessage
import com.itschristmas.domain.model.UnityMessageType
import com.itschristmas.domain.model.UnityStatusType
import com.unity3d.player.UnityPlayerForActivityOrService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

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

        binding.composeContainerText.setContent {
            ItsChristmasTheme {
                CardEditorTextScreen()
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
    }

    private fun initListener() {
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

        binding.imgBtnAddText.setOnClickListener {
            viewModel.onIntent(CardEditorIntent.AddText)
        }

        binding.imgBtnDeleteText.setOnClickListener {
            val textId = viewModel.cardEditorState.value.selectedTextTempId ?: return@setOnClickListener
            viewModel.onIntent(CardEditorIntent.DeleteText(textId))
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
        binding.containerTextOption.isVisible = state.showTextOptionContainer
        binding.frameLoading.isVisible = state.isLoading

        updateTransformPanel(state)
    }

    private fun updateTransformPanel(state: CardEditorState) {
        val temp = state.tempTransform
        val active = state.isTransformPanelActive && temp != null

        binding.containerTransformOption.isVisible = active
        binding.imgBtnTransformReset.isVisible = active && state.hasPendingTransform
        binding.imgObjectThumb.setImageResource(getDrawableIdByKey(this, temp?.thumbnailKey))
        binding.textElementKey.text = if(active) toBase62(temp.elementId) else ""
    }

    // Unity에서 호출하는 함수
    fun onUnityMessage(jsonString: String) {
        Log.d("UnityMsg", "Received: $jsonString")
        try {
            val message = Json.decodeFromString<UnityMessage>(jsonString)
            // UI 스레드에서 처리 보장
            runOnUiThread {
                handleUnityMessage(message)
            }
        } catch (e: Exception) {
            Log.e("UnityMsg", "Parsing Error: ${e.message}")
        }
    }

    private fun handleUnityMessage(msg: UnityMessage) {
        when (msg.type) {
            UnityMessageType.LIFECYCLE -> {
                if (msg.status == UnityStatusType.START) {
                    viewModel.handleInitUnity()
                    Log.i("UnityMsg", "Unity Started Ready!")
                }
            }
            UnityMessageType.UPLOAD_GLB -> {
                if (msg.status == UnityStatusType.SUCCESS) { } else { }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        unityPlayer.onStart()
    }

    override fun onResume() {
        super.onResume()
        unityPlayer.onResume()
    }

    override fun onPause() {
        super.onPause()
        unityPlayer.onPause()
    }

    override fun onStop() {
        super.onStop()
        unityPlayer.onStop()
    }

    override fun onDestroy() {
        unityPlayer.destroy()
        super.onDestroy()
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        unityPlayer.windowFocusChanged(hasFocus)
    }

    // Low Memory Unity
    override fun onLowMemory() {
        super.onLowMemory()
        unityPlayer.onTrimMemory(UnityPlayerForActivityOrService.MemoryUsage.Critical)
    }

    // Trim Memory Unity
    override fun onTrimMemory(level: Int) {
        super.onTrimMemory(level)
        when {
            level >= TRIM_MEMORY_RUNNING_CRITICAL -> unityPlayer.onTrimMemory(UnityPlayerForActivityOrService.MemoryUsage.Critical)
            level >= TRIM_MEMORY_RUNNING_LOW -> unityPlayer.onTrimMemory(UnityPlayerForActivityOrService.MemoryUsage.High)
            else -> unityPlayer.onTrimMemory(UnityPlayerForActivityOrService.MemoryUsage.Medium)
        }
    }

    // 레이아웃에 따른 Unity 맵핑
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        unityPlayer.configurationChanged(newConfig)
    }
}