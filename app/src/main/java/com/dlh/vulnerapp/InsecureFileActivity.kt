package com.dlh.vulnerapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream

class InsecureFileActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_file)

        val fileNameInput = findViewById<EditText>(R.id.fileNameInput)
        val readButton = findViewById<Button>(R.id.readButton)
        val writeButton = findViewById<Button>(R.id.writeButton)
        val contentOutput = findViewById<TextView>(R.id.fileContent)

        // 1. Insecure File Permission (MODE_WORLD_READABLE)
        writeButton.setOnClickListener {
            createWorldReadableFile()
        }

        // 2. Path Traversal
        readButton.setOnClickListener {
            val filename = fileNameInput.text.toString()
            if (filename.isNotEmpty()) {
                val content = readFileVulnerable(filename)
                contentOutput.text = content
            }
        }
    }

    @SuppressLint("WorldReadableFiles", "SetWorldReadable")
    private fun createWorldReadableFile() {
        try {
            // VULNERABILITY: MODE_WORLD_READABLE (Deprecated but dangerous)
            // Even if Android throws SecurityException on modern APIs, the intent is detectable.
            // On older APIs or rooted devices, this exposes the file to all apps.
            // We use the integer value 1 for MODE_WORLD_READABLE to avoid compilation errors if symbols are removed
            val modeWorldReadable = 1 
            val fOut: FileOutputStream = openFileOutput("public_secret.txt", modeWorldReadable)
            fOut.write("This is a world-readable secret!".toByteArray())
            fOut.close()
            Toast.makeText(this, "Created world-readable file!", Toast.LENGTH_SHORT).show()
            
            // Explicitly set readability purely for demonstration if openFileOutput blocks it
            val file = File(filesDir, "public_secret.txt")
            file.setReadable(true, false) // readable by everyone
            
        } catch (e: Exception) {
            Toast.makeText(this, "Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun readFileVulnerable(filename: String): String {
        return try {
            // VULNERABILITY: Path Traversal
            // Directly using user input to construct a file path without sanitizing "../"
            // Attacker can pass "../../../etc/hosts" or similar.
            val file = File(filesDir, filename)
            
            // Reading the file
            val stream = FileInputStream(file)
            val inputString = stream.bufferedReader().use { it.readText() }
            "File Content:\n$inputString"
        } catch (e: Exception) {
            "Error reading file: ${e.message}\n(Tried path: ${File(filesDir, filename).absolutePath})"
        }
    }
}
