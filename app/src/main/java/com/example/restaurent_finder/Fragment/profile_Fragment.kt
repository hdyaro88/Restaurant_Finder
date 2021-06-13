package com.example.restaurent_finder.Fragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.example.restaurent_finder.R

class profile_Fragment : Fragment() {
    lateinit var username: TextView
    lateinit var email: TextView
    lateinit var mobile_number: TextView
    lateinit var address: TextView
    lateinit var sharedPreferences: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile_, container, false)
        sharedPreferences = activity?.getSharedPreferences(
            getString(R.string.prefence_file_name),
            Context.MODE_PRIVATE
        )!!
        username = view.findViewById(R.id.txt_username)
        email = view.findViewById(R.id.txt_email)
        mobile_number = view.findViewById(R.id.txt_mobile_number)
        address = view.findViewById(R.id.txt_address)
        username.text = sharedPreferences.getString("name", "null")
        email.text = sharedPreferences.getString("email" , "null")
        mobile_number.text = sharedPreferences.getString("mobile_number" , "null")
        address.text = sharedPreferences.getString("address" , "null")
        return view;
    }

}