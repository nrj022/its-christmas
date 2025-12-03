package com.itschristmas.card.cardeditor

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.itschristmas.card.databinding.ActivityCardEditorBinding
import com.itschristmas.designsystem.theme.ItsChristmasTheme
import com.unity3d.player.UnityPlayerForActivityOrService
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CardEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardEditorBinding
    private lateinit var unityPlayer: UnityPlayerForActivityOrService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardEditorBinding.inflate(layoutInflater)
        val cardId = intent.getLongExtra("cardId", 1)
        val viewModel: CardEditorViewModel by viewModels()

        setContentView(binding.root)
        unitySetting()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.cardEditorState.collect {
                    updateUi(it)
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
    private fun unitySetting() {
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

    private fun updateUi(state: CardEditorState) {
        binding.objectOptionContainer.visibility = if(state.showObjectOptionContainer) View.VISIBLE else View.GONE
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