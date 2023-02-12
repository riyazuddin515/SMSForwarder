package com.riyazuddin.smsforwarder

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EntryViewModel @Inject constructor(
    val entriesRepository: EntriesRepositoryImp
) : ViewModel() {

    val loadCurrentUserStatus: LiveData<List<Entry>> = entriesRepository.getAllEntries()

    fun insertEntry(entry: Entry) {
        viewModelScope.launch {
            entriesRepository.insertEntry(entry)
        }
    }

    fun deleteEntry(entry: Entry) {
        viewModelScope.launch {
            entriesRepository.deleteEntry(entry)
        }
    }
}