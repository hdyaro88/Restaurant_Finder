package com.example.restaurent_finder.Activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.android.volley.Request
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.restaurent_finder.R
import com.example.restaurent_finder.network_state.ConnectionManager
import org.json.JSONException
import org.json.JSONObject

class Forgot_Activity : AppCompatActivity() {
    lateinit var btn_next : Button
    lateinit var et_mobile_number : EditText
    lateinit var et_email : EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot)
        title = "Forgot Password"
        btn_next = findViewById(R.id.btn_next)
        et_mobile_number = findViewById(R.id.et_mobile_number)
        et_email = findViewById(R.id.et_email)
        btn_next.setOnClickListener {

            if (ConnectionManager().checkConnectivity(this)) {
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/forgot_password/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", et_mobile_number.text.toString())
                jsonParams.put("email", et_email.text.toString())
                val jsonObjectRequest =
                    object :
                        JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val Maindata = it.getJSONObject("data");
                                val success = Maindata.getBoolean("success")
                                if (success) {
                                    val intent = Intent(this , Reset_Activity::class.java)
                                    startActivity(intent);
                                    Toast.makeText(
                                        this,
                                        "OTP sent to your Email",
                                        Toast.LENGTH_SHORT
                                    ).show();
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
