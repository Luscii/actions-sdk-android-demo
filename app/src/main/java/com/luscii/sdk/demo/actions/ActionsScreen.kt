package com.luscii.sdk.demo.actions

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.luscii.sdk.actions.Action
import com.luscii.sdk.actions.LaunchActionFlowActivityResultContract
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun ActionsScreen(
    actionFlowActivityResultContract: LaunchActionFlowActivityResultContract,
    startMeasurement: (Action.Id) -> Unit,
    viewModel: ActionsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()

    val actionFlowLauncher = rememberLauncherForActivityResult(actionFlowActivityResultContract) {
        viewModel.onActionFlowResult(it)
    }

    ActionsScreenContent(
        state,
        launchActionFlow = { actionFlowLauncher.launch(it) },
        startMeasurement = { startMeasurement(it.id) },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ActionsScreenContent(
    state: ActionsState,
    launchActionFlow: (Action) -> Unit,
    startMeasurement: (Action) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding).verticalScroll(rememberScrollState())) {
            when (state) {
                ActionsState.Loading -> CircularProgressIndicator()
                is ActionsState.Success -> {
                    if (state.lastActionFlowResult != null) {
                        Text(
                            "Last action flow result: ${state.lastActionFlowResult}",
                            Modifier.padding(16.dp)
                        )
                    }

                    ActionList(
                        state.actionsToday,
                        onClick = launchActionFlow,
                        onTrailingClick = startMeasurement
                    )

                    Text(
                        "Self care actions",
                        Modifier.padding(16.dp),
                        style = MaterialTheme.typography.titleMedium
                    )

                    ActionList(
                        state.selfCareActions,
                        onClick = launchActionFlow,
                        onTrailingClick = startMeasurement
                    )
                }
            }
        }
    }
}

@Composable
private fun ActionList(
    actions: List<Action>,
    onClick: (Action) -> Unit,
    onTrailingClick: (Action) -> Unit = {}
) {
    Column {
        actions.forEach {
            ListItem(
                modifier = Modifier.clickable { onClick(it) },
                headlineContent = { Text(it.name) },
                supportingContent = {
                    Column {
                        it.completedAt?.let { date ->
                            Text(
                                "Completed at: " +
                                        date.withZoneSameInstant(ZoneId.systemDefault())
                                            .format(DateTimeFormatter.RFC_1123_DATE_TIME)
                            )
                        }

                        Text("isPlanned: ${it.isPlanned}")

                        Text("isExtra: ${it.isExtra}")

                        Text("category: ${it.category}")
                    }
                },
                trailingContent = {
                    IconButton({ onTrailingClick(it) }) {
                        Icon(
                            Icons.Default.Create,
                            contentDescription = "Start measurement",
                        )
                    }
                }
            )
        }
    }
}