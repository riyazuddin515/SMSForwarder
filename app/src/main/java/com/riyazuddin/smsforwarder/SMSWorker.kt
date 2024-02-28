package com.riyazuddin.smsforwarder

import android.content.Context
import android.telephony.SmsManager
import android.util.Log
import androidx.hilt.work.HiltWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import java.util.regex.Matcher
import java.util.regex.Pattern

@HiltWorker
class SMSWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val entryRepositoryImp: EntriesRepositoryImp
) :
    Worker(context, params) {

    override fun doWork(): Result {
        val message = inputData.getString("message") ?: ""
        Log.i("SMSWorker message", message)
        val entries = entryRepositoryImp.getAllEntriesStatic()
        entries.forEach { entry ->
            if (message.contains(entry.contains)) {
                val otp = extractOTPFromMessage(message)
                sendSMS(entry.forwardTo, "Your OTP for ${entry.contains} is $otp. SMSForwarder")
            }
        }
        return Result.success()
    }

    private fun sendSMS(number: String, body: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage("+91$number", null, body, null, null)
    }

    private fun extractOTPFromMessage(message: String): String? {
        // Define the regular expression pattern for the OTP
        val pattern = Pattern.compile("\\b(\\d{4}|\\d{6})\\b")
        val matcher: Matcher = pattern.matcher(message)
        // Extract the first occurrence of the OTP from the message text
        return if (matcher.find()) {
            matcher.group(0)
        } else {
            message
        }
    }
}