@file:JvmName("PWHash")
package com.example.miniapp.helper_classes

import java.security.MessageDigest

fun hash(plainText: String): String {
    val hexChars = "0123456789ABCDEF"
    val bytes = MessageDigest.getInstance("MD5").digest(plainText.toByteArray())
    val result = StringBuilder(bytes.size * 2)

    bytes.forEach {
        val i = it.toInt()
        result.append(hexChars[i shr 4 and 0x0f])
        result.append(hexChars[i and 0x0f])
    }

    return result.toString()
}