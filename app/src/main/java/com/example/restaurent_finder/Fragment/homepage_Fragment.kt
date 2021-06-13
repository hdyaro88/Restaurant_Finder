package com.example.restaurent_finder.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.media.audiofx.BassBoost
import android.os.AsyncTask
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.restaurent_finder.network_state.ConnectionManager
import com.example.restaurent_finder.Adapters.Homepage_adapter
import com.example.restaurent_finder.R
import com.example.restaurent_finder.modal.Restro
import org.json.JSONException
import android.provider.Settings
import androidx.room.Room
import com.example.restaurent_finder.Database.RestroDatabase
import com.example.restaurent_finder.Database.RestroEntity


class homepage_Fragment : Fragment() {
    lateinit var recyclerDashboard: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: Homepage_adapter
    lateinit var progresslayout: RelativeLayout
    lateinit var progressbar: ProgressBar
    val restroInfoList = arrayListOf<Restro>();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_homepage_, container, false)
        recyclerDashboard = view.findViewById(R.id.recyclerDashboard)
        layoutManager = LinearLayoutManager(activity)
        progressbar = view.findViewById(R.id.progressbar)
        progresslayout = view.findViewById(R.id.progrsslayout)
        recyclerAdapter = Homepage_adapter(activity as Context, restroInfoList)
        recyclerDashboard.adapter = recyclerAdapter
        recyclerDashboard.layoutManager = layoutManager
        progresslayout.visibility = View.VISIBLE
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val queue = Volley.newRequestQueue(activity as Context)
            val url = "http://13.235.250.119/v2/restaurants/fetch_result/"
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        progresslayout.visibility = View.GONE
                        val Maindata = it.getJSONObject("data");
                        val success = Maindata.getBoolean("success")
                        if (success) {
                            val data = Maindata.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restroJSONObject = data.getJSONObject(i)
                                val restroofobject = Restro(
                                    restroJSONObject.getString("id"),
                                    restroJSONObject.getString("name"),
                                    restroJSONObject.getString("cost_for_one"),
                                    restroJSONObject.getString("rating"),
                                    restroJSONObject.getString("image_url")
                                )
                                    restroInfoList.add(restroofobject)
                            }
                            recyclerAdapter =
                                Homepage_adapter(activity as Context, restroInfoList)
                            recyclerDashboard.adapter = recyclerAdapter
                            recyclerDashboard.layoutManager = layoutManager
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Error Occured In Fetching Data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    catch (e : JSONException) {
                        Toast.makeText(activity as Context, "Some Unexpected Error Occured" , Toast.LENGTH_SHORT).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(activity as Context, "Volley Error Occured" , Toast.LENGTH_SHORT).show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        headers["token"] = "f5ee84a1195aad"
                        return headers;
                    }
                }
            queue.add(jsonObjectRequest);
        }
        else {
            val dialog = AlertDialog.Builder(activity as Context)
            dialog.setTitle("Error")
            dialog.setMessage("Internet Connection Not Found")
            dialog.setPositiveButton("Open Settings") { text, listener ->
                val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                startActivity(intent)
                activity?.finish()
            }
            dialog.setNegativeButton("Exit") { text, listener ->
                ActivityCompat.finishAffinity(activity as Activity)
            }
            dialog.create()
            dialog.show()
        }

        return view;
    }

}