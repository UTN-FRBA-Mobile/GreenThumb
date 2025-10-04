package com.utn.greenthumb.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.utn.greenthumb.state.UiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

abstract class BaseViewModel<T> : ViewModel() {

    protected val _uiState = MutableStateFlow<UiState<T>>(UiState.Idle)
    val uiState: StateFlow<UiState<T>> = _uiState.asStateFlow()

    protected fun setLoading() {
        _uiState.value = UiState.Loading
    }

    protected fun setSuccess(data: T) {
        _uiState.value = UiState.Success(data)
    }

    protected fun setError(message: String, throwable: Throwable? = null, errorCode: String? = null) {
        _uiState.value = UiState.Error(message, throwable, errorCode)
    }

    protected fun setIdle() {
        _uiState.value = UiState.Idle
    }

    /**
     * Limpiar estado de error
     */
    fun clearError() {
        if (_uiState.value is UiState.Error) {
            _uiState.value = UiState.Idle
        }
    }

    /**
     * Ejecutar operación suspendida con manejo automático de estados
     */
    protected fun executeOperation(
        operation: suspend () -> T,
        onError: (Throwable) -> String = { it.message ?: "Error desconocido" }
    ) {
        viewModelScope.launch {
            try {
                setLoading()
                val result = operation()
                setSuccess(result)
            } catch (e: Exception) {
                val errorMessage = onError(e)
                setError(errorMessage, e)
                Log.e(this@BaseViewModel::class.simpleName, "Operation failed", e)
            }
        }
    }
}