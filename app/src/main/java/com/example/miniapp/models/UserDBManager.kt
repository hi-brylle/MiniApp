package com.example.miniapp.models

import com.couchbase.lite.*
import com.example.miniapp.helper_classes.log
import java.util.*

class UserDBManager(private val dbToUseOrMake: String, private val config: DatabaseConfiguration) : IUserDBManager {
    private lateinit var userDatabase: Database

    override fun openDB() {
        try {
            userDatabase = Database(dbToUseOrMake, config)
            log("opened ${userDatabase.name}")
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    override fun create(task: String, dateCreated: Date, dateStart: Date, imageURIString: String, address: String) {
        val doc = MutableDocument()
        doc.setString("task", task)
        doc.setDate("dateCreated", dateCreated)
        doc.setDate("dateStart", dateStart)
        doc.setBoolean("isDone", false)
        doc.setString("imageURI", imageURIString)
        doc.setString("address", address)
        try {
            userDatabase.save(doc)
            log("write to ${userDatabase.name} successful")
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    override fun closeDB() {
        try {
            userDatabase.close()
            log("closed ${userDatabase.name}")
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }
}