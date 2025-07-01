package com.luscii.sdk.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import com.luscii.sdk.Luscii
import com.luscii.sdk.actions.ActionFlowResult
import com.luscii.sdk.demo.actions.ActionsScreen
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
                entryProvider = entryProvider {
                    entry<Login> {
                        LoginScreen(
                            goToActions = { luscii.startActionsActivity(this@MainActivity) },
                            goToCustomActions = { backStack.add(CustomActions()) }
                        )
                    }

                    entry<CustomActions> {
                        ActionsScreen(luscii.createActionFlowActivityResultContract())
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
    data object Measurement : NavKey
}

