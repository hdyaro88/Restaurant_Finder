package com.example.restaurent_finder.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurent_finder.Database.RestroEntity
import com.example.restaurent_finder.R
import com.squareup.picasso.Picasso

class Favorite_adapter(val context : Context , val restrolist : List<RestroEntity>) :
    RecyclerView.Adapter<Favorite_adapter.Favourite_viewHolder>() {
    class Favourite_viewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var txtRestroName : TextView = view.findViewById(R.id.txtRestroName)
        var txtRestoPrice : TextView = view.findViewById(R.id.txtRestroPrice)
        var txtRestroRating : TextView = view.findViewById(R.id.txtRestroRating)
        var imgRestroImage : ImageView = view.findViewById(R.id.img_restro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Favourite_viewHolder {
        val view = LayoutInflater.from(parent.context).inflate((R.layout.single_favourite_row) , parent , false);
        return Favourite_viewHolder(view);
    }

    override fun getItemCount(): Int {
        return restrolist.size;
    }

    override fun onBindViewHolder(holder: Favourite_viewHolder, position: Int) {
        val restro = restrolist[position]
        holder.txtRestroName.text = restro.RestroName
        holder.txtRestoPrice.text = "Rs. ${restro.RestroPrice}"
        holder.txtRestroRating.text = restro.RestroRating
        Picasso.get().load(restro.RestroImage).error(R.drawable.default_book_cover)
            .into(holder.imgRestroImage);
    }
}