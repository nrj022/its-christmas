package com.zcard.feature.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.zcard.domain.exception.CardNotExportedException
import com.zcard.domain.repository.CardRepository
import com.zcard.domain.model.UnityEventType
import com.zcard.domain.model.UnityMessage
import com.zcard.domain.usecase.CreateCardUseCase
import com.zcard.domain.usecase.GenerateCardUrlUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HomeViewModel"

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val cardRepository: CardRepository,
    private val createCardUseCase: CreateCardUseCase,
    private val generateCardUrlUseCase: GenerateCardUrlUseCase
): ViewModel() {

    private val _homeState = MutableStateFlow(HomeState())
    val homeState: StateFlow<HomeState> = _homeState

    private val _homeSideEffect = Channel<HomeSideEffect>(Channel.BUFFERED)
    val homeSideEffect = _homeSideEffect.receiveAsFlow()

    fun onIntent(intent: HomeIntent) {
        when(intent) {
            is HomeIntent.OnUnityMessage -> handleUnityMessage(intent.message)
            is HomeIntent.CreateCard -> handleCreateCard()
            is HomeIntent.NavigateToCardEditor -> navigateToCardEditor(intent.cardId)
            is HomeIntent.OpenBottomSheet -> handleOpenBottomSheet(intent.cardId)
            is HomeIntent.CloseBottomSheet -> handleCloseBottomSheet()
            is HomeIntent.ShareLink -> handleShareLink(intent.cardId)
            is HomeIntent.ConfirmDelete -> handleConfirmDelete()
            is HomeIntent.CancelDelete -> handleCancelDelete()
            is HomeIntent.DeleteCard -> handleDeleteCard(intent.cardId)
        }
    }

    init { loadCardPreviews() }

    private fun loadCardPreviews() {
        cardRepository.getCardPreviews()
            .onEach { result ->
                result.onSuccess { previews ->
                    _homeState.update { it.copy(cardPreviews = previews) }
                }.onFailure { e ->
                    Log.e(TAG, "getCardPreviewsFromDB: $e")
                    // TODO: 실패 시 처리
                }
            }.launchIn(viewModelScope)
    }

    private fun handleUnityMessage(msg: UnityMessage) {
        if(msg.type == UnityEventType.CREATE_CARD_THUMB) {
            val cardId = msg.data.toLongOrNull() ?: return
            if(cardId > 0) viewModelScope.launch { cardRepository.updateCardThumbnail(cardId) }
        }
    }

    private fun handleCreateCard() {
        viewModelScope.launch {
            createCardUseCase()
                .onSuccess { navigateToCardEditor(it) }
                .onFailure { e ->
                    _homeSideEffect.trySend(HomeSideEffect.ToastMessage("Failed to create card"))
                    Log.e(TAG, "createCard failed: $e")
                }
        }
    }

    private fun navigateToCardEditor(cardId: Long) {
        _homeSideEffect.trySend(HomeSideEffect.NavigateToCardEditor(cardId))
    }

    private fun handleOpenBottomSheet(cardId: Long) {
        _homeState.update { it.copy(selectedCardId = cardId) }
    }

    private fun handleCloseBottomSheet() {
        _homeState.update { it.copy(selectedCardId = null, deleteTargetCard = null) }
    }

    private fun handleShareLink(cardId: Long) {
        viewModelScope.launch {
            generateCardUrlUseCase(cardId)
                .onSuccess { link ->
                    _homeSideEffect.trySend(HomeSideEffect.ShareLink(link))
                }.onFailure { e ->
                    when(e) {
                        is CardNotExportedException -> {
                            _homeSideEffect.trySend(
                                HomeSideEffect.ToastMessage("Please export the card before sharing.")
                            )
                            Log.w(TAG, "generateCardUrl failed: $e")
                        }
                        else -> {
                            _homeSideEffect.trySend(
                                HomeSideEffect.ToastMessage("Failed to generate link. Please try again.")
                            )
                            Log.e(TAG, "generateCardUrl failed: $e")
                        }
                    }
                }
        }
    }

    private fun handleConfirmDelete() {
        _homeState.update {
            it.copy(deleteTargetCard = it.selectedCard)
        }
    }

    private fun handleCancelDelete() {
        _homeState.update {
            it.copy(deleteTargetCard = null)
        }
    }

    private fun handleDeleteCard(cardId: Long) {
        viewModelScope.launch {
            cardRepository.deleteCard(cardId)
                .onSuccess {
                    _homeSideEffect.trySend(HomeSideEffect.ToastMessage("Card deleted"))
                    handleCloseBottomSheet()
                }.onFailure { e ->
                    Log.e(TAG, "deleteCard failed: $e")
                    _homeSideEffect.trySend(HomeSideEffect.ToastMessage("Failed to delete card"))
                }
        }
    }
}