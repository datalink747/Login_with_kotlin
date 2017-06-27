package com.soussidev.kotlin.login_with_kotlin

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

import com.soussidev.kotlin.login_with_kotlin.models.ServerRequest
import com.soussidev.kotlin.login_with_kotlin.models.ServerResponse

/**
 * Created by Soussi on 27/06/2017.
 */
interface RequestInterface {

    @POST("soussidev_login_kotlin/")
    fun operation(@Body request: ServerRequest): Call<ServerResponse>
}