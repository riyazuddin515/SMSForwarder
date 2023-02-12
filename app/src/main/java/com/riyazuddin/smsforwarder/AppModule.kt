package com.riyazuddin.smsforwarder

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun provideApplicationContext(
        @ApplicationContext context: Context
    ) = context

    @Provides
    @Singleton
    fun provideNoteDB(
        @ApplicationContext context: Context
    ): SMSEntryDatabase = Room.databaseBuilder(context, SMSEntryDatabase::class.java, "sms_entry_db").build()

    @Provides
    @Singleton
    fun provideNoteDao(
        smsEntryDatabase: SMSEntryDatabase
    ): EntryDao = smsEntryDatabase.getEntryDao()

    @Provides
    @Singleton
    fun provideNoteRepository(
        dao: EntryDao
    ): IEntriesRepository = EntriesRepositoryImp(dao)
}