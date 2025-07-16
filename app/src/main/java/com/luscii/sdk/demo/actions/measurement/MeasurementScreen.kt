@file:OptIn(ExperimentalMaterial3ExpressiveApi::class)

package com.luscii.sdk.demo.actions.measurement

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luscii.sdk.actions.Action

@Composable
fun MeasurementScreen(
    actionId: Action.Id,
    viewModel: MeasurementViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.init(actionId)
    }

    MeasurementScreenContent(
        state,
        viewModel.textFieldState,
        viewModel::next
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MeasurementScreenContent(
    state: MeasurementState,
    textFieldState: TextFieldState,
    next: () -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(Modifier.padding(innerPadding)) {
            when (state) {
                MeasurementState.Loading -> Text("Loading")
                is MeasurementState.AtItem -> Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Spacer(Modifier.height(8.dp))

                    Text(
                        "At item: ${state.item.id} of ${state.instrument.id}",
                        textAlign = TextAlign.Center
                    )

                    Spacer(Modifier.height(8.dp))

                    Text("Quantity: ${state.item.quantity}")

                    Spacer(Modifier.height(8.dp))

                    TextField(
                        textFieldState,
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        ),
                        lineLimits = TextFieldLineLimits.SingleLine,
                    )

                    Spacer(Modifier.height(16.dp))

                    Button(
                        onClick = next,
                    ) {
                        Text("Next")
                    }
                }

                is MeasurementState.Done -> Text(
                    "Action done: ${state.action.name}",
                    Modifier.align(Alignment.Center)
                )
            }
        }
    }
}