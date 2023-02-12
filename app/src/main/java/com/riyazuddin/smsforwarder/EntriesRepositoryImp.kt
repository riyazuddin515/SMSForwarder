package com.riyazuddin.smsforwarder

import androidx.lifecycle.LiveData
import javax.inject.Inject

class EntriesRepositoryImp @Inject constructor(val dao: EntryDao): IEntriesRepository {

    override suspend fun insertEntry(entry: Entry) {
        dao.insertEntry(entry)
    }

    override fun getAllEntries(): LiveData<List<Entry>> {
        return dao.getAllEntries()
    }

    override suspend fun deleteEntry(entry: Entry) {
        dao.deleteEntry(entry)
    }

    override fun getAllEntriesStatic(): List<Entry> {
        return dao.getAllEntriesStatic()
    }
}