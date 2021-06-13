package com.example.restaurent_finder.Database

import android.os.Parcel
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "restroList")
data class RestroEntity (
 @PrimaryKey var restro_id: Int,
 @ColumnInfo(name = "restro_name") var RestroName: String,
 @ColumnInfo(name = "restro_price") var RestroPrice: String,
 @ColumnInfo(name = "restro_rating") var RestroRating: String,
 @ColumnInfo(name = "restro_image") var RestroImage: String
)
