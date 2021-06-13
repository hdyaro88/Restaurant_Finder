package com.example.restaurent_finder.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.restaurent_finder.Adapters.Homepage_adapter
import com.example.restaurent_finder.R
import com.example.restaurent_finder.modal.Restro
import com.example.restaurent_finder.network_state.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class Register_Activity : AppCompatActivity() {
    lateinit var et_reg_username: EditText
    lateinit var et_reg_email: EditText
    lateinit var et_reg_phone: EditText
    lateinit var et_reg_deliveryaddress: EditText
    lateinit var et_reg_password: EditText
    lateinit var btn_register: Button
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         sharedPreferences = getSharedPreferences(
             getString(R.string.prefence_file_name) ,
             Context.MODE_PRIVATE
         )
        setContentView(R.layout.activity_register)
        title = "Register Yourself"
        et_reg_username = findViewById(R.id.et_reg_username)
        et_reg_email = findViewById(R.id.et_reg_email)
        et_reg_phone = findViewById(R.id.et_reg_phone)
        et_reg_deliveryaddress = findViewById(R.id.et_reg_deliveryaddress)
        et_reg_password = findViewById(R.id.et_reg_password)
        btn_register = findViewById(R.id.btn_register)
        btn_register.setOnClickListener {

            if (ConnectionManager().checkConnectivity(this)) {
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/register/fetch_result/"
                val jsonParams = JSONObject()
                jsonParams.put("name", et_reg_username.text.toString())
                jsonParams.put("mobile_number", et_reg_phone.text.toString())
                jsonParams.put("password", et_reg_password.text.toString())
                jsonParams.put("address", et_reg_deliveryaddress.text.toString())
                jsonParams.put("email", et_reg_email.text.toString())
                val jsonObjectRequest =
                    object :
                        JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val Maindata = it.getJSONObject("data");
                                val success = Maindata.getBoolean("success")
                                val data = Maindata.getJSONObject("data");
                                if (success) {
                                    sharedPreferences.edit().putString("name" , data.getString("name")).apply()
                                    sharedPreferences.edit().putString("email" , data.getString("email")).apply()
                                    sharedPreferences.edit().putString("mobile_number" , data.getString("mobile_number")).apply()
                                    sharedPreferences.edit().putString("address" , data.getString("address")).apply()
                                    val intent = Intent(this , Restro_Activity::class.java)
                                    startActivity(intent);
                                    Toast.makeText(
                                        this,
                                        "Welcome Aboard , Have a Yummy Journey!!",
                                        Toast.LENGTH_SHORT
                                    ).show();
                                    finish()
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


    }
}
