package com.example.bookxpert.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.bookxpert.data.local.model.LocalApiItem
import kotlinx.coroutines.flow.Flow

@Dao
interface ApiItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<LocalApiItem>)

    @Query("SELECT * FROM api_items")
    fun getAllItems(): Flow<List<LocalApiItem>>

    @Update
    suspend fun updateItem(item: LocalApiItem)

    @Delete
    suspend fun deleteItem(item: LocalApiItem)

    @Query("DELETE FROM api_items")
    suspend fun clearAll()
}