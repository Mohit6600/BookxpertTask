package com.example.bookxpert.domain.model

data class ApiItem(
    val id: String,
    val name: String,
    val data: ItemData?
)

data class ItemData(
    val color: String?,
    val capacity: String?
)