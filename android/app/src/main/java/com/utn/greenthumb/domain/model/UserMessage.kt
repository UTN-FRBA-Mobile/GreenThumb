package com.utn.greenthumb.domain.model

enum class Severity {
    INFO,
    WARNING,
    ERROR
}

data class UserMessage (
    val message: String,
    val severity: Severity
)