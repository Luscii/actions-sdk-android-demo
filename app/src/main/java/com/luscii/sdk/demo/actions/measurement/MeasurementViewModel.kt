package com.luscii.sdk.demo.actions.measurement

import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.foundation.text.input.clearText
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luscii.sdk.Luscii
import com.luscii.sdk.actions.Action
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.ZonedDateTime
import javax.inject.Inject

@HiltViewModel
class MeasurementViewModel @Inject constructor(
    private val luscii: Luscii
) : ViewModel() {
    private val _uiState: MutableStateFlow<MeasurementState> = MutableStateFlow(MeasurementState.Loading)
    val uiState: StateFlow<MeasurementState> = _uiState

    val textFieldState = TextFieldState()

    private lateinit var measurements: Action.Measurements

    fun init(actionId: Action.Id) {
        viewModelScope.launch {
            val action = luscii.getAction(actionId) ?: return@launch // TODO: handle error

            _uiState.value = MeasurementState.AtItem(
                action,
                action.instruments.first(),
                action.instruments.first().items.first()
            )

            measurements = Action.Measurements(
                action,
                completedAt = ZonedDateTime.now(),
            )
        }
    }

    fun next() {
        _uiState.update { state ->
            if (state !is MeasurementState.AtItem) return@update state

            measurements = measurements.updateInstrumentItemMeasurement(state.item) { _ ->
                Action.Instrument.Item.Measurement(
                    state.item,
                    value = textFieldState.text.toString()
                )
            }

            textFieldState.clearText()

            val currentItemIndex = state.instrument.items.indexOf(state.item)
            val nextItemInInstrument = state.instrument.items.elementAtOrNull(currentItemIndex + 1)

            val instrumentIndex = state.action.instruments.indexOf(state.instrument)
            val nextInstrument = when (nextItemInInstrument) {
                null -> state.action.instruments.elementAtOrNull(instrumentIndex + 1)
                else -> state.instrument
            }

            when (nextInstrument) {
                null -> {
                    submit()
                    MeasurementState.Done(state.action)
                }
                else -> MeasurementState.AtItem(
                    state.action,
                    nextInstrument,
                    nextItemInInstrument ?: nextInstrument.items.first()
                )
            }
        }
    }

    private fun submit() {
        viewModelScope.launch {
            luscii.submitMeasurements(measurements.copy(completedAt = ZonedDateTime.now()))
        }
    }
}