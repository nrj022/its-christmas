package com.example.card.ui.cardeditor

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import com.example.card.databinding.ActivityCardEditorBinding
import com.example.designsystem.theme.ItsHalloweenTheme
import com.unity3d.player.UnityPlayerForActivityOrService

class CardEditorActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardEditorBinding
    private lateinit var unityPlayer: UnityPlayerForActivityOrService

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardEditorBinding.inflate(layoutInflater)
        setContentView(binding.root)

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

        binding.composeContainer.setContent {
            ItsHalloweenTheme {
                AssetBrowserPanel()
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