package com.luscii.sdk.demo.actions.measurement

import com.luscii.sdk.actions.Action

sealed interface MeasurementState {
    data object Loading : MeasurementState

    data class AtItem(
        val action: Action,
        val instrument: Action.Instrument,
        val item: Action.Instrument.Item
    ) : MeasurementState

    data class Done(val action: Action) : MeasurementState
}