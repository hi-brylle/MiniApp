package com.example.miniapp.models

import com.couchbase.lite.MutableDocument

interface IDBManager {
    fun create(customCreate: () -> MutableDocument)
}