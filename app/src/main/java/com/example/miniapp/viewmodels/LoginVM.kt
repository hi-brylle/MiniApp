package com.example.miniapp.viewmodels

import com.couchbase.lite.*
import com.example.miniapp.helper_classes.IPublisher
import com.example.miniapp.helper_classes.ISubscriber
import com.example.miniapp.helper_classes.hash
import com.example.miniapp.models.IDBManager

class LoginVM(private var loginView: ISubscriber<Int>, private val dbManager: IDBManager): IPublisher<Int> {
    init {
        addSub(loginView)
    }

    fun register(email: String, password: String){
        dbManager.create {
            val doc = MutableDocument()
            doc.setString("email", email)
            doc.setString("hash", hash(password))
        }
    }

    private fun isEmailRegistered(email: String): Boolean {
        val resultSet = dbManager.read { currentDatabase ->
             QueryBuilder.select(SelectResult.property("email"))
                    .from(DataSource.database(currentDatabase))
                    .where(Expression.property("email").equalTo(Expression.string(email)))
        }

        /*
            if resultSet is null, it means no user is registered yet, therefore any inputted email
            is, by default, not yet registered; no need for a run{} clause returning false
        */
        resultSet?.let {
            it.forEach { result -> if(email == result.getString("email")) return true
            }
        }

        return false
    }
    
    private fun verifyCredentials(email: String, password: String): Boolean {
        val resultSet: ResultSet? = dbManager.read { currentDatabase ->
            QueryBuilder.select(SelectResult.property("email"), SelectResult.property("hash"))
                    .from(DataSource.database(currentDatabase))
                    .where(Expression.property("email").equalTo(Expression.string(email))
                            .add(Expression.property("hash").equalTo(Expression.string(hash(password)))))
        }

        resultSet?.let {
            it.forEach { result ->
                if (email == result.getString("email") && hash(password) == result.getString("hash")) {
                    return true
                }
            }
        }
        
        return false
    }

    fun verify(email: String, password: String){
        val isEmailRegistered = isEmailRegistered(email)
        val isPasswordCorrect: Boolean? = if(isEmailRegistered) verifyCredentials(email, password) else null

        /*
            the following integers are used for the login status
            0: email is not registered
            1: email is registered, password is correct
            -1: email is registered, password is incorrect
        */
        isPasswordCorrect?.let { correct ->
            if(correct) customNotify {loginView.update(1)} else customNotify {loginView.update(-1)}
        } ?: run { customNotify {loginView.update(0)} }
    }

    override fun addSub(subscriber: ISubscriber<Int>) {
        loginView = subscriber
    }
}