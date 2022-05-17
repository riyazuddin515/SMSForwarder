package com.riyazuddin.smsforwarder

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.provider.Telephony
import android.telephony.SmsManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.riyazuddin.smsforwarder.Constants.CHECKED
import com.riyazuddin.smsforwarder.Constants.CONTAINS
import com.riyazuddin.smsforwarder.Constants.FORWARD_TO_NUMBER
import com.riyazuddin.smsforwarder.Constants.RECIPIENT
import java.util.*

class SMSReceiver : BroadcastReceiver() {

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
                    sharedPreferences.getString(FORWARD_TO_NUMBER, null)?.let { number ->
                        val filter = sharedPreferences.getString(CONTAINS, null)
                        filter?.let {
                            if (it.isEmpty() || it.isBlank())
                                sendSMS(number, body)
                            else if (body.lowercase(Locale.getDefault()).contains(
                                    it.lowercase(
                                        Locale.getDefault()
                                    )
                                )
                            ) {
                                sendSMS(number, body)
                            }
                        }
                    }
                }
            }
        }
    }

    private fun sendSMS(number: String, body: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage("+91$number", null, body, null, null)
    }
}