package com.example.miniapp.models

import com.couchbase.lite.*
import com.example.miniapp.helper_classes.log
import java.util.*
import kotlin.collections.ArrayList
import com.example.miniapp.helper_classes.ISubscriber as ISubscriber

class UserDBManager(private val dbToUseOrMake: String, private val config: DatabaseConfiguration) : IUserDBManager {
    private val listeners: ArrayList<ISubscriber<Task>> = ArrayList()

    private lateinit var currentDatabase: Database
    private lateinit var changesQuery: Query
    private lateinit var listenerToken: ListenerToken

    override fun openDB() {
        try {
            currentDatabase = Database(dbToUseOrMake, config)
            log("opened " + currentDatabase.name)
            changesQuery = QueryBuilder.select(SelectResult.all())
                    .from(DataSource.database(currentDatabase))
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
            currentDatabase.save(doc)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    override fun listenForChanges() {
        listenerToken = changesQuery.addChangeListener { change: QueryChange ->
            for (result in change.results) {
                val all = result.getDictionary(currentDatabase!!.name)
                val task = all.getString("task")
                val dateCreated = all.getDate("dateCreated")
                val dateStart = all.getDate("dateStart")
                val isDone = all.getBoolean("isDone")
                val imageURI = all.getString("imageURI")
                val address = all.getString("address")
                val t = Task(task, dateCreated, dateStart)

                // TODO: change setDone based on date and time
                t.isDone = isDone
                t.imageURI = imageURI
                t.address = address
                log("Task Retrieved: $task")
                log("Created Retrieved: $dateCreated")
                log("Start Retrieved: $dateStart")
                log("URI Retrieved: $imageURI")
                log("Address Retrieved: $address")
                notifySubs(t)
            }
        }
        try {
            changesQuery.execute()
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    override fun notifySubs(task: Task) {
        for (subscriber in listeners) {
            subscriber.update(task)
        }
    }

    override fun closeDB() {
        changesQuery.removeChangeListener(listenerToken)

        try {
            currentDatabase.close()
            log("closed " + currentDatabase.name)
        } catch (e: CouchbaseLiteException) {
            e.printStackTrace()
        }
    }

    override fun addSub(subscriber: ISubscriber<Task>?) {
        subscriber?.let { listeners.add(it) }
    }
}