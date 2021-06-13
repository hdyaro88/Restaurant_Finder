package com.example.restaurent_finder.Fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
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
import com.example.restaurent_finder.Adapters.History_Adapter
import com.example.restaurent_finder.R
import com.example.restaurent_finder.modal.History_item
import com.example.restaurent_finder.modal.Restro_item
import com.example.restaurent_finder.network_state.ConnectionManager
import org.json.JSONArray
import org.json.JSONException

class Order_history_Fragment : Fragment() {
    lateinit var recyclerhistory: RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: History_Adapter
    lateinit var progresslayout: RelativeLayout
    lateinit var progressbar: ProgressBar
    val HistoryItemsList: ArrayList<History_item> = arrayListOf();
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order_history, container, false)
        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.prefence_file_name),
            Context.MODE_PRIVATE
        )!!
        val user_id = sharedPreferences.getString("user_id", "null")
        recyclerhistory = view.findViewById(R.id.order_history_recycler_view)
        layoutManager = LinearLayoutManager(activity)
        progressbar = view.findViewById(R.id.progressbar)
        progresslayout = view.findViewById(R.id.progrsslayout)
        progresslayout.visibility = View.VISIBLE
        if (ConnectionManager().checkConnectivity(activity as Context)) {
            val queue = Volley.newRequestQueue(activity as Context)
            val url = "http://13.235.250.119/v2/orders/fetch_result/${user_id}"
            val jsonObjectRequest =
                object : JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        progresslayout.visibility = View.GONE
                        val Maindata = it.getJSONObject("data");
                        val success = Maindata.getBoolean("success")
                        if (success) {
                            val data = Maindata.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val restro_name = data.getJSONObject(i).getString("restaurant_name");
                                var date = data.getJSONObject(i).getString("order_placed_at")
                                if(date.contains(" ")) {
                                    date = date.substring(0 , date.indexOf(" "));
                                }

                                val FoodItems: JSONArray =
                                    data.getJSONObject(i).getJSONArray("food_items");
                                val ItemList : ArrayList<Restro_item> = arrayListOf();
                                for (j in 0 until FoodItems.length()) {
                                    val ItemJSONObject = FoodItems.getJSONObject(j)
                                    val ItemObject = Restro_item(
                                        ItemJSONObject.getString("food_item_id"),
                                        j.toString(),
                                        ItemJSONObject.getString("name"),
                                        ItemJSONObject.getString("cost")
                                    );
                                    ItemList.add(ItemObject)
                                }
                                val Single_history_item = History_item(
                                    restro_name ,
                                    date,
                                    ItemList
                                )
                                HistoryItemsList.add(Single_history_item)
                            }
                            recyclerAdapter =
                                History_Adapter(activity as Context, HistoryItemsList)
                            recyclerhistory.adapter = recyclerAdapter
                            recyclerhistory.layoutManager = layoutManager
                        } else {
                            Toast.makeText(
                                activity as Context,
                                "Error Occured In Fetching Data",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } catch (e: JSONException) {
                        Toast.makeText(
                            activity as Context,
                            "Some Unexpected Error Occured",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }, Response.ErrorListener {
                    Toast.makeText(activity as Context, "Volley Error Occured", Toast.LENGTH_SHORT)
                        .show()
                }) {
                    override fun getHeaders(): MutableMap<String, String> {
                        val headers = HashMap<String, String>()
                        headers["Content-Type"] = "application/json"
                        headers["token"] = "f5ee84a1195aad"
                        return headers;
                    }
                }
            queue.add(jsonObjectRequest);
        } else {
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