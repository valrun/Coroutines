package com.example.coroutines.datebase

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.coroutines.Item

@Database(entities = [Item::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun itemDao(): ItemDAO?
}
