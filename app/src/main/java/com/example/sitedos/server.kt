package com.example.sitedos

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
class server {
    fun sendFCMMessage(targetToken: String, title: String, message: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val serverKey =
                "AAAAQMQBC7U:APA91bHSUwCYX7Dd1lDu9N3qVeONBhc1O9VjJT8CvsDFQl2fw2IdYFAXrud7jY0yecbASPiP1OQBETWWpPVpnbZ7Krdhpl0H_ofbgi9JL1Z7c8vjgnQBuzuRBr4vKNjHpSAKtPyHZdM7"  // Replace with your FCM server key

            val client = OkHttpClient()

            val json = """
        {
            "to": "$targetToken",
            "notification": {
                "title": "$title",
                "body": "$message"
            }
        }
    """.trimIndent()

            val requestBody = json.toRequestBody("application/json".toMediaType())

            val request = Request.Builder()
                .url("https://fcm.googleapis.com/fcm/send")
                .post(requestBody)
                .addHeader("Authorization", "key=$serverKey")
                .addHeader("Content-Type", "application/json")
                .build()

            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    // Handle the error
                    println("FCM message sending failed: ${response.code} ${response.message}")
                } else {
                    // Handle the success
                    println("FCM message sent successfully")
                }
            }
        }
    }

    fun main() {
        // Example usage
        val targetDeviceToken = "TARGET_DEVICE_TOKEN"  // Replace with the target device token
        val notificationTitle = "New Message"
        val notificationMessage = "Hello, this is a new message!"

        sendFCMMessage(targetDeviceToken, notificationTitle, notificationMessage)
    }
}