package com.zcard.feature.cardeditor

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zcard.card.R
import com.zcard.card.databinding.ActivityCardEditorBinding
import com.zcard.domain.model.UnityMessage
import com.unity3d.player.UnityPlayerForActivityOrService
import com.zcard.feature.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCardEditorBinding
    private lateinit var unityPlayer: UnityPlayerForActivityOrService
    private lateinit var layoutParams: ConstraintLayout.LayoutParams
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCardEditorBinding.inflate(layoutInflater)
        layoutParams = binding.unityContainer.layoutParams as ConstraintLayout.LayoutParams

        setContentView(binding.root)

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, HomeFragment())
            .addToBackStack(null)
            .commit()

        initUnity()

        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.mainSideEffect.collect { sideEffect ->
                    when(sideEffect) {
                        is MainSideEffect.ResumeUnity -> {
                            unityPlayer.onResume()
                        }
                        is MainSideEffect.PauseUnity -> {
                            unityPlayer.onPause()
                        }
                        is MainSideEffect.UnityContainerHeightFraction -> {
                            updateUnityContainerHeight(sideEffect.fraction)
                        }
                        else -> Unit
                    }
                }
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

    private fun updateUnityContainerHeight(heightFraction: Float) {
        if(layoutParams.matchConstraintPercentHeight != heightFraction) {
            layoutParams.matchConstraintPercentHeight = heightFraction
            binding.unityContainer.layoutParams = layoutParams
        }
    }

    // Unity에서 호출하는 함수
    fun onUnityMessage(jsonString: String) {
        Log.d("UnityMsg", "Received: $jsonString")
        try {
            val message = Json.decodeFromString<UnityMessage>(jsonString)
            // UI 스레드에서 처리 보장
            runOnUiThread { viewModel.emitUnityMessage(message) }
        } catch (e: Exception) {
            Log.e("UnityMsg", "Parsing Error: ${e.message}")
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
            // 앱 백그라운드 이동 + 메모리 부족 시
            level >= TRIM_MEMORY_BACKGROUND -> unityPlayer.onTrimMemory(UnityPlayerForActivityOrService.MemoryUsage.High)
            // 앱 백그라운드 이동 시
            level == TRIM_MEMORY_UI_HIDDEN -> unityPlayer.onTrimMemory(UnityPlayerForActivityOrService.MemoryUsage.Medium)
            else -> unityPlayer.onTrimMemory(UnityPlayerForActivityOrService.MemoryUsage.Medium)
        }
    }

    // 레이아웃에 따른 Unity 맵핑
    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        unityPlayer.configurationChanged(newConfig)
    }
}