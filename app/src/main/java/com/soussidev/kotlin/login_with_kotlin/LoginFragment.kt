package com.soussidev.kotlin.login_with_kotlin

import android.app.Fragment
import android.app.FragmentTransaction
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.widget.Snackbar

import android.support.v7.widget.AppCompatButton
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import com.soussidev.kotlin.login_with_kotlin.models.ServerRequest
import com.soussidev.kotlin.login_with_kotlin.models.ServerResponse
import com.soussidev.kotlin.login_with_kotlin.models.User


import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class LoginFragment : Fragment(), View.OnClickListener {

    private var btn_login: AppCompatButton? = null
    private var et_email: EditText? = null
    private var et_password: EditText? = null
    private var tv_register: TextView? = null
    private var progress: ProgressBar? = null
    private var pref: SharedPreferences? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_login, container, false)
        initViews(view)
        return view
    }

    private fun initViews(view: View) {

        pref = activity.getPreferences(0)

        btn_login = view.findViewById(R.id.btn_login) as AppCompatButton
        tv_register = view.findViewById(R.id.tv_register) as TextView
        et_email = view.findViewById(R.id.et_email) as EditText
        et_password = view.findViewById(R.id.et_password) as EditText

        progress = view.findViewById(R.id.progress) as ProgressBar

        btn_login!!.setOnClickListener(this)
        tv_register!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {

        when (v.id) {

            R.id.tv_register -> goToRegister()

            R.id.btn_login -> {
                val email = et_email!!.text.toString()
                val password = et_password!!.text.toString()

                if (!email.isEmpty() && !password.isEmpty()) {

                    progress!!.visibility = View.VISIBLE
                    loginProcess(email, password)

                } else {

                    Snackbar.make(view!!, "Fields are empty !", Snackbar.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun loginProcess(email: String, password: String) {

        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val requestInterface = retrofit.create<RequestInterface>(RequestInterface::class.java!!)

        val user = User()
        user.email = email
        user.setPassword(password)
        val request = ServerRequest()
        request.setOperation(Constants.LOGIN_OPERATION)
        request.setUser(user)
        val response = requestInterface.operation(request)

        response.enqueue(object : Callback<ServerResponse> {
            override fun onResponse(call: Call<ServerResponse>, response: retrofit2.Response<ServerResponse>) {

                val resp = response.body()
                val msg=resp!!.message
                Snackbar.make(view!!, msg.toString(), Snackbar.LENGTH_LONG).show()

                if (resp.result == Constants.SUCCESS) {
                    val editor = pref!!.edit()
                    editor.putBoolean(Constants.IS_LOGGED_IN, true)
                    editor.putString(Constants.EMAIL, resp.user!!.email)
                    editor.putString(Constants.NAME, resp.user.name)
                    editor.putString(Constants.UNIQUE_ID, resp.user.unique_id)
                    editor.apply()
                    goToProfile()

                }
                progress!!.visibility = View.INVISIBLE
            }

            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {

                progress!!.visibility = View.INVISIBLE
                Log.d(Constants.TAG, "failed")
                Snackbar.make(view!!, t.localizedMessage, Snackbar.LENGTH_LONG).show()

            }
        })
    }

    private fun goToRegister() {

        val register = RegisterFragment()
        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.fragment_frame, register)
        ft.commit()
    }

    private fun goToProfile() {

        val profile = ProfileFragment()
        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.fragment_frame, profile)
        ft.commit()
    }
}
