package com.zcard.network

import android.Manifest
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.annotation.RequiresPermission
import androidx.core.content.getSystemService
import com.zcard.domain.network.NetworkManager
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class NetworkManagerImpl @Inject constructor(
    @ApplicationContext private val context: Context
): NetworkManager {

    override val isOnline: Boolean
        @RequiresPermission(Manifest.permission.ACCESS_NETWORK_STATE)
        get() {
            val connectivityManager = context.getSystemService<ConnectivityManager>()
                ?: return false

            // 현재 기기가 사용 중인 활성 네트워크 (비행기 모드거나 다 꺼져있으면 false
            val activeNetwork = connectivityManager.activeNetwork
                ?: return false

            // 해당 네트워크의 속성(Capabilities)
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                ?: return false

            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) &&
                    capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_VALIDATED)
        }

}