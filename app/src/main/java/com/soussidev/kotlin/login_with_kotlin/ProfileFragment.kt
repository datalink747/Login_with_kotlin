package com.soussidev.kotlin.login_with_kotlin

import android.app.AlertDialog
import android.app.Fragment
import android.app.FragmentTransaction
import android.content.DialogInterface
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

class ProfileFragment : Fragment(), View.OnClickListener {

    private var tv_name: TextView? = null
    private var tv_email: TextView? = null
    private var tv_message: TextView? = null
    private var pref: SharedPreferences? = null
    private var btn_change_password: AppCompatButton? = null
    private var btn_logout: AppCompatButton? = null
    private var et_old_password: EditText? = null
    private var et_new_password: EditText? = null
    private var dialog: AlertDialog? = null
    private var progress: ProgressBar? = null


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        initViews(view)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        pref = activity.getPreferences(0)
        tv_name!!.text = "Welcome : " + pref!!.getString(Constants.NAME, "")!!
        tv_email!!.text = pref!!.getString(Constants.EMAIL, "")

    }

    private fun initViews(view: View) {

        tv_name = view.findViewById(R.id.tv_name) as TextView
        tv_email = view.findViewById(R.id.tv_email) as TextView
        btn_change_password = view.findViewById(R.id.btn_chg_password) as AppCompatButton
        btn_logout = view.findViewById(R.id.btn_logout) as AppCompatButton
        btn_change_password!!.setOnClickListener(this)
        btn_logout!!.setOnClickListener(this)

    }

    private fun showDialog() {

        val builder = AlertDialog.Builder(activity)
        val inflater = activity.layoutInflater
        val view = inflater.inflate(R.layout.dialog_change_password, null)
        et_old_password = view.findViewById(R.id.et_old_password) as EditText
        et_new_password = view.findViewById(R.id.et_new_password) as EditText
        tv_message = view.findViewById(R.id.tv_message) as TextView
        progress = view.findViewById(R.id.progress) as ProgressBar
        builder.setView(view)
        builder.setTitle("Change Password")
        builder.setPositiveButton("Change Password") { dialog, which -> }
        builder.setNegativeButton("Cancel") { dialog, which -> dialog.dismiss() }
        dialog = builder.create()
        dialog!!.show()
        dialog!!.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
            val old_password = et_old_password!!.text.toString()
            val new_password = et_new_password!!.text.toString()
            if (!old_password.isEmpty() && !new_password.isEmpty()) {

                progress!!.visibility = View.VISIBLE
                changePasswordProcess(pref!!.getString(Constants.EMAIL, ""), old_password, new_password)

            } else {

                tv_message!!.visibility = View.VISIBLE
                tv_message!!.text = "Fields are empty"
            }
        }
    }

    override fun onClick(v: View) {
        when (v.id) {

            R.id.btn_chg_password -> showDialog()
            R.id.btn_logout -> logout()
        }
    }

    private fun logout() {
        val editor = pref!!.edit()
        editor.putBoolean(Constants.IS_LOGGED_IN, false)
        editor.putString(Constants.EMAIL, "")
        editor.putString(Constants.NAME, "")
        editor.putString(Constants.UNIQUE_ID, "")
        editor.apply()
        goToLogin()
    }

    private fun goToLogin() {

        val login = LoginFragment()
        val ft = fragmentManager.beginTransaction()
        ft.replace(R.id.fragment_frame, login)
        ft.commit()
    }

    private fun changePasswordProcess(email: String, old_password: String, new_password: String) {

        val retrofit = Retrofit.Builder()
                .baseUrl(Constants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()

        val requestInterface = retrofit.create<RequestInterface>(RequestInterface::class.java!!)

        val user = User()
        user.email = email
        user.setOld_password(old_password)
        user.setNew_password(new_password)
        val request = ServerRequest()
        request.setOperation(Constants.CHANGE_PASSWORD_OPERATION)
        request.setUser(user)
        val response = requestInterface.operation(request)

        response.enqueue(object : Callback<ServerResponse> {
            override fun onResponse(call: Call<ServerResponse>, response: retrofit2.Response<ServerResponse>) {

                val resp = response.body()
                if (resp!!.result == Constants.SUCCESS) {
                    progress!!.visibility = View.GONE
                    tv_message!!.visibility = View.GONE
                    dialog!!.dismiss()
                    val msg=resp.message
                    Snackbar.make(view!!, msg.toString(), Snackbar.LENGTH_LONG).show()



                } else {
                    progress!!.visibility = View.GONE
                    tv_message!!.visibility = View.VISIBLE
                    tv_message!!.text = resp.message

                }
            }

            override fun onFailure(call: Call<ServerResponse>, t: Throwable) {

                Log.d(Constants.TAG, "failed")
                progress!!.visibility = View.GONE
                tv_message!!.visibility = View.VISIBLE
                tv_message!!.text = t.localizedMessage


            }
        })
    }
}
