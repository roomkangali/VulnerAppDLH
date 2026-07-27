package com.dlh.vulnerapp

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

class DeserializationActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val output = outputView()

        // Attacker-controlled bytes arrive via IPC (this activity is exported).
        val data = intent.getByteArrayExtra("data") ?: demoPayload()

        try {
            // VULNERABILITY: Insecure Deserialization.
            // Untrusted bytes are deserialized with no allowlist / ObjectInputFilter,
            // enabling object-injection & gadget-chain attacks (potential RCE).
            val ois = ObjectInputStream(ByteArrayInputStream(data))
            val obj = ois.readObject()
            ois.close()
            output.text = "Deserialized object:\n$obj"
        } catch (e: Exception) {
            output.text = "Error: ${e.message}"
        }
    }

    private fun demoPayload(): ByteArray {
        val bos = ByteArrayOutputStream()
        ObjectOutputStream(bos).use { it.writeObject(DemoData("guest", false)) }
        return bos.toByteArray()
    }

    data class DemoData(val user: String, val admin: Boolean) : Serializable

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
