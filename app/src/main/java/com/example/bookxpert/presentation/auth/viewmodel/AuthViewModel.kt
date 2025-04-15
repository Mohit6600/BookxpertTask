package com.example.bookxpert.presentation.auth.viewmodel


import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.bookxpert.domain.usecase.GetCurrentUserUseCase
import com.example.bookxpert.domain.usecase.SignInWithGoogleUseCase
import com.example.bookxpert.domain.usecase.SignOutUseCase
import com.example.bookxpert.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthViewModel @Inject constructor(
    private val signInWithGoogleUseCase: SignInWithGoogleUseCase,
    private val signOutUseCase: SignOutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    private val _currentUser = MutableStateFlow<User?>(null)
    val currentUser: StateFlow<User?> = _currentUser

    fun signInWithGoogle(idToken: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val result = signInWithGoogleUseCase(idToken)
                if (result.isSuccess) {
                    _authState.value = AuthState.Success(result.getOrNull()!!)
                    result.getOrNull()?.id?.let { userId ->
                        getCurrentUserUseCase(userId).collectLatest { user ->
                            _currentUser.value = user
                        }
                    }
                } else {
                    _authState.value =
                        AuthState.Error(result.exceptionOrNull()?.message ?: "Unknown error")
                }
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Unknown error")
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                signOutUseCase()
                _authState.value = AuthState.SignedOut
                _currentUser.value = null
            } catch (e: Exception) {
                _authState.value = AuthState.Error(e.message ?: "Sign out failed")
            }
        }
    }

    fun getCurrentUser(userId: String) {
        viewModelScope.launch {
            getCurrentUserUseCase(userId).collect { user ->
                _currentUser.value = user
            }
        }
    }
}

 open class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: User) : AuthState()
    data class Error(val message: String) : AuthState()
    object SignedOut : AuthState()
}