package com.example.restaurent_finder.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.restaurent_finder.Adapters.Homepage_adapter
import com.example.restaurent_finder.Adapters.Menu_adapter
import com.example.restaurent_finder.Database.ItemEntity
import com.example.restaurent_finder.Database.RestroDatabase
import com.example.restaurent_finder.R
import com.example.restaurent_finder.modal.Restro_item
import com.example.restaurent_finder.network_state.ConnectionManager
import kotlinx.android.synthetic.main.activity_restro.*
import org.json.JSONException

class Restro_menu_Activity:AppCompatActivity() {
    lateinit var restro_id : String
    lateinit var resto_name : String
    lateinit var menuRecycler : RecyclerView
    lateinit var layoutManager: RecyclerView.LayoutManager
    lateinit var recyclerAdapter: Menu_adapter
    lateinit var progresslayout: RelativeLayout
    lateinit var progressbar: ProgressBar
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    lateinit var btn_proceed : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restro_menu)
        val itemInfoList  = arrayListOf<Restro_item>();
        progressbar = findViewById(R.id.progressbar)
        progresslayout = findViewById(R.id.progrsslayout)
        layoutManager = LinearLayoutManager(this)
        menuRecycler = findViewById(R.id.menu_recycler)
        toolbar = findViewById(R.id.toolbar)
        btn_proceed = findViewById(R.id.btn_proceed_cart)
        setUpToolbar()
        if(intent != null) {
            restro_id = intent.getStringExtra("restro_id").toString();
            resto_name = intent.getStringExtra("restro_name").toString()
            supportActionBar?.title = resto_name;
//                Toast.makeText(this , "done${restro_id}" , Toast.LENGTH_SHORT).show()
        }
        else {
            Toast.makeText(this , "Some Error Occurred" , Toast.LENGTH_SHORT).show()

        }
        btn_proceed.visibility = View.GONE

        btn_proceed.setOnClickListener{
            val intent = Intent(this , Mycart_Activity::class.java);
            intent.putExtra("restro_id" , restro_id)
            intent.putExtra("restro_name" , resto_name)
            startActivity(intent)
        }
        progresslayout.visibility = View.VISIBLE;
    if (ConnectionManager().checkConnectivity(this)) {
        val queue = Volley.newRequestQueue(this)
        val url = "http://13.235.250.119/v2/restaurants/fetch_result/${restro_id}"
        val jsonObjectRequest =
            object :
                JsonObjectRequest(Request.Method.GET, url, null, Response.Listener {
                    try {
                        progresslayout.visibility = View.GONE;
                        val Maindata = it.getJSONObject("data");
                        val success = Maindata.getBoolean("success")
                        if (success) {
                            val data = Maindata.getJSONArray("data")
                            for (i in 0 until data.length()) {
                                val itemJSONObject = data.getJSONObject(i)
                                val item = Restro_item(
                                    itemJSONObject.getString("id"),
                                    (i + 1).toString(),
                                    itemJSONObject.getString("name"),
                                    itemJSONObject.getString("cost_for_one")
                                )
                                itemInfoList.add(item)
                            }
                            recyclerAdapter = Menu_adapter(this , findViewById(R.id.btn_proceed_cart) , itemInfoList);
                            menuRecycler.layoutManager = LinearLayoutManager(this);
                            menuRecycler.adapter = recyclerAdapter;
                        } else {
                            val error = Maindata.getString("errorMessage")
                            Toast.makeText(
                                this,  error,
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
    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Please Wait.."
        supportActionBar?.setHomeButtonEnabled(true)    //make home button active
        supportActionBar?.setDisplayHomeAsUpEnabled(true)   //make home button visible
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId;
        if(id == android.R.id.home) {
            DeleteItems(this).execute()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
            DeleteItems(this).execute()
            finish()
        }

    class RetrieveItems(val context : Context) : AsyncTask<Void, Void, List<ItemEntity>>() {
        override fun doInBackground(vararg params: Void?): List<ItemEntity> {
            val db = Room.databaseBuilder(context, RestroDatabase :: class.java , "itemList-db").build()
            return db.itemDao().getAllItems()
        }
    }
    class DeleteItems(val context: Context) : AsyncTask<Void , Void , Void>() {
        override fun doInBackground(vararg params: Void?): Void {
            val db = Room.databaseBuilder(context , RestroDatabase:: class.java , "itemList-db").build()
            return  db.itemDao().deleteAllItems();
        }
    }
}