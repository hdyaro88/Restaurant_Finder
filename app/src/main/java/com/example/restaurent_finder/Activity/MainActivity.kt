package com.example.restaurent_finder.Activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.app2.Login_Activity
import com.example.restaurent_finder.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        title="Welcome"
       val postDelayed = Handler().postDelayed({
            val i = Intent(this, Login_Activity::class.java)
            startActivity(i)
           finish()
        }, 3000)
    }
}