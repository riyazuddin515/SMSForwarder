package com.riyazuddin.smsforwarder

import androidx.lifecycle.LiveData

interface IEntriesRepository {

    suspend fun insertEntry(entry: Entry)

    fun getAllEntries(): LiveData<List<Entry>>

    suspend fun deleteEntry(entry: Entry)

    fun getAllEntriesStatic(): List<Entry>
}