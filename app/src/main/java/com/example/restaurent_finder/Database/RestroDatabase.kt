package com.example.restaurent_finder.Database
import androidx.room.Database
import androidx.room.RoomDatabase
@Database(entities = [RestroEntity :: class , ItemEntity::class] , version = 2)
abstract class RestroDatabase : RoomDatabase() {
      abstract  fun restroDao() : RestroDao
      abstract fun itemDao() : ItemDao
}