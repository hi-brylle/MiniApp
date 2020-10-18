package com.example.miniapp.models

import com.couchbase.lite.CouchbaseLiteException
import com.couchbase.lite.Database
import com.couchbase.lite.DatabaseConfiguration
import com.couchbase.lite.MutableDocument
import com.example.miniapp.helper_classes.log

class DBManager(private val dbName: String, private val config: DatabaseConfiguration) : IDBManager {
    private lateinit var currentDatabase: Database

    override fun create(customCreate: () -> MutableDocument) {
        val doc = customCreate()

        try {
            currentDatabase = Database(dbName, config)
            currentDatabase.save(doc)
            log("write to ${currentDatabase.name} successful")
            currentDatabase.close()
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }
}