package com.luscii.sdk.demo.actions

import com.luscii.sdk.actions.Action
import com.luscii.sdk.actions.ActionFlowResult

sealed interface ActionsState {
    data object Loading : ActionsState
    data class Success(
        val actionsToday: List<Action>,
        val selfCareActions: List<Action>,
        val lastActionFlowResult: ActionFlowResult? = null
    ) : ActionsState
}