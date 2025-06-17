package com.luscii.sdk.demo

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.FragmentActivity
import com.luscii.sdk.demo.DemoApp.Companion.luscii
import com.luscii.sdk.actions.Action
import com.luscii.sdk.actions.ActionFlowResult
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@AndroidEntryPoint
class CustomActionsActivity : FragmentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val actions = remember { mutableStateOf(emptyList<Action>()) }
            val latestActionResult = remember { mutableStateOf<ActionFlowResult?>(null) }
            val scope = rememberCoroutineScope()

            suspend fun updateActions() {
                actions.value = luscii.getActions()
            }

            val actionFlowLauncher = rememberLauncherForActivityResult(luscii.createActionFlowActivityResultContract()) {
                latestActionResult.value = it
                scope.launch { updateActions() }
            }

            LaunchedEffect(Unit) { updateActions() }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Column {
                    if (latestActionResult.value != null) {
                        Text(
                            "Latest action result: ${latestActionResult.value}",
                            Modifier.padding(16.dp)
                        )
                    }

                    ActionList(actions.value, onClick = {
                        actionFlowLauncher.launch(it)
                    })
                }
            }
        }
    }

    companion object {
        fun intent(context: Context) = Intent(context, CustomActionsActivity::class.java)
    }
}

@Composable
private fun ActionList(actions: List<Action>, onClick: (Action) -> Unit) {
    Column {
        actions.forEach {
            ListItem(
                modifier = Modifier.clickable { onClick(it) },
                headlineContent = { Text(it.name) },
                supportingContent = it.completedAt?.let { date ->
                    {
                        Text(
                            "Completed at: " +
                                    date.withZoneSameInstant(ZoneId.systemDefault())
                                        .format(DateTimeFormatter.RFC_1123_DATE_TIME)
                        )
                    }
                }
            )
        }
    }
}