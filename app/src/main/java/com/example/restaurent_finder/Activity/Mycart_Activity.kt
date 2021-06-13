package com.example.restaurent_finder.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.restaurent_finder.Adapters.Menu_adapter
import com.example.restaurent_finder.Adapters.Mycart_adapter
import com.example.restaurent_finder.Database.ItemEntity
import com.example.restaurent_finder.R
import com.example.restaurent_finder.modal.Restro_item
import com.example.restaurent_finder.network_state.ConnectionManager
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

class Mycart_Activity : AppCompatActivity() {
    lateinit var btn_place_order: Button
    lateinit var txt_order_from: TextView
    lateinit var restro_id: String
    lateinit var restro_name: String
    lateinit var toolbar: Toolbar
    lateinit var progressBar: ProgressBar
    lateinit var progresslayout: RelativeLayout
    lateinit var sharedPreferences: SharedPreferences
    var cartItems: ArrayList<ItemEntity> = arrayListOf();
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerCart: RecyclerView
    lateinit var cart_adapter: Mycart_adapter
    var total_price = 0;
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mycart)
        if (intent != null) {
            restro_name = intent.getStringExtra("restro_name").toString()
            restro_id = intent.getStringExtra("restro_id").toString()
        }
        btn_place_order = findViewById(R.id.btn_place_order)
        txt_order_from = findViewById(R.id.txt_ordering_from)
        progressBar = findViewById(R.id.progressbar)
        progresslayout = findViewById(R.id.progrsslayout)
        toolbar = findViewById(R.id.toolbar)
        recyclerCart = findViewById(R.id.cart_recycler)
        layoutManager = LinearLayoutManager(this)
        sharedPreferences =
            getSharedPreferences(getString(R.string.prefence_file_name), Context.MODE_PRIVATE);
        txt_order_from.text = "Ordering From :- ${restro_name}"
        setUpToolbar()
        progresslayout.visibility = View.VISIBLE;
        cartItems =
            Restro_menu_Activity.RetrieveItems(this).execute().get() as ArrayList<ItemEntity>;
        if (cartItems.size != 0) {
            progresslayout.visibility = View.GONE;
            cart_adapter = Mycart_adapter(this, findViewById(R.id.btn_place_order), cartItems);
            recyclerCart.adapter = cart_adapter;
            recyclerCart.layoutManager = layoutManager
        } else {
            Toast.makeText(this, "Error Occurred", Toast.LENGTH_SHORT).show()
        }

        btn_place_order.setOnClickListener {
            if (ConnectionManager().checkConnectivity(this)) {
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/place_order/fetch_result/"
                val jsonParams = JSONObject()
                val jsonItemArray = JSONArray()
                for (i in 0 until cartItems.size) {
                    val obj = JSONObject()
                    obj.put("food_order_id", cartItems[i].item_id.toString())
                    jsonItemArray.put(obj);
                    total_price += cartItems[i].ItemPrice.toInt();
                }
                jsonParams.put("user_id", sharedPreferences.getString("user_id", "null"))
                jsonParams.put(
                    "restaurant_id", restro_id
                )
                jsonParams.put("total_cost", total_price.toString())
                jsonParams.put("food", jsonItemArray)
                val jsonObjectRequest =
                    object :
                        JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                progresslayout.visibility = View.GONE;
                                val Maindata = it.getJSONObject("data");
                                val success = Maindata.getBoolean("success")
                                if (success) {
                                    val intent = Intent(this, Confirm_Activity::class.java)
                                    startActivity(intent)
                                    finish()
                                } else {
                                    val error = Maindata.getString("errorMessage")
                                    Toast.makeText(
                                        this, error,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } catch (e: JSONException) {
                                Toast.makeText(
                                    this,
                                    "Some Unexpected Error Occured",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }, Response.ErrorListener {
                            Toast.makeText(
                                this,
                                "Volley Error Occured",
                                Toast.LENGTH_SHORT
                            ).show()
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
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Error")
                dialog.setMessage("Internet Connection Not Found")
                dialog.setPositiveButton("Open Settings") { text, listener ->
                    val intent = Intent(Settings.ACTION_WIRELESS_SETTINGS)
                    startActivity(intent)
                    this.finish()
                }
                dialog.setNegativeButton("Exit") { text, listener ->
                    ActivityCompat.finishAffinity(this)
                }
                dialog.create()
                dialog.show()
            }
        }
    }

    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "My Cart"
        supportActionBar?.setHomeButtonEnabled(true)    //make home button active
        supportActionBar?.setDisplayHomeAsUpEnabled(true)   //make home button visible
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId;
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}