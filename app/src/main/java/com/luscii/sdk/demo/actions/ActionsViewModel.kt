package com.luscii.sdk.demo.actions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luscii.sdk.Luscii
import com.luscii.sdk.actions.ActionFlowResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActionsViewModel @Inject constructor(
    private val luscii: Luscii
) : ViewModel() {
    private val _uiState: MutableStateFlow<ActionsState> = MutableStateFlow(ActionsState.Loading)
    val uiState: StateFlow<ActionsState> = _uiState

    init {
        viewModelScope.launch {
            luscii.getActionsFlow().collect { actions ->
                _uiState.update {
                    when (it) {
                        is ActionsState.Success -> it.copy(actions = actions)
                        else -> ActionsState.Success(actions)
                    }
                }
            }
        }
    }

    fun onActionFlowResult(result: ActionFlowResult) {
        viewModelScope.launch {
            _uiState.update {
                when (it) {
                    is ActionsState.Success -> it.copy(lastActionFlowResult = result)
                    else -> it
                }
            }
        }
    }
}