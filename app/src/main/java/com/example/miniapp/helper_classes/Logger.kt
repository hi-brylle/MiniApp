@file:JvmName("Logger")
package com.example.miniapp.helper_classes

import android.util.Log

@JvmOverloads fun log(message: String, type: Char = 'i') {
    val tag = "MY TAG"
    when(type) {
        'v' -> Log.v(tag, message)
        'd' -> Log.d(tag, message)
        'i' -> Log.i(tag, message)
        'w' -> Log.w(tag, message)
        'e' -> Log.e(tag, message)
    }
}
