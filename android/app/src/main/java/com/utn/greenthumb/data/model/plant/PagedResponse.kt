package com.utn.greenthumb.data.model.plant

data class PagedResponse<T>(
    val page: Int,
    val limit: Int,
    val total: Int,
    val totalPages: Int,
    val content: List<T>
)