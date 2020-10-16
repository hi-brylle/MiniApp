package com.example.miniapp.viewmodels

import android.annotation.SuppressLint
import android.net.Uri
import com.example.miniapp.helper_classes.log
import com.example.miniapp.models.IUserDBManager
import java.text.DateFormatSymbols
import java.util.*

class NewTaskViewModel(private val dbManager: IUserDBManager) {
    fun openDB() {
        dbManager.openDB()
    }

    fun closeDB() {
        dbManager.closeDB()
    }

    fun submit(task: String, created: Date, start: Date, imageURI: Uri?, completeAddress: String?) {
        val imageURIString = imageURI?.toString() ?: ""
        val address = completeAddress ?: ""
        dbManager.create(task, created, start, imageURIString, address)
        log("Task: $task")
        log("Created: $created")
        log("Start: $start")
        log("URI: $imageURIString")
        log("Address: $address")
    }

    fun isValid(dateTimeSelected: Date): Boolean {
        val now = Calendar.getInstance().time
        return dateTimeSelected.after(now)
    }

    companion object {
        @JvmStatic
        fun dateRepresentation(year: Int, month: Int, day: Int): String {
            // needs localization
            val monthName = DateFormatSymbols().months[month]
            return "$day $monthName $year"
        }

        @JvmStatic
        @SuppressLint("DefaultLocale")
        fun timeRepresentation(hr: Int, min: Int): String {
            var hour = hr % 12
            if (hour == 0) hour = 12
            return String.format("%02d:%02d %s", hour, min, if (hr < 12) "am" else "pm")
        }
    }
}