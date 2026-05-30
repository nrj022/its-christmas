package com.zcard.feature.cardshare

import androidx.lifecycle.ViewModel
import com.zcard.domain.network.NetworkManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject
import kotlinx.coroutines.flow.update

private const val TAG = "CardShareViewModel"

@HiltViewModel
class CardShareViewModel @Inject constructor(
    private val networkManager: NetworkManager
): ViewModel() {

    private val _state = MutableStateFlow(CardShareState())
    val state: StateFlow<CardShareState> = _state

    private val _sideEffect = Channel<CardShareSideEffect>(Channel.BUFFERED)
    val sideEffect = _sideEffect.receiveAsFlow()

    fun onIntent(intent: CardShareIntent) {
        when(intent) {
            is CardShareIntent.NavigateBack -> handleNavigateBack()
            is CardShareIntent.NavigateToHome -> handleNavigateToHome()
            is CardShareIntent.ShareCardLink -> handleShareCardLink(intent.cardUrl)
            is CardShareIntent.CheckNetwork -> handleCheckNetwork()
            is CardShareIntent.StopLoading -> handleStopLoading()
        }
    }

    private fun handleNavigateBack() {
        _sideEffect.trySend(CardShareSideEffect.NavigateBack)
    }

    private fun handleNavigateToHome() {
        _sideEffect.trySend(CardShareSideEffect.NavigateToHome)
    }

    private fun handleShareCardLink(cardUrl: String) {
        _sideEffect.trySend(CardShareSideEffect.ShareCardLink(cardUrl))
    }

    private fun handleCheckNetwork() {
        _state.update { it.copy(isOnline = networkManager.isOnline) }
    }

    private fun handleStopLoading() {
        _state.update { it.copy(isLoading = false) }
    }
}