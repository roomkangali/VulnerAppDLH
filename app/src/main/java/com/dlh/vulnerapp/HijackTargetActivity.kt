package com.dlh.vulnerapp

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

/**
 * VULNERABILITY: StrandHogg (task hijacking).
 *
 * The actual weakness is declared in AndroidManifest.xml: this activity is
 * exported and uses a custom android:taskAffinity together with
 * android:launchMode="singleTask". A malicious app that declares the same
 * taskAffinity can insert itself into this task and overlay/phish the user.
 * The class body itself is intentionally trivial.
 */
class HijackTargetActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "StrandHogg Target\n(see manifest: taskAffinity + launchMode=singleTask)"
        tv.setPadding(32, 32, 32, 32)
        tv.setTextColor(Color.parseColor("#00FF00"))
        tv.setBackgroundColor(Color.parseColor("#121212"))
        setContentView(tv)
    }
}
