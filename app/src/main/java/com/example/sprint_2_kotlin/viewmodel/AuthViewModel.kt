package com.example.sprint_2_kotlin.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.sprint_2_kotlin.model.repository.Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: Repository = Repository()
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState

    fun onEmailChange(value: String) {
        _uiState.value = _uiState.value.copy(email = value)
    }

    fun onPasswordChange(value: String) {
        _uiState.value = _uiState.value.copy(password = value)
    }

    fun login() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val success = repository.signIn(_uiState.value.email, _uiState.value.password)
            println("DEBUG: Login result = $success")
            _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = success)
        }
    }

    fun register() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val success = repository.signUp(_uiState.value.email, _uiState.value.password)
            _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = success)
        }
    }

    fun loginWithBiometric() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            // Check if user session exists
            val isLoggedIn = repository.isUserLoggedIn()
            _uiState.value = _uiState.value.copy(isLoading = false, isSuccess = isLoggedIn)
        }
    }
}

data class AuthUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false
)