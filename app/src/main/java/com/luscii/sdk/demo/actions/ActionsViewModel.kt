package com.luscii.sdk.demo.actions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luscii.sdk.Luscii
import com.luscii.sdk.actions.Action
import com.luscii.sdk.actions.ActionFlowResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActionsViewModel @Inject constructor(
    private val luscii: Luscii
) : ViewModel() {

    private val actionFlowResultState: MutableStateFlow<ActionFlowResult?> = MutableStateFlow(null)

    private val actionsState = MutableStateFlow(emptyList<Action>())

    init {
        viewModelScope.launch {
           updateActions()
        }
    }

    val uiState = combine(
        actionsState,
        actionFlowResultState

    ) { actions, actionFlowResult ->
        ActionsState.Success(actions, actionFlowResult)
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            ActionsState.Loading
        )

    private suspend fun updateActions() {
        actionsState.value = luscii.getActions()
    }

    fun onActionFlowResult(result: ActionFlowResult) {
        viewModelScope.launch {
            updateActions()
            actionFlowResultState.value = result
        }
    }
}