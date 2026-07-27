package com.dlh.vulnerapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.Toast

class SpoofableReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        // VULNERABILITY: This receiver is exported and does not check the sender's identity/permission.
        // It performs a sensitive action based on the intent action.
        if ("com.dlh.vulnerapp.ACTION_RESET_PASSWORD" == intent.action) {
            val newPass = intent.getStringExtra("new_password")
            if (newPass != null) {
                // Simulating a critical state change based on spoofed intent
                Log.d("VulnerApp", "Password reset triggered via Broadcast! New Pass: $newPass")
                Toast.makeText(context, "IPC Attack: Password Reset to $newPass", Toast.LENGTH_LONG).show()
            }
        }
    }
}
