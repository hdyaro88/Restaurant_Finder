package com.example.restaurent_finder.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ItemDao {
    @Insert
    fun insertItem(ItemEntity: ItemEntity)

    @Delete
    fun deleteItem(itemEntitity: ItemEntity)
    @Query("Select * from itemList" )
    fun getAllItems() : List<ItemEntity>
    @Query("Select * from itemList where item_id = :Itemid")
    fun getItemById(Itemid : String) : ItemEntity
    @Query("Delete From itemList")
    fun deleteAllItems() : Void
}