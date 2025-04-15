package com.example.bookxpert.data.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "api_items")
data class LocalApiItem(
    @PrimaryKey val id: String,
    val name: String,
    val color: String?,
    val capacity: String?
)