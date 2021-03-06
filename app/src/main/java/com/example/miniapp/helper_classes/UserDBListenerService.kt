package com.example.miniapp.helper_classes

import android.app.Service
import android.content.Intent
import android.os.Bundle
import android.os.IBinder
import com.couchbase.lite.*
import com.example.miniapp.models.Repository
import com.example.miniapp.models.Task

class UserDBListenerService : Service() {
    private lateinit var userDatabase: Database
    private lateinit var changesQuery: Query
    private lateinit var listenerToken: ListenerToken

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        intent.getStringExtra("email")?.let {
            log("DB LISTENER SERVICE STARTED")
            Repository.register(this)
            start(it)
        } ?: run {log("NULL EMAIL. SERVICE FAILED TO START")}

        return START_STICKY
    }

    override fun stopService(name: Intent?): Boolean {
        Repository.unregister(this)
        changesQuery.removeChangeListener(listenerToken)
        userDatabase.close()
        return super.stopService(name)
    }

    private fun start(dbName: String) {
        userDatabase = Database(dbName, DatabaseConfiguration(this))
        changesQuery = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(userDatabase))

        listenForChanges()
    }

    private fun listenForChanges() {
        listenerToken = changesQuery.addChangeListener { change: QueryChange ->
            for (result in change.results) {
                val all = result.getDictionary(userDatabase.name)
                val task = all.getString("task")
                val dateCreated = all.getDate("dateCreated")
                val dateStart = all.getDate("dateStart")
                val isDone = all.getBoolean("isDone")
                val imageURI = all.getString("imageURI")
                val address = all.getString("address")
                val t = Task(task, dateCreated, dateStart)

                t.isDone = isDone
                t.imageURI = imageURI
                t.address = address

                val intent = Intent()
                val bundle = Bundle()
                bundle.putSerializable("taskObj", t)
                intent.putExtra("taskExtra", bundle)
                intent.action = "INTENT_ACTION_DB_CHANGED"
                sendBroadcast(intent)
            }
        }
        try {
            changesQuery.execute()
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }
}