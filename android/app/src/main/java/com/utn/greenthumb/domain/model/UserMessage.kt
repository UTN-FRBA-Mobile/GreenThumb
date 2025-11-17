package com.utn.greenthumb.domain.model

enum class Severity {
    INFO,
    WARNING,
    ERROR
}

data class UserMessage (
    val id: String? = null,
    val message: String,
    val severity: Severity,
    val showToast: Boolean = false,
)