package com.example.miniapp.viewmodels

import android.annotation.SuppressLint
import android.net.Uri
import com.couchbase.lite.MutableDocument
import com.example.miniapp.models.IDBManager
import java.text.DateFormatSymbols
import java.util.*

class NewTaskViewModel(private val dbManager: IDBManager) {
    fun submit(task: String, dateCreated: Date, dateStart: Date, imageURI: Uri?, completeAddress: String?) {
        val imageURIString = imageURI?.toString() ?: ""
        val address = completeAddress ?: ""

        dbManager.create {
            val doc = MutableDocument()
            doc.setString("task", task)
            doc.setDate("dateCreated", dateCreated)
            doc.setDate("dateStart", dateStart)
            doc.setBoolean("isDone", false)
            doc.setString("imageURI", imageURIString)
            doc.setString("address", address)
        }
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