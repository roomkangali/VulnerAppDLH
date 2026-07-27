package com.dlh.vulnerapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ScrollView
import android.graphics.Color
import android.view.Gravity
import android.content.Intent
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        val scrollView = ScrollView(this)
        scrollView.setBackgroundColor(Color.parseColor("#0D0D0D")) // Dark Cyberpunk BG
        
        val container = LinearLayout(this)
        container.orientation = LinearLayout.VERTICAL
        container.setPadding(32, 32, 32, 32)
        
        val title = Button(this)
        title.text = "Vulnerability App Android DLH"
        title.setTextColor(Color.GREEN)
        title.setBackgroundColor(Color.TRANSPARENT)
        title.textSize = 20f
        container.addView(title)
        
        // Add buttons for categories (Placeholder for now)
        val categories = listOf(
            "Secrets & Auth",
            "WebView Exploits",
            "SQL Injection",
            "Insecure Storage",
            "IPC Attacks",
            "Logic Flaws",
            "GraphQL Injection",
            // v2 modules
            "Insecure Deserialization",
            "Unsafe Reflection",
            "PendingIntent Hijack",
            "Zip Slip",
            "Intent Redirection",
            "Fragment Injection",
            "StrandHogg"
        )
        
        for (cat in categories) {
            val btn = Button(this)
            btn.text = cat
            btn.setTextColor(Color.BLACK)
            btn.setBackgroundColor(Color.parseColor("#00FF00")) // Cyberpunk Green
            val params = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, 
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            params.setMargins(0, 16, 0, 16)
            btn.layoutParams = params
            
            btn.setOnClickListener {
                when (cat) {
                    "Secrets & Auth" -> startActivity(Intent(this, SecretsActivity::class.java))
                    "WebView Exploits" -> startActivity(Intent(this, WebViewActivity::class.java))
                    "SQL Injection" -> startActivity(Intent(this, SQLInjectionActivity::class.java))
                    "IPC Attacks" -> startActivity(Intent(this, UnprotectedExportedActivity::class.java))
                    "Insecure Storage" -> startActivity(Intent(this, InsecureFileActivity::class.java))
                    "Logic Flaws" -> startActivity(Intent(this, CryptoActivity::class.java))
                    "GraphQL Injection" -> startActivity(Intent(this, GraphQLInjectionActivity::class.java))
                    "Insecure Deserialization" -> startActivity(Intent(this, DeserializationActivity::class.java))
                    "Unsafe Reflection" -> startActivity(Intent(this, ReflectionActivity::class.java))
                    "PendingIntent Hijack" -> startActivity(Intent(this, PendingIntentActivity::class.java))
                    "Zip Slip" -> startActivity(Intent(this, ZipSlipActivity::class.java))
                    "Intent Redirection" -> startActivity(Intent(this, IntentRedirectionActivity::class.java))
                    "Fragment Injection" -> startActivity(Intent(this, FragmentInjectionActivity::class.java))
                    "StrandHogg" -> startActivity(Intent(this, HijackTargetActivity::class.java))
                    else -> Toast.makeText(this, "Module $cat coming soon!", Toast.LENGTH_SHORT).show()
                }
            }
            
            container.addView(btn)
        }

        scrollView.addView(container)
        setContentView(scrollView)
    }
}