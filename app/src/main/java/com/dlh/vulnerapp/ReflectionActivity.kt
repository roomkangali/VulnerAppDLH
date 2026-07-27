package com.dlh.vulnerapp

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ReflectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val output = outputView()

        // Attacker-controlled class name arrives via IPC (this activity is exported).
        val className = intent.getStringExtra("class_name") ?: "java.util.Date"

        try {
            // VULNERABILITY: Unsafe Reflection.
            // Instantiating an arbitrary class named by untrusted input lets an
            // attacker reach dangerous constructors / gadget classes (potential RCE).
            val clazz = Class.forName(className)
            val instance = clazz.getDeclaredConstructor().newInstance()
            output.text = "Loaded class: $className\nInstance: $instance"
        } catch (e: Exception) {
            output.text = "Error: ${e.message}"
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
