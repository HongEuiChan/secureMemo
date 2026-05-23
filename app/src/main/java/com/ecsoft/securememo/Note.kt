package com.ecsoft.securememo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class Note(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // ACCOUNT, TOKEN, CARD, NOTE
    val title: String,
    val field1: String? = null, // ID or Card Number
    val field2: String? = null, // Password, Token, or Card PIN
    val field3: String? = null, // CVC or Extra info
    val field4: String? = null, // Extra Notes
    val createdAt: Long = System.currentTimeMillis()
)
