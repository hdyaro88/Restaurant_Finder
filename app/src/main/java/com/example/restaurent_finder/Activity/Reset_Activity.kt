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
import com.example.app2.Login_Activity
import com.example.restaurent_finder.R
import com.example.restaurent_finder.network_state.ConnectionManager
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.et_password
import kotlinx.android.synthetic.main.activity_reset.*
import org.json.JSONException
import org.json.JSONObject

class Reset_Activity : AppCompatActivity() {
    lateinit var et_mobile_number: EditText
    lateinit var et_password: EditText
    lateinit var et_OTP: EditText
    lateinit var btn_reset: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)
        title = "Reset Password"
        et_mobile_number = findViewById(R.id.et_mobile_number)
        et_password = findViewById(R.id.et_password)
        btn_reset = findViewById(R.id.btn_reset)
        et_OTP = findViewById(R.id.et_otp)
        btn_reset.setOnClickListener {

            if (ConnectionManager().checkConnectivity(this)) {
                val queue = Volley.newRequestQueue(this)
                val url = "http://13.235.250.119/v2/reset_password/fetch_result"
                val jsonParams = JSONObject()
                jsonParams.put("mobile_number", et_mobile_number.text.toString())
                jsonParams.put("password", et_password.text.toString())
                jsonParams.put("otp", et_OTP.text.toString())
                val jsonObjectRequest =
                    object :
                        JsonObjectRequest(Request.Method.POST, url, jsonParams, Response.Listener {
                            try {
                                val Maindata = it.getJSONObject("data");
                                val success = Maindata.getBoolean("success")
                                if (success) {
                                    val intent = Intent(this, Login_Activity::class.java)
                                    startActivity(intent);
                                    Toast.makeText(
                                        this,
                                        Maindata.getString("successMessage"),
                                        Toast.LENGTH_SHORT
                                    ).show();
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
}