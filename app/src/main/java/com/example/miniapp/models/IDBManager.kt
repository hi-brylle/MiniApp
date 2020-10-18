package com.example.miniapp.models

import com.couchbase.lite.Database
import com.couchbase.lite.MutableDocument
import com.couchbase.lite.Query
import com.couchbase.lite.ResultSet

interface IDBManager {
    fun create(customCreate: () -> MutableDocument)
    fun read(customQuery: (database: Database) -> Query): ResultSet?
}