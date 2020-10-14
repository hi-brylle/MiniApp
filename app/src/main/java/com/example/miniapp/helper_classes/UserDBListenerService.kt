package com.example.miniapp.helper_classes

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.couchbase.lite.*
import com.example.miniapp.models.Task
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.launch

class UserDBListenerService : Service() {
    private lateinit var userDatabase: Database
    private lateinit var changesQuery: Query
    private lateinit var listenerToken: ListenerToken

    override fun onBind(p0: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        intent.getStringExtra("email")?.let { start(it) } ?:
            run {log("user db name is null")}

        return START_STICKY
    }

    private fun start(dbName: String) {
        log("LISTENER SERVICE STARTED")
        userDatabase = Database(dbName, DatabaseConfiguration(this))
        changesQuery = QueryBuilder.select(SelectResult.all())
                .from(DataSource.database(userDatabase))

        CoroutineScope(IO).launch{
            listenForChanges()
        }

    }

    private fun listenForChanges(){
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

                log("FROM SERVICE: Task Retrieved: $task")
                log("FROM SERVICE: Created Retrieved: $dateCreated")
                log("FROM SERVICE: Start Retrieved: $dateStart")
                log("FROM SERVICE: URI Retrieved: $imageURI")
                log("FROM SERVICE: Address Retrieved: $address")

                //notifySubs(t)
            }
        }
        try {
            changesQuery.execute()
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

}