package com.dlh.vulnerapp

import android.app.PendingIntent
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class PendingIntentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val output = outputView()

        // An empty / implicit base Intent (no component or package set).
        val baseIntent = Intent()

        // VULNERABILITY: PendingIntent Hijacking.
        // A MUTABLE PendingIntent wrapping an implicit Intent lets whichever app
        // receives it (e.g. via a notification) fill in the unspecified fields and
        // redirect it to a component of the attacker's choice, running with THIS
        // app's identity and permissions.
        val pending = PendingIntent.getActivity(
            this,
            0,
            baseIntent,
            PendingIntent.FLAG_MUTABLE
        )

        output.text = "Created MUTABLE PendingIntent:\n$pending\n" +
            "(base intent is implicit -> hijackable)"
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
