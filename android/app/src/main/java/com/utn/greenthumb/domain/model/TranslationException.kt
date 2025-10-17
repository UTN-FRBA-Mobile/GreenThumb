package com.utn.greenthumb.domain.model

class TranslationException(
    message: String,
    cause: Throwable? = null
) : Exception(message, cause)