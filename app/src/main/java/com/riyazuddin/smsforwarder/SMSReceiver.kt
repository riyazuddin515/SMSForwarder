package com.riyazuddin.smsforwarder

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Telephony
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.work.Data
import androidx.work.OneTimeWorkRequest
import androidx.work.WorkManager
import com.riyazuddin.smsforwarder.Constants.CHECKED
import com.riyazuddin.smsforwarder.Constants.RECIPIENT
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SMSReceiver : BroadcastReceiver() {

    @Inject
    lateinit var dao: EntryDao

    override fun onReceive(context: Context, intent: Intent?) {
        if (intent != null && intent.action.equals(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)) {
            val smsMessages = Telephony.Sms.Intents.getMessagesFromIntent(intent)
            smsMessages.forEach { sms ->
                val body = sms.messageBody

                val sharedPreferences = context.getSharedPreferences(
                    RECIPIENT,
                    AppCompatActivity.MODE_PRIVATE
                )

                if (sharedPreferences.getBoolean(
                        CHECKED,
                        false
                    ) && ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.SEND_SMS
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val data = Data.Builder()
                    data.putString("message", body)
                    WorkManager.getInstance(context).enqueue(
                        OneTimeWorkRequest.Builder(SMSWorker::class.java)
                            .setInputData(data.build()).build()
                    )
                }
            }
        }
    }


}