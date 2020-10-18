package com.example.miniapp.models

interface ILoginDBManager {
    fun register(email: String, password: String)
    fun isEmailRegistered(email: String): Boolean
    fun verifyCredentials(email: String, password: String): Boolean
}