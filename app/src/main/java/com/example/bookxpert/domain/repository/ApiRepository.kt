package com.example.bookxpert.domain.repository

import com.example.bookxpert.data.local.dao.ApiItemDao
import com.example.bookxpert.data.local.model.LocalApiItem
import com.example.bookxpert.data.remote.api.ApiService
import kotlinx.coroutines.flow.Flow

class ApiRepository(
    private val apiService: ApiService,
    private val apiItemDao: ApiItemDao
) {
    suspend fun fetchAndStoreItems() {
        try {
            val items = apiService.getItems()
            val localItems = items.map { item ->
                LocalApiItem(
                    id = item.id,
                    name = item.name,
                    color = item.data?.color,
                    capacity = item.data?.capacity,
                )
            }
            apiItemDao.clearAll()
            apiItemDao.insertAll(localItems)
        } catch (e: Exception) {
            throw e
        }
    }

    fun getAllItems(): Flow<List<LocalApiItem>> = apiItemDao.getAllItems()

    suspend fun updateItem(item: LocalApiItem) {
        apiItemDao.updateItem(item)
    }

    suspend fun deleteItem(item: LocalApiItem) {
        apiItemDao.deleteItem(item)
    }
}