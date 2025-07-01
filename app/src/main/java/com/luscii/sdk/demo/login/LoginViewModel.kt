package com.luscii.sdk.demo.login

import androidx.compose.foundation.text.input.TextFieldState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.luscii.sdk.Luscii
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val luscii: Luscii
) : ViewModel() {
    private val _uiState: MutableStateFlow<LoginState> = MutableStateFlow(LoginState.NotLoggedIn)
    val uiState: StateFlow<LoginState> = _uiState

    val patientApiKeyFieldState = TextFieldState()

    fun login(destination: LoginState.LoggedIn.Destination) {
        _uiState.value = LoginState.LoggingIn

        viewModelScope.launch {
            _uiState.value = when (luscii.authenticate(patientApiKeyFieldState.text.toString())) {
                Luscii.AuthenticateResult.Invalid -> LoginState.Error
                is Luscii.AuthenticateResult.Success -> LoginState.LoggedIn(destination)
            }

            delay(100)

            // State is reset so that the user can log in again
            _uiState.value = LoginState.NotLoggedIn
        }
    }
}