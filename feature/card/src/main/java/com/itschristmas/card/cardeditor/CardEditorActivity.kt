package com.itschristmas.card.cardeditor

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.itschristmas.card.databinding.ActivityCardEditorBinding
import com.itschristmas.designsystem.theme.ItsChristmasTheme
import com.unity3d.player.UnityPlayerForActivityOrService
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CardEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardEditorBinding
    private lateinit var unityPlayer: UnityPlayerForActivityOrService

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val cardId = intent.getLongExtra("cardId", 1)

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

        // TODO: ViewModel 연동 후 compose의 my object selected 여부와 visibility 연결
        binding.objectOptionContainer.visibility = View.VISIBLE

        unityPlayer.windowFocusChanged(true)

        binding.composeContainer.setContent {
            ItsChristmasTheme {
                CardEditorBottomScreen(cardId)
            }
        }
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