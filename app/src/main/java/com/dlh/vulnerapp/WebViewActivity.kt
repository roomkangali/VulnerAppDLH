package com.dlh.vulnerapp

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.JavascriptInterface
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class WebViewActivity : AppCompatActivity() {

    @SuppressLint("SetJavaScriptEnabled", "AddJavascriptInterface")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        val webView = findViewById<WebView>(R.id.webView)
        val urlInput = findViewById<EditText>(R.id.urlInput)
        val loadButton = findViewById<Button>(R.id.loadButton)

        // VULNERABILITY 1: JavaScript Enabled (XSS Risk)
        webView.settings.javaScriptEnabled = true

        // VULNERABILITY 2: Universal Access from File URLs (Local File Theft)
        webView.settings.allowUniversalAccessFromFileURLs = true
        webView.settings.allowFileAccess = true

        // VULNERABILITY 3: JavaScript Interface (Bridge Risk)
        webView.addJavascriptInterface(WebAppInterface(this), "AndroidBridge")

        // Standard WebView setup
        webView.webChromeClient = WebChromeClient()
        webView.webViewClient = WebViewClient()

        // Load default risky page or user input
        loadButton.setOnClickListener {
            val url = urlInput.text.toString()
            if (url.isNotEmpty()) {
                // VULNERABILITY 4: Loading arbitrary URLs (Deep Link / Phishing risk if exposed via Intent)
                webView.loadUrl(url)
            }
        }

        // Handle Deep Link if activity started via Intent
        val data = intent.data
        if (data != null) {
            val deepLinkUrl = data.getQueryParameter("url")
            if (deepLinkUrl != null) {
                webView.loadUrl(deepLinkUrl)
                urlInput.setText(deepLinkUrl)
            }
        } else {
            // Default load
            webView.loadData("<html><body><h1>Vulnerable WebView</h1><p>JS is enabled. Try alerts.</p></body></html>", "text/html", "UTF-8")
        }
    }

    // Vulnerable Interface Class
    class WebAppInterface(private val activity: AppCompatActivity) {
        @JavascriptInterface
        fun showToast(message: String) {
            Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
        }

        @JavascriptInterface
        fun getSecrets(): String {
            // VULNERABILITY: Exposing sensitive data to JS
            return "SUPER_SECRET_TOKEN_FROM_BRIDGE"
        }
    }
}
