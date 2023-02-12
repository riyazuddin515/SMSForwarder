package com.riyazuddin.smsforwarder

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface EntryDao {

    @Insert(onConflict =  OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: Entry)

    @Query("SELECT * FROM entries")
    fun getAllEntries(): LiveData<List<Entry>>

    @Query("SELECT * FROM entries")
    fun getAllEntriesStatic(): List<Entry>

    @Delete
    suspend fun deleteEntry(entry: Entry)
}