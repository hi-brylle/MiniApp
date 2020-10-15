package com.example.miniapp.models

import java.io.Serializable
import java.util.*

class Task(val task: String, val dateCreated: Date, val dateStart: Date) : Serializable {
    var isDone = false
    var isExpanded = false
    var imageURI = ""
    var address = ""
}

fun Task.isSame(another: Task): Boolean {
    return this.task == another.task &&
            this.dateCreated == another.dateCreated &&
            this.dateStart == another.dateStart &&
            this.imageURI == another.imageURI &&
            this.address == another.address
}
