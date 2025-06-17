package com.luscii.sdk.demo

import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.luscii.sdk.demo.DemoApp.Companion.luscii
import com.luscii.sdk.Luscii.AuthenticateResult.Invalid
import com.luscii.sdk.Luscii.AuthenticateResult.Success
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private suspend fun authenticate(apiKey: String) =
        luscii.authenticate(apiKey)

    private val scope = CoroutineScope(Dispatchers.Main)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            var patientApiKey by remember { mutableStateOf("") }
            var errorMessage by remember { mutableStateOf("") }

            val customActionsLauncher =
                rememberLauncherForActivityResult(StartActivityForResult()) {}

            fun authenticate(onSuccess: () -> Unit) = scope.launch {
                when (authenticate(patientApiKey)) {
                    Invalid -> errorMessage = "API key is invalid or expired"
                    is Success -> onSuccess()
                }
            }

            Surface(
                modifier = Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Form(
                    patientApiKey,
                    errorMessage,
                    onChange = { patientApiKey = it },
                    onConfirm = {
                        authenticate {
                            luscii.startActionsActivity(this)
                        }
                    },
                    onCustomConfirm = {
                        authenticate {
                            customActionsLauncher.launch(CustomActionsActivity.intent(this@MainActivity))
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun Form(
    value: String,
    error: String,
    onChange: (String) -> Unit,
    onConfirm: () -> Unit,
    onCustomConfirm: () -> Unit
) {
    Box(
        Modifier.padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "Enter patient API key",
                Modifier.padding(bottom = 16.dp)
            )

            TextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                value = value,
                onValueChange = onChange,
                label = { Text("Patient API key") },
                isError = error.isNotEmpty(),
                supportingText = {
                    if (error.isNotEmpty()) {
                        Text(error)
                    }
                }
            )

            Button(onClick = { onConfirm() }) {
                Text("Confirm & show default Actions screen")
            }

            Button(onClick = onCustomConfirm) {
                Text("Confirm & show custom Action screens")
            }
        }
    }
}