package com.example.restaurent_finder.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurent_finder.Database.ItemEntity
import com.example.restaurent_finder.R
import com.example.restaurent_finder.modal.Restro_item
import kotlinx.android.synthetic.main.single_cart_item.view.*

class History_row_Adapter(val context: Context , val itemList : List<Restro_item>) : RecyclerView.Adapter<History_row_Adapter.HistorRowViewHolder>() {
    class HistorRowViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val txt_item_name : TextView = view.findViewById(R.id.txt_food_item_name)
        val txt_item_price : TextView = view.findViewById(R.id.txt_food_item_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorRowViewHolder {
        val view = LayoutInflater.from(parent.context).inflate((R.layout.single_history_row_row) , parent , false)
        return HistorRowViewHolder(view);
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onBindViewHolder(holder: HistorRowViewHolder, position: Int) {
            val item = itemList[position]
        holder.txt_item_name.text = item.name
        holder.txt_item_price.text = "Rs. ${item.price}"
    }
}