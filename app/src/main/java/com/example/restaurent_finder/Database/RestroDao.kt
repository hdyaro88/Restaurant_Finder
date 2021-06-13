package com.example.restaurent_finder.Database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RestroDao {
    @Insert
    fun insertRestro(restroEntity: RestroEntity)

    @Delete
    fun deleteRestro(bookEntity: RestroEntity)
    @Query("Select * from restroList" )
    fun getAllRestro() : List<RestroEntity>
    @Query("Select * from restroList where restro_id = :Restroid")
    fun getRestroById(Restroid : String) : RestroEntity
}