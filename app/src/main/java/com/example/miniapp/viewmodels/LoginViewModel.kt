package com.example.miniapp.viewmodels

import com.example.miniapp.helper_classes.IPublisher
import com.example.miniapp.helper_classes.ISubscriber
import com.example.miniapp.helper_classes.log
import com.example.miniapp.models.ILoginDBManager

class LoginViewModel(loginView: ISubscriber<Int>, private val dbManager: ILoginDBManager) : IPublisher<Int> {
    private lateinit var loginView: ISubscriber<Int>

    init {
        // publish changes to MainActivity (login)
        this.addSub(loginView)
    }

    fun verify(email: String, password: String) {
        val isEmailRegistered = dbManager.isEmailRegistered(email)
        val isPasswordCorrect: Boolean? = if(isEmailRegistered) dbManager.verifyCredentials(email, password) else null

        // the following integers are used for the login status
        // 0: email is not registered
        // 1: email is registered, password is correct
        // -1: email is registered, password is incorrect
        if (!isEmailRegistered) {
            log("register email $email")
            customNotify { loginView.update(0) }
        } else {
            if (isPasswordCorrect!!) {
                log("password correct for $email")
                customNotify { loginView.update(1) }
            } else {
                log("password incorrect for $email")
                customNotify { loginView.update(-1) }
            }
        }
    }

    fun register(email: String, password: String) {
        dbManager.register(email, password)
    }

    override fun addSub(subscriber: ISubscriber<Int>) {
        loginView = subscriber
    }
}