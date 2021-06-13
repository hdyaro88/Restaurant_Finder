package com.example.restaurent_finder.Adapters

import android.content.Context
import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.restaurent_finder.Database.ItemEntity
import com.example.restaurent_finder.R

class Mycart_adapter(val context : Context, val btn_place_order : Button, val cartItem : List<ItemEntity>) : RecyclerView.Adapter<Mycart_adapter.MycartViewHolder>() {
    var total_price = 0;
    lateinit var sharedPreferences: SharedPreferences
    class MycartViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val txt_item_name : TextView = view.findViewById(R.id.txt_food_item_name)
        val txt_item_price : TextView = view.findViewById(R.id.txt_food_item_price)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MycartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate((R.layout.single_cart_item) , parent , false);
        sharedPreferences = context.getSharedPreferences(context.getString(R.string.prefence_file_name) , Context.MODE_PRIVATE);
        return MycartViewHolder(view);
    }

    override fun getItemCount(): Int {
        return cartItem.size;
    }

    override fun onBindViewHolder(holder: MycartViewHolder, position: Int) {
        val item = cartItem[position]
        holder.txt_item_name.text = item.ItemName
        holder.txt_item_price.text = "Rs. ${item.ItemPrice}"
        total_price += item.ItemPrice.toInt();
        btn_place_order.text = "Place Order(Total:Rs. ${total_price.toString()})";
        sharedPreferences.edit().putString("total_price" , total_price.toString());
    }
}