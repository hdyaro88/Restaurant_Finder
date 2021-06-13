package com.example.restaurent_finder.Database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "itemList")
data class ItemEntity(
    @PrimaryKey var item_id: Int,
    @ColumnInfo(name = "item_name") var ItemName: String,
    @ColumnInfo(name = "item_price") var ItemPrice: String
)