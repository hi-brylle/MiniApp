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

    private enum class STATUS(val value: Int) {
        NOT_REGISTERED(0),
        CORRECT_PW(1),
        WRONG_PW(-1)
    }

    fun verify(email: String, password: String){
        val isPasswordCorrect: Boolean? = if(isEmailRegistered(email)) verifyCredentials(email, password) else null

        isPasswordCorrect?.let { correct ->
            if(correct) customNotify {loginView.update(STATUS.CORRECT_PW.value)} else customNotify {loginView.update(STATUS.WRONG_PW.value)}
        } ?: run { customNotify {loginView.update(STATUS.NOT_REGISTERED.value)} }
    }

    override fun addSub(subscriber: ISubscriber<Int>) {
        loginView = subscriber
    }
}