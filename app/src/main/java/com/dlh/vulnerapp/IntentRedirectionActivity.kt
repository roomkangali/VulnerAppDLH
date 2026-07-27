package com.dlh.vulnerapp

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * VULNERABILITY: Intent Redirection (confused deputy).
 *
 * This exported activity extracts a nested Intent from an untrusted caller and
 * forwards it with startActivity() WITHOUT validating its target. A malicious app
 * can point the nested Intent at one of this app's NON-exported components (or
 * attach granted URI permissions), using this app as a proxy to reach things it
 * could not reach directly.
 */
class IntentRedirectionActivity : AppCompatActivity() {

    @Suppress("DEPRECATION")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val output = outputView()

        // Nested, attacker-controlled Intent pulled straight from the incoming extras.
        val forward = intent.getParcelableExtra<Intent>("forward_intent")

        if (forward != null) {
            // VULNERABILITY: forwarding an unvalidated Intent from an untrusted source.
            startActivity(forward)
            output.text = "Redirected to: ${forward.component ?: forward.action}"
        } else {
            output.text = "Send a nested Intent in the 'forward_intent' extra to redirect."
        }
    }

    private fun outputView(): TextView {
        val tv = TextView(this)
        tv.setPadding(32, 32, 32, 32)
        tv.setTextColor(Color.parseColor("#00FF00"))
        tv.typeface = Typeface.MONOSPACE
        val scroll = ScrollView(this).apply {
            setBackgroundColor(Color.parseColor("#121212"))
            addView(tv)
        }
        setContentView(scroll)
        return tv
    }
}
