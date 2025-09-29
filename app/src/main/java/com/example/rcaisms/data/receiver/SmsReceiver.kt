package com.example.rcaisms.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.provider.Telephony
import android.telephony.SmsMessage
import android.util.Log

class SmsReceiverManager(private val context: Context) {

    private var receiver: BroadcastReceiver? = null
    private var isRegistered = false

    fun register(onMessageReceived: (from: String?, body: String) -> Unit) {
        if (isRegistered) return

        receiver = object : BroadcastReceiver() {
            override fun onReceive(ctx: Context?, intent: Intent?) {
                try {
                    if (intent == null) return
                    val action = intent.action
                    if (action == Telephony.Sms.Intents.SMS_RECEIVED_ACTION ||
                        action == "android.provider.Telephony.SMS_RECEIVED"
                    ) {

                        // Safely obtain SmsMessage[] from the intent (handles multipart SMS PDUs)
                        val smsMessages: Array<SmsMessage> =
                            Telephony.Sms.Intents.getMessagesFromIntent(intent)
                        val sb = StringBuilder()
                        var origin: String? = null

                        // Concatenate parts (for multipart messages) and capture sender
                        for (sms in smsMessages) {
                            origin = sms.originatingAddress
                            sb.append(sms.messageBody)
                        }

                        val body = sb.toString()
                        // Deliver the parsed message to the provided callback
                        onMessageReceived(origin, body)
                    }
                } catch (t: Throwable) {
                    // Log parsing errors — do not crash the app
                    Log.e("SmsReceiverManager", "Error parsing incoming SMS", t)
                }
            }
        }

        // Intent filter for SMS_RECEIVED. Priority is high, but note modern Android imposes restrictions.
        val filter = IntentFilter().apply {
            addAction(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
            priority = 1000
        }

        // Register the receiver dynamically with the given filter
        context.registerReceiver(receiver, filter)
        isRegistered = true
    }

    /**
     * Unregister the receiver if it is registered.
     */
    fun unregister() {
        try {
            if (receiver != null && isRegistered) {
                context.unregisterReceiver(receiver)
            }
        } catch (ignored: Exception) {
            // Swallow exceptions during unregister to avoid crashing (e.g., already-unregistered)
        } finally {
            receiver = null
            isRegistered = false
        }
    }
}