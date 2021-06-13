package com.example.restaurent_finder.Activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.AsyncTask
import android.os.Bundle
import android.provider.Settings
import android.view.MenuItem
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.app.ActivityCompat
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.example.app2.Login_Activity
import com.example.restaurent_finder.Adapters.Menu_adapter
import com.example.restaurent_finder.Database.RestroDatabase
import com.example.restaurent_finder.Database.RestroEntity
import com.example.restaurent_finder.Fragment.*
import com.example.restaurent_finder.R
import com.google.android.material.navigation.NavigationView

class Restro_Activity  : AppCompatActivity(){
    lateinit var drawerLayout: DrawerLayout
    lateinit var coordinatorLayout: CoordinatorLayout
    lateinit var navigationView: NavigationView
    lateinit var frameLayout: FrameLayout
    lateinit var toolbar: androidx.appcompat.widget.Toolbar
    var previousMenuItem: MenuItem? = null
    lateinit var sharedPreferences: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_restro)
        title = "Restaurent Section"
        drawerLayout = findViewById(R.id.drawer_layout)
        coordinatorLayout = findViewById(R.id.coordinator_layout)
        toolbar = findViewById(R.id.toolbar)
        navigationView = findViewById(R.id.navigation_view)
        sharedPreferences = getSharedPreferences(getString(R.string.prefence_file_name) , Context.MODE_PRIVATE)
        setUpToolbar()
        //so that the homepage fragment opensup at the start
        openDashboard()
        //the home button is defined but not its working
        val actionBarDrawerToggle =  ActionBarDrawerToggle(this , drawerLayout ,
            R.string.open_drawer,
            R.string.close_drawer
        )
        //apply the working on Drawer button
        drawerLayout.addDrawerListener(actionBarDrawerToggle)
        //to change button from back to hamburger alternatively
        actionBarDrawerToggle.syncState()
        //to add listners to the menu items of the menu

        navigationView.setNavigationItemSelectedListener {
            if(previousMenuItem != null) {
                previousMenuItem?.isCheckable = false;
            }
            it.isCheckable = true
            it.isChecked = true
            when(it.itemId) {
                R.id.menu_homepage -> {
                    openDashboard()
                    drawerLayout.closeDrawers()
                    it.isCheckable
                }
                R.id.menu_profile -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame_layout , profile_Fragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Profile"
                    it.isCheckable
                }
                R.id.menu_favourite -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, favourite_Fragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Favourite"
                    it.isCheckable
                }
                R.id.order_history -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, Order_history_Fragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "Order History"
                    it.isCheckable
                }
                R.id.menu_faq -> {
                    supportFragmentManager.beginTransaction().replace(R.id.frame_layout , faq_Fragment()).commit()
                    drawerLayout.closeDrawers()
                    supportActionBar?.title = "FAQ"
                    it.isCheckable
                }
                R.id.btn_logout -> {
                    val dialog = AlertDialog.Builder(this)
                    dialog.setTitle("Attention!!")
                    dialog.setMessage("Do You Really want to Logout?")
                    dialog.setPositiveButton("Yes") { text, listener ->
                        sharedPreferences.edit().putBoolean("isLoggedIn" , false).apply();
                        val intent = Intent(this , Login_Activity::class.java)
                        Restro_menu_Activity.DeleteItems(this).execute();
                        startActivity(intent)
                        finish()
                    }
                    dialog.setNegativeButton("No") { text, listener ->

                    }
                    dialog.create()
                    dialog.show()
                    it.isCheckable
                }
            }
            return@setNavigationItemSelectedListener true
        }
    }
    fun setUpToolbar() {
        setSupportActionBar(toolbar)
        supportActionBar?.title = "ToolBar Title"
        supportActionBar?.setHomeButtonEnabled(true)    //make home button active
        supportActionBar?.setDisplayHomeAsUpEnabled(true)   //make home button visible
    }
    //hamburger button is also a menu item so this function to add functionality to it
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if(id == android.R.id.home) {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return super.onOptionsItemSelected(item)
    }
    fun openDashboard() {
        val fragment = homepage_Fragment()
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.frame_layout, fragment)
        transaction.commit()
        supportActionBar?.title = "Homepage"
        navigationView.setCheckedItem(R.id.menu_homepage)
    }
    fun closeDashboard() {
        drawerLayout.closeDrawers()
    }

    override fun onBackPressed() {
        val frag = supportFragmentManager.findFragmentById(R.id.frame_layout)
        when (frag) {
            !is homepage_Fragment -> openDashboard()
            else -> {
                val dialog = AlertDialog.Builder(this)
                dialog.setTitle("Attention!!")
                dialog.setMessage("Do You Really want to exit?")
                dialog.setPositiveButton("Yes") { text, listener ->
                    ActivityCompat.finishAffinity(this)
                }
                dialog.setNegativeButton("No") { text, listener ->

                }
                dialog.create()
                dialog.show()
            }
        }
    }

}