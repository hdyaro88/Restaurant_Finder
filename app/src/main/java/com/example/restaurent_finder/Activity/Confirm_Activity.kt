package com.example.restaurent_finder.Activity

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.restaurent_finder.R

class Confirm_Activity : AppCompatActivity() {
    lateinit var btn_ok : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmed)
        btn_ok = findViewById(R.id.btn_ok)
        btn_ok.setOnClickListener{
            val intent = Intent(this , Restro_Activity::class.java)
            startActivity(intent)
            finish()
        }
    }
}