package com.example.coroutines

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity()
data class Item(
        @PrimaryKey() val id: Int,
        @ColumnInfo(name = "title") val title: String?,
        @ColumnInfo(name = "body") val body: String?,
        @ColumnInfo(name = "userId") val userId: Long)
