package com.example.miniapp.models

import java.util.*

class Task(val task: String, val dateCreated: Date, val dateStart: Date) {
    var isDone = false
    var isExpanded = false
    var imageURI = ""
    var address = ""
}