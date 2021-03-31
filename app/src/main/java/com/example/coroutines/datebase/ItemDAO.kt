package com.example.coroutines.datebase

import androidx.room.*
import com.example.coroutines.Item

@Dao
interface ItemDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<Item>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(items: Item)

    @Query("DELETE FROM Item where ID=:id")
    suspend fun deleteByID(id: Int)

    @Query("DELETE FROM Item")
    suspend fun deleteAll()

    @Query("SELECT * FROM Item")
    suspend fun getItems(): List<Item>
}
