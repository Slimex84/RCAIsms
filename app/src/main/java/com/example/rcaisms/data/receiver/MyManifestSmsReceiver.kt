package com.example.rcaisms.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log

class MyManifestSmsReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        try {
            // If there's no intent, nothing to do
            if (intent == null) return

            // Get the action from the incoming intent
            val action = intent.action

            // Only handle the SMS_RECEIVED action (use the Telephony constant where possible)
            if (action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION ||
                action == "android.provider.Telephony.SMS_RECEIVED"
            ) {

                // Retrieve SmsMessage array from the intent — this handles multipart PDUs for you
                val smsMessages: Array<SmsMessage> =
                    Telephony.Sms.Intents.getMessagesFromIntent(intent)

                // Use a StringBuilder to concatenate message parts (for multipart SMS)
                val sb = StringBuilder()
                var origin: String? = null

                // Iterate all message parts and accumulate sender and body
                for (sms in smsMessages) {
                    origin = sms.originatingAddress // sender (may be null on some carriers)
                    sb.append(sms.messageBody)     // append body part
                }

                val body = sb.toString()
                // Log the assembled message. In production, avoid logging sensitive data.
                Log.i("MyManifestSmsReceiver", "SMS received from $origin: $body")

                // TODO: forward the message to an app component (e.g., via a local broadcast or a WorkManager job)
                // or update app state via a repository — avoid long work on the BroadcastReceiver thread.
            }
        } catch (t: Throwable) {
            // Catch-all logging for unexpected parsing errors
            Log.e("MyManifestSmsReceiver", "Error parsing incoming SMS", t)
        }
    }
}