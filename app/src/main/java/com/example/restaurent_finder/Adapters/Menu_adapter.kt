package com.example.restaurent_finder.Adapters

import android.content.Context
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.restaurent_finder.Activity.Restro_menu_Activity
import com.example.restaurent_finder.Database.ItemEntity
import com.example.restaurent_finder.Database.RestroDatabase
import com.example.restaurent_finder.R
import com.example.restaurent_finder.modal.Restro_item

class Menu_adapter(val context: Context , val btn_proceed : Button, val menulist: List<Restro_item>) :
    RecyclerView.Adapter<Menu_adapter.MenuViewHolder>()  {
    class MenuViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val s_no: TextView = view.findViewById(R.id.txt_food_item_id)
        val item_name: TextView = view.findViewById(R.id.txt_food_item_name)
        val item_price: TextView = view.findViewById(R.id.txt_food_item_price)
        val add_btn: Button = view.findViewById(R.id.btn_add_to_cart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MenuViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate((R.layout.single_food_item_row), parent, false);
        return MenuViewHolder(view);
    }

    override fun getItemCount(): Int {
        return menulist.size;
    }

    override fun onBindViewHolder(holder: MenuViewHolder, position: Int) {
        val item = menulist[position];
        val itemEntity = ItemEntity(
            item.id.toInt() as Int,
            item.name.toString(),
            item.price.toString()
        )
        holder.s_no.text = item.s_no;
        holder.item_name.text = item.name
        holder.item_price.text = "Rs. ${item.price}"
            val checkAdd = DBasyncTask(context, itemEntity, 1).execute().get();
            if (checkAdd) {
                val Added = ContextCompat.getColor(context , R.color.addedToCart)
                holder.add_btn.setBackgroundColor(Added)
            }
            else {
                val NotAdded = ContextCompat.getColor(context , R.color.colorPrimary)
                holder.add_btn.setBackgroundColor(NotAdded);
            }
        holder.add_btn.setOnClickListener {
            if(DBasyncTask(context, itemEntity, 1).execute().get()) {
                val task = DBasyncTask(context, itemEntity, 3).execute()
                val result = task.get()
                if(result) {
                    val hasItems = Restro_menu_Activity.RetrieveItems(context).execute().get()
                    if(hasItems.size != 0) {
                        btn_proceed.visibility = View.VISIBLE
                    }
                    else {
                        btn_proceed.visibility= View.GONE
                    }
                    val NotAdded = ContextCompat.getColor(context , R.color.colorPrimary)
                    holder.add_btn.setBackgroundColor(NotAdded);
                    holder.add_btn.text = "Add"
                }
                else {
                        Toast.makeText(context , "Some Error Occured" , Toast.LENGTH_SHORT).show()
                }
            }
            else {
                btn_proceed.visibility = View.VISIBLE;
                val task = DBasyncTask(context, itemEntity, 2).execute()
                val result = task.get()
                if(result) {
                    val Added = ContextCompat.getColor(context , R.color.addedToCart)
                    holder.add_btn.setBackgroundColor(Added)
                    holder.add_btn.text = "Remove"
                }
                else {
                        Toast.makeText(context , "Some Error Occured" , Toast.LENGTH_SHORT).show()
                }
            }
        }

    }

    class DBasyncTask(val context: Context, val itemEntity: ItemEntity, val mode: Int) :
        AsyncTask<Void, Void, Boolean>() {
        /*
        Mode 1 -> check if the book is favourite or not
        Mode 2 -> add to the favourite
        Mode 3 -> remove from favourite
         */
        val db = Room.databaseBuilder(context, RestroDatabase::class.java, "itemList-db").fallbackToDestructiveMigration()
            .build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
                    val item: ItemEntity? =
                        db.itemDao().getItemById(itemEntity.item_id.toString())
                    db.close()
                    return item != null
                }
                2 -> {
                    db.itemDao().insertItem(itemEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.itemDao().deleteItem(itemEntity)
                    db.close()
                    return true
                }
            }
            return false;
        }
    }
}