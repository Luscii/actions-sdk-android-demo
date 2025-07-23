package com.luscii.sdk.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.luscii.sdk.Luscii
import com.luscii.sdk.actions.Action
import com.luscii.sdk.actions.ActionFlowResult
import com.luscii.sdk.demo.actions.ActionsScreen
import com.luscii.sdk.demo.actions.measurement.MeasurementScreen
import com.luscii.sdk.demo.login.LoginScreen
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.Serializable
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var luscii: Luscii

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            val backStack = remember { mutableStateListOf<NavKey>(Login) }

            NavDisplay(
                backStack = backStack,
                entryDecorators = listOf(
                    rememberSceneSetupNavEntryDecorator(),
                    rememberSavedStateNavEntryDecorator(),
                    rememberViewModelStoreNavEntryDecorator()
                ),
                entryProvider = entryProvider {
                    entry<Login> {
                        LoginScreen(
                            goToActions = { luscii.startActionsActivity(this@MainActivity) },
                            goToSchedule = { luscii.startScheduleActivity(this@MainActivity) },
                            goToCustomActions = { backStack.add(CustomActions()) },
                        )
                    }

                    entry<CustomActions> {
                        ActionsScreen(
                            luscii.createActionFlowActivityResultContract(),
                            startMeasurement = { backStack.add(CustomActions.Measurement(it)) }
                        )
                    }

                    entry<CustomActions.Measurement> {
                        MeasurementScreen(it.actionId)
                    }
                }
            )
        }
    }
}

@Serializable
data object Login : NavKey

@Serializable
data class CustomActions(val result: ActionFlowResult? = null) : NavKey {
    @Serializable
    data class Measurement(val actionId: Action.Id) : NavKey
}

