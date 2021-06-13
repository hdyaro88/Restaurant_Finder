package com.example.restaurent_finder.Fragment

import android.content.Context
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.example.restaurent_finder.Adapters.Favorite_adapter
import com.example.restaurent_finder.Adapters.Homepage_adapter
import com.example.restaurent_finder.Database.RestroDatabase
import com.example.restaurent_finder.Database.RestroEntity
import com.example.restaurent_finder.R
import com.example.restaurent_finder.modal.Restro

class favourite_Fragment : Fragment() {
    lateinit var recyclerFavourite: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: Favorite_adapter
    lateinit var progresslayout: RelativeLayout
    lateinit var progressbar: ProgressBar
    var restrolist = listOf<RestroEntity>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_favourite_, container, false)
        recyclerFavourite = view.findViewById(R.id.recylerfavourite)
        layoutManager = LinearLayoutManager(activity)
        progressbar = view.findViewById(R.id.progressbar)
        progresslayout = view.findViewById(R.id.progrsslayout)
        progresslayout.visibility = View.VISIBLE
        restrolist = RetrieveFavourite(activity as Context).execute().get()
        if(activity != null) {
            progresslayout.visibility = View.GONE
            recyclerAdapter = Favorite_adapter(activity as Context , restrolist)
            recyclerFavourite.adapter = recyclerAdapter
            recyclerFavourite.layoutManager = layoutManager
        }
        return view
    }
    class RetrieveFavourite(val context : Context) : AsyncTask<Void , Void , List<RestroEntity>>() {
        override fun doInBackground(vararg params: Void?): List<RestroEntity> {
            val db = Room.databaseBuilder(context, RestroDatabase :: class.java , "restroList-db").build()
            return db.restroDao().getAllRestro()
        }

    }

}