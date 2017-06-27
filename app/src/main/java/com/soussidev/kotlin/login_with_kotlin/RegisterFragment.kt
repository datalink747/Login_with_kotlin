package com.soussidev.kotlin.login_with_kotlin

import android.app.Fragment
import android.app.FragmentTransaction
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

class RegisterFragment : Fragment(), View.OnClickListener {

    private var btn_register: AppCompatButton? = null
    private var et_email: EditText? = null
    private var et_password: EditText? = null
    private var et_name: EditText? = null
    private var tv_login: TextView? = null
    private var progress: ProgressBar? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_register, container, false)
        initViews(view)
        return view
    }

    private fun initViews(view: View) {

        btn_register = view.findViewById(R.id.btn_register) as AppCompatButton
        tv_login = view.findViewById(R.id.tv_login) as TextView
        et_name = view.findViewById(R.id.et_name) as EditText
        et_email = view.findViewById(R.id.et_email) as EditText
        et_password = view.findViewById(R.id.et_password) as EditText

        progress = view.findViewById(R.id.progress) as ProgressBar

        btn_register!!.setOnClickListener(this)
        tv_login!!.setOnClickListener(this)
    }


    override fun onClick(v: View) {

        when (v.id) {
            R.id.tv_login -> goToLogin()

            R.id.btn_register -> {

                val name = et_name!!.text.toString()
                val email = et_email!!.text.toString()
                val password = et_password!!.text.toString()

                if (!name.isEmpty() && !email.isEmpty() && !password.isEmpty()) {

                    progress!!.visibility = View.VISIBLE
                    registerProcess(name, email, password)

                } else {

                    Snackbar.make(view!!, "Fields are empty !", Snackbar.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun registerProcess(name: String, email: String, password: String) {

        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val requestInterface = retrofit.create<RequestInterface>(RequestInterface::class.java!!)

        val user = User()
        user.name = name
        user.email = email
        user.setPassword(password)
        val request = ServerRequest()
        request.setOperation(Constants.REGISTER_OPERATION)
        request.setUser(user)
        val response = requestInterface.operation(request)

        response.enqueue(object : Callback<ServerResponse> {
            override fun onResponse(call: Call<ServerResponse>, response: retrofit2.Response<ServerResponse>) {

                val resp = response.body()
                val msg =resp!!.message
                Snackbar.make(view!!, msg.toString(), Snackbar.LENGTH_LONG).show()
                progress!!.visibility = View.INVISIBLE

                if(resp.result.equals("success"))
                {
                    goToLogin()
                }
            }

            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {

                progress!!.visibility = View.INVISIBLE
                Log.d(Constants.TAG, "failed")
                Snackbar.make(view!!, t.localizedMessage, Snackbar.LENGTH_LONG).show()


            }
        })
    }

    private fun goToLogin() {

        val login = LoginFragment()
        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.fragment_frame, login)
        ft.commit()
    }
}
