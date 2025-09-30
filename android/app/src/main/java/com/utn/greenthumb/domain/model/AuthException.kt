package com.utn.greenthumb.domain.model

class AuthException(
    message: String,
    val errorCode: String? = null,
    cause: Throwable? = null
) : Exception(message, cause)