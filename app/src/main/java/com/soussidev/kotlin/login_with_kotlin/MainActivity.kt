package com.soussidev.kotlin.login_with_kotlin

import android.app.Fragment
import android.content.SharedPreferences
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class MainActivity : AppCompatActivity() {
    private var pref: SharedPreferences? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pref = getPreferences(0)
        initFragment()

    }

    private fun initFragment() {
        val fragment: Fragment
        if (pref!!.getBoolean(Constants.IS_LOGGED_IN, false)) {
            fragment = ProfileFragment()
        } else {
            fragment = LoginFragment()
        }
        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.fragment_frame, fragment)
        ft.commit()
    }
}
