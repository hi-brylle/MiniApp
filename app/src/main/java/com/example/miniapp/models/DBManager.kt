package com.example.miniapp.models

import com.couchbase.lite.*
import com.example.miniapp.helper_classes.log

class DBManager(private val dbName: String, private val config: DatabaseConfiguration) : IDBManager {
    private lateinit var currentDatabase: Database

    private fun openDB(){
        currentDatabase = Database(dbName, config)
    }

    override fun create(customCreate: () -> MutableDocument) {
        openDB()
        val doc = customCreate()

        try {
            currentDatabase.save(doc)
            log("write to ${currentDatabase.name} successful")
            currentDatabase.close()
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    override fun read(customQuery: (database: Database) -> Query): ResultSet {
        openDB()
        val query = customQuery(currentDatabase)
        lateinit var resultSet: ResultSet

        try {
            resultSet = query.execute()
            currentDatabase.close()
        } catch (e: CouchbaseLiteException){
            e.printStackTrace()
        }

        return resultSet
    }
}