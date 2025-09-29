package com.example.rcaisms.data.sender

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.telephony.SmsManager
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

// sendMessage: sends an SMS to `customerPhone` with `customerMessage` using SmsManager.
fun sendMessage(customerPhone: String, customerMessage: String, context: Context) {
    // The original code prefixes a '0' — be careful: blindly adding '0' may break international numbers.
    val formattedPhone = "0$customerPhone"

    try {
        // Runtime permission constant for sending SMS
        val permission = Manifest.permission.SEND_SMS

        // Check whether SEND_SMS is already granted
        if (ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            // Get default SmsManager (works for most cases; see notes below for multi-SIM)
            val smsManager: SmsManager = SmsManager.getDefault()

            // Send a one-shot text message
            // (destinationAddress, scAddress(null uses default), text, sentIntent, deliveryIntent)
            smsManager.sendTextMessage(formattedPhone, null, customerMessage, null, null)

            // Inform the user (UI feedback)
            Toast.makeText(context, "Successful Message", Toast.LENGTH_LONG).show()
        } else {
            // If permission not granted, request it from the Activity.
            // Casting context to Activity may crash if caller did not pass an Activity context.
            ActivityCompat.requestPermissions(context as Activity, arrayOf(permission), 0)
        }
    } catch (e: Exception) {
        // Log/print the exception (use proper logging in production) and show a user-facing error
        e.printStackTrace()
        Toast.makeText(context, "Error sending SMS", Toast.LENGTH_LONG).show()
    }
}
