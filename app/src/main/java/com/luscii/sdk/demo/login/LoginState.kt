package com.luscii.sdk.demo.login

sealed interface LoginState {
    data object NotLoggedIn : LoginState
    data object LoggingIn : LoginState
    data object Error : LoginState
    data class LoggedIn(val destination: Destination) : LoginState {
        sealed interface Destination {
            data object Actions : Destination
            data object CustomActions : Destination
        }
    }
}