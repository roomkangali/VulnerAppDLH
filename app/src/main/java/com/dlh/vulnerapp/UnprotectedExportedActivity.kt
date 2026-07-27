package com.dlh.vulnerapp

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class UnprotectedExportedActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ipc)

        // VULNERABILITY: This activity is exported and can be launched by other apps.
        // It blindly trusts the intent data.
        val data = intent.getStringExtra("secret_override")
        val statusText = findViewById<TextView>(R.id.ipcStatus)

        if (data != null) {
            statusText.text = "Sensitive Action Triggered by External App!\nData: $data"
        } else {
            statusText.text = "Unprotected Activity Launched.\nWaiting for malicious intent..."
        }
    }
}
