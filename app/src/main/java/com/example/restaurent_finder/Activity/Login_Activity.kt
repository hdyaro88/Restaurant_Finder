package com.example.app2

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.restaurent_finder.R
import com.example.restaurent_finder.Activity.Register_Activity
import com.example.restaurent_finder.Activity.Restro_Activity
import com.example.restaurent_finder.network_state.ConnectionManager
import org.json.JSONException
import android.provider.Settings
import android.app.AlertDialog
import android.widget.EditText
import androidx.core.app.ActivityCompat
import org.json.JSONObject

class Login_Activity : AppCompatActivity() {
    lateinit var btnLogin: Button
    lateinit var btnRegister: TextView
    lateinit var btnForgot: TextView
    lateinit var et_mobile_number : EditText
    lateinit var et_password : EditText
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        title = "Login"
        btnLogin = findViewById(R.id.btn_login)
        btnRegister = findViewById(R.id.txt_register)
        btnForgot = findViewById(R.id.txt_forgot)
        et_mobile_number = findViewById(R.id.et_mobile_number)
        et_password = findViewById(R.id.et_password)
        sharedPreferences =
            getSharedPreferences(getString(R.string.prefence_file_name), Context.MODE_PRIVATE);
        val isLoggedIn = sharedPreferences.getBoolean("isLoggedIn" , false);
        if(isLoggedIn) {
            val intent = Intent(this, Restro_Activity::class.java)
            startActivity(intent)
            finish()
        }
        btnRegister.setOnClickListener {
            val intent = Intent(this, Register_Activity::class.java)
            startActivity(intent);
        }
        btnForgot.setOnClickListener {
            val intent =
                Intent(this, com.example.restaurent_finder.Activity.Forgot_Activity::class.java)
            startActivity(intent);
        }
        btnLogin.setOnClickListener {
            if(et_mobile_number.text.toString() == "" || et_password.text.toString() == "") {
                Toast.makeText(this , "Fields cannot be empty" , Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ConnectionManager().checkConnectivity(this)) {
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/login/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", et_mobile_number.text.toString())
                jsonParams.put("password", et_password.text.toString())
                val jsonObjectRequest =
                    object :
                        JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val Maindata = it.getJSONObject("data");
                                val success = Maindata.getBoolean("success")
                                val data = Maindata.getJSONObject("data");
                                if (success) {
                                    sharedPreferences.edit()
                                        .putString("user_id", data.getString("user_id")).apply()
                                    sharedPreferences.edit()
                                        .putString("name", data.getString("name")).apply()
                                    sharedPreferences.edit()
                                        .putString("email", data.getString("email")).apply()
                                    sharedPreferences.edit()
                                        .putString("mobile_number", data.getString("mobile_number"))
                                        .apply()
                                    sharedPreferences.edit()
                                        .putString("address", data.getString("address")).apply()
//                                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT)
//                                        .show()
                                    val intent = Intent(this, Restro_Activity::class.java)
                                    startActivity(intent)
                                    sharedPreferences.edit().putBoolean("isLoggedIn" , true).apply()
                                    finish()
                                    Toast.makeText(
                                        this,
                                        "Welcome Aboard , Have a Yummy Journey!!",
                                        Toast.LENGTH_SHORT
                                    ).show();
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


}

