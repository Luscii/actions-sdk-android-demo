package com.luscii.sdk.demo.login

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.input.TextFieldState
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luscii.sdk.demo.R

@Composable
fun LoginScreen(
    goToActions: () -> Unit,
    goToCustomActions: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(state is LoginState.LoggedIn) {
        val state = state
        if (state is LoginState.LoggedIn) {
            when (state.destination) {
                is LoginState.LoggedIn.Destination.Actions -> goToActions()
                is LoginState.LoggedIn.Destination.CustomActions -> goToCustomActions()
            }
        }
    }

    LoginScreenContent(
        state,
        viewModel.patientApiKeyFieldState,
        viewModel::login
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginScreenContent(
    state: LoginState,
    patientApiKeyFieldState: TextFieldState,
    login: (LoginState.LoggedIn.Destination) -> Unit,
) {
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.app_name))
                },
            )
        }
    ) { contentPadding ->
        Box(Modifier.padding(contentPadding)) {
            Form(
                patientApiKeyFieldState,
                error = when (state) {
                    is LoginState.Error -> "Invalid API key"
                    else -> ""
                },
                onConfirm = { login(LoginState.LoggedIn.Destination.Actions) },
                onCustomConfirm = { login(LoginState.LoggedIn.Destination.CustomActions) },
            )
        }
    }
}

@Composable
private fun Form(
    state: TextFieldState,
    error: String,
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
                state,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                label = { Text("Patient API key") },
                isError = error.isNotEmpty(),
                supportingText = {
                    if (error.isNotEmpty()) {
                        Text(error)
                    }
                }
            )

            Button(onClick = onConfirm) {
                Text("Confirm & show default Actions screen")
            }

            Button(onClick = onCustomConfirm) {
                Text("Confirm & show custom Action screens")
            }
        }
    }
}