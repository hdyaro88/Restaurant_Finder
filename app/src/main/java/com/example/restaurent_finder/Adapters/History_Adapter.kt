package com.example.restaurent_finder.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurent_finder.Database.ItemEntity
import com.example.restaurent_finder.R
import com.example.restaurent_finder.modal.History_item
import kotlinx.android.synthetic.main.single_cart_item.view.*

class History_Adapter(val context: Context , val itemList : List<History_item>) : RecyclerView.Adapter<History_Adapter.HistorViewHolder>() {
    class HistorViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val txt_restro_name : TextView = view.findViewById(R.id.txt_restro_name)
        val txt_date : TextView = view.findViewById(R.id.txt_date)
       val Single_row_recycler : RecyclerView = view.findViewById(R.id.history_row_recycler)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorViewHolder {
        val view = LayoutInflater.from(parent.context).inflate((R.layout.single_history_row) , parent , false);
        return HistorViewHolder(view);
    }

    override fun getItemCount(): Int {
       return itemList.size
    }

    override fun onBindViewHolder(holder: HistorViewHolder, position: Int) {
        val item = itemList[position]
        holder.txt_restro_name.text = item.Restroname
        holder.txt_date.text = item.Date
        holder.Single_row_recycler.adapter = History_row_Adapter(context , item.ItemArray)
        holder.Single_row_recycler.layoutManager = LinearLayoutManager(context)
    }
}