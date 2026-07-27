package com.dlh.vulnerapp

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SecretsActivity : AppCompatActivity() {

    // VULNERABILITY: Hardcoded Secrets
    private val AWS_ACCESS_KEY = "AKIAIOSFODNN7EXAMPLE" // Fake AWS Key
    private val AWS_SECRET_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY" // Fake Secret
    private val API_TOKEN = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.e30.Et9HF98yRYq" // Fake JWT

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_secrets)

        val statusText = findViewById<TextView>(R.id.statusText)
        val usernameInput = findViewById<EditText>(R.id.usernameInput)
        val passwordInput = findViewById<EditText>(R.id.passwordInput)
        val loginButton = findViewById<Button>(R.id.loginButton)

        // VULNERABILITY: Logging sensitive data
        Log.d("SecretsActivity", "Initialized with API Token: $API_TOKEN")

        loginButton.setOnClickListener {
            val user = usernameInput.text.toString()
            val pass = passwordInput.text.toString()

            if (user.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter credentials", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // VULNERABILITY: Insecure Storage (SharedPreferences with MODE_PRIVATE but plain text)
            // While MODE_PRIVATE is default, storing passwords in plain text is bad practice.
            // DLH should detect "insecure_storage" or similar patterns.
            val sharedPref = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
            with(sharedPref.edit()) {
                putString("username", user)
                putString("password", pass) // Storing password in plain text
                putString("aws_key", AWS_ACCESS_KEY) // Persisting hardcoded secret
                apply()
            }

            statusText.text = "Logged in & Saved! \n(Check /data/data/.../shared_prefs/UserPrefs.xml)"
            statusText.setTextColor(android.graphics.Color.GREEN)
            
            // TODO: Remove this debug toast before production
            Toast.makeText(this, "Secret Key Used: $AWS_SECRET_KEY", Toast.LENGTH_LONG).show()
        }
    }
}
