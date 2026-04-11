package com.zcard.feature

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.annotation.Keep
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.zcard.domain.model.UnityMessage
import com.unity3d.player.UnityPlayerForActivityOrService
import com.zcard.feature.databinding.ActivityMainBinding
import com.zcard.feature.home.HomeFragment
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var unityPlayer: UnityPlayerForActivityOrService
    private lateinit var layoutParams: ConstraintLayout.LayoutParams
    private val viewModel: MainViewModel by viewModels()
    private var lastConfig: Configuration? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        layoutParams = binding.unityContainer.layoutParams as ConstraintLayout.LayoutParams
        lastConfig = Configuration(resources.configuration)

        setContentView(binding.root)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }

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

    /* Unity에서 호출하는 함수 */
    @Keep   // 배포 시 프로그램 최적화로부터 보호
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

        val diff = lastConfig?.diff(newConfig) ?: return
        val isOrientationChanged = (diff and ActivityInfo.CONFIG_ORIENTATION) != 0 || (diff and ActivityInfo.CONFIG_SCREEN_SIZE) != 0

        if (isOrientationChanged) {
            Log.i("ConfigCheck", "🟢 Orientation change detected → keep Unity engine (orientation=${newConfig.orientation})")
            unityPlayer.configurationChanged(newConfig)
        } else {
            Log.w("ConfigCheck", "🔴 Non-orientation config change detected → restarting app (diff=$diff)")
            restartApp()
        }

        lastConfig = Configuration(newConfig)
    }

    private fun restartApp() {
        val intent = packageManager.getLaunchIntentForPackage(packageName)
        if (intent == null) {
            Log.e("ConfigCheck", "Failed to get launch intent for package: $packageName")
            return
        }
        // CLEAR_TASK: 기존 태스크 스택 전체 제거 후 새 태스크에서 재시작
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(intent)
        // 현재 태스크의 모든 Activity 종료 후 프로세스 종료
        finishAffinity()
        Runtime.getRuntime().exit(0)
    }
}