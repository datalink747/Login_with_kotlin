package com.soussidev.kotlin.login_with_kotlin.models

/**
 * Created by Soussi on 27/06/2017.
 */
class ServerRequest {

    private var operation: String? = null
    private var user: User? = null

    fun setOperation(operation: String) {
        this.operation = operation
    }

    fun setUser(user: User) {
        this.user = user
    }
}
