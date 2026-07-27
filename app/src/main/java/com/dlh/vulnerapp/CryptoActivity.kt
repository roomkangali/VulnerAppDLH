package com.dlh.vulnerapp

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.util.Random
// We must actually USE the class for it to appear in bytecode/smali
import androidx.biometric.BiometricPrompt 

class CryptoActivity : AppCompatActivity() {

    private var isVip = false
    private lateinit var statusText: TextView

    // DUMMY USAGE to force "Landroidx/biometric/BiometricPrompt;" into bytecode
    private var dummyPrompt: BiometricPrompt? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crypto)

        statusText = findViewById(R.id.statusText)
        val vipButton = findViewById<Button>(R.id.vipButton)
        val tokenButton = findViewById<Button>(R.id.tokenButton)
        val bioButton = findViewById<Button>(R.id.bioButton)

        // 1. Universal Logic Flaw
        vipButton.setOnClickListener {
            checkVipStatus()
        }

        // 2. Insecure Random Number Generation
        tokenButton.setOnClickListener {
            generateWeakToken()
        }

        // 3. Biometric Bypass
        bioButton.setOnClickListener {
            performBiometricAuth()
        }
    }

    private fun checkVipStatus() {
        if (isVip) {
            statusText.text = "Welcome, VIP User! Access Granted."
        } else {
            statusText.text = "Regular User. Access Denied."
            isVip = true 
            Toast.makeText(this, "Debug: VIP Mode Enabled for next click", Toast.LENGTH_SHORT).show()
        }
    }

    private fun generateWeakToken() {
        // VULNERABILITY: Explicitly using java.util.Random (Insecure PRNG)
        // Ensure we use the full class name or explicit import to avoid Kotlin Random confusion
        val random = java.util.Random() 
        val token = random.nextLong()
        statusText.text = "Session Token (Weak): $token"
    }

    private fun performBiometricAuth() {
        // VULNERABILITY: Improper handling of BiometricPrompt authentication result.
        // We reference the class here to ensure the scanner sees it.
        // In a real vulnerability, this would be a callback that sets a boolean.
        
        // Mocking the vulnerability logic
        val success = biometricLibraryCall()
        
        // Logic Flaw: '|| true' bypasses the check
        if (success || true) { 
            statusText.text = "Biometric Auth Success! (Or Bypassed)"
        } else {
            statusText.text = "Auth Failed."
        }
    }

    private fun biometricLibraryCall(): Boolean {
        // Just returning false to simulate failure that is bypassed
        return false
    }
}
