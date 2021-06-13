package com.example.restaurent_finder.Adapters

import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.AsyncTask
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.restaurent_finder.Activity.Restro_Activity
import com.example.restaurent_finder.Activity.Restro_menu_Activity
import com.example.restaurent_finder.Database.RestroDatabase
import com.example.restaurent_finder.Database.RestroEntity
import com.example.restaurent_finder.R
import com.example.restaurent_finder.modal.Restro
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.single_restro_row.view.*
import java.security.AccessController.getContext

class Homepage_adapter(val context : Context , val restroList : List<Restro> ) :
    RecyclerView.Adapter<Homepage_adapter.HomepageViewHolder>() {

    class HomepageViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        var txtRestroName : TextView = view.findViewById(R.id.txtRestroName)
        var txtRestoPrice : TextView = view.findViewById(R.id.txtRestroPrice)
        var txtRestroRating : TextView = view.findViewById(R.id.txtRestroRating)
        var imgRestroImage : ImageView = view.findViewById(R.id.img_restro)
        var btnaddtofav : Button = view.findViewById(R.id.btnaddtofav)
        var restro_ll : LinearLayout = view.findViewById(R.id.restro_ll)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HomepageViewHolder {
        val view = LayoutInflater.from(parent.context).inflate((R.layout.single_restro_row) , parent , false)
        return HomepageViewHolder(view)
    }

    override fun getItemCount(): Int {
        return restroList.size;
    }

    override fun onBindViewHolder(holder: HomepageViewHolder, position: Int) {
        val restro = restroList[position]
        val restroEntity = RestroEntity(
            restro.id.toInt() as Int ,
            restro.Restroname.toString(),
            restro.RestroPrice.toString(),
            restro.Rating.toString(),
            restro.image.toString()
        )
        holder.txtRestroName.text = restro.Restroname
        holder.txtRestoPrice.text = "Rs. ${restro.RestroPrice}"
        holder.txtRestroRating.text = restro.Rating
        Picasso.get().load(restro.image).error(R.drawable.default_book_cover)
            .into(holder.imgRestroImage);
        holder.restro_ll.setOnClickListener{
                val intent = Intent(context , Restro_menu_Activity::class.java);
                 intent.putExtra("restro_id" , restro.id);
                 intent.putExtra("restro_name" , restro.Restroname)
            context.startActivity(intent)
        }
        val checkFav = DBasyncTask(context, restroEntity, 1).execute()
        val isFav = checkFav.get()
        if (!isFav) {
            val NoFav = ContextCompat.getDrawable(context, R.drawable.ic_not_fav)
            holder.btnaddtofav.setBackgroundDrawable(NoFav)
        }
        else {
            val Fav = ContextCompat.getDrawable(context , R.drawable.ic_favourite)
            holder.btnaddtofav.setBackgroundDrawable(Fav)
        }
            holder.btnaddtofav.setOnClickListener {
                if(DBasyncTask(context , restroEntity , 1).execute().get()) {
                    val task = DBasyncTask(context , restroEntity , 3).execute()
                    val result = task.get()
                    if(result) {
                        val NoFav = ContextCompat.getDrawable(context, R.drawable.ic_not_fav)
                        holder.btnaddtofav.setBackgroundDrawable(NoFav)
                    }
                    else {
                        Toast.makeText(context , "Some Error Occured" , Toast.LENGTH_SHORT).show()
                    }
                }
                else {
                    val task = DBasyncTask(context , restroEntity , 2).execute()
                    val result = task.get()
                    if(result) {
                        val Fav = ContextCompat.getDrawable(context , R.drawable.ic_favourite)
                        holder.btnaddtofav.setBackgroundDrawable(Fav)
                    }
                    else {
                        Toast.makeText(context , "Some Error Occured" , Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }
    class DBasyncTask(val context: Context, val restroEntity : RestroEntity, val mode : Int) : AsyncTask<Void, Void, Boolean>() {
        /*
        Mode 1 -> check if the book is favourite or not
        Mode 2 -> add to the favourite
        Mode 3 -> remove from favourite
         */
        val db = Room.databaseBuilder(context, RestroDatabase::class.java, "restroList-db").build()

        override fun doInBackground(vararg params: Void?): Boolean {
            when (mode) {
                1 -> {
//                    val restro : RestroEntity? = db.restroDao().getRestroById(Restroid = RestroEntity.toString())
                    val restro: RestroEntity? =
                        db.restroDao().getRestroById(restroEntity.restro_id.toString())
                    db.close()
                    return restro != null
                }
                2 -> {
                    db.restroDao().insertRestro(restroEntity)
                    db.close()
                    return true
                }
                3 -> {
                    db.restroDao().deleteRestro(restroEntity)
                    db.close()
                    return true
                }
            }
            return false;
        }
    }
}