package com.riyazuddin.smsforwarder

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "entries")
data class Entry(
    @PrimaryKey(autoGenerate = true)
    val id: Int? = null,
    val forwardTo: String,
    val contains: String
)
