package com.luscii.sdk.demo.actions

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luscii.sdk.Luscii
import com.luscii.sdk.actions.Action
import com.luscii.sdk.actions.ActionFlowResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
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

    private val actionsTodayState = MutableStateFlow(emptyList<Action>())
    private val selfCareActionsState = MutableStateFlow(emptyList<Action>())

    init {
        viewModelScope.launch {
            updateActions()
        }
    }

    val uiState = combine(
        actionsTodayState,
        selfCareActionsState,
        actionFlowResultState,
    ) { actions, selfCareActions, actionFlowResult ->
        ActionsState.Success(actions, selfCareActions, actionFlowResult)
    }
        .stateIn(
            viewModelScope,
            SharingStarted.WhileSubscribed(),
            ActionsState.Loading
        )

    private suspend fun updateActions() = coroutineScope {
        awaitAll(
            async {
                actionsTodayState.value = luscii.getTodayActions()
            },
            async {
                selfCareActionsState.value = luscii.getSelfCareActions()
            }
        )
    }

    fun onActionFlowResult(result: ActionFlowResult) {
        viewModelScope.launch {
            updateActions()
            actionFlowResultState.value = result
        }
    }
}