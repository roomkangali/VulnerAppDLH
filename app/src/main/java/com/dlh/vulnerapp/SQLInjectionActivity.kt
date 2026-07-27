package com.dlh.vulnerapp

import android.annotation.SuppressLint
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class SQLInjectionActivity : AppCompatActivity() {

    private lateinit var dbHelper: VulnerableDBHelper
    private lateinit var resultView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sqli)

        dbHelper = VulnerableDBHelper(this)
        val searchInput = findViewById<EditText>(R.id.searchInput)
        val searchButton = findViewById<Button>(R.id.searchButton)
        resultView = findViewById(R.id.resultView)

        // Pre-populate dummy data
        insertDummyData()

        searchButton.setOnClickListener {
            val query = searchInput.text.toString()
            if (query.isNotEmpty()) {
                performVulnerableSearch(query)
            }
        }
    }

    private fun insertDummyData() {
        val db = dbHelper.writableDatabase
        db.execSQL("INSERT OR IGNORE INTO users (name, secret) VALUES ('admin', 'Flag{SQL_Injection_Success}')")
        db.execSQL("INSERT OR IGNORE INTO users (name, secret) VALUES ('guest', 'Nothing here')")
    }

    @SuppressLint("Range", "Recycle")
    private fun performVulnerableSearch(username: String) {
        val db = dbHelper.readableDatabase
        try {
            // VULNERABILITY: SQL Injection
            // Concatenating user input directly into the SQL query string
            val sql = "SELECT * FROM users WHERE name = '$username'"
            
            // Log the query to show the flaw (simulated logging)
            resultView.text = "Executing Query:\n$sql\n\nResults:"

            val cursor = db.rawQuery(sql, null)

            if (cursor.moveToFirst()) {
                do {
                    val name = cursor.getString(cursor.getColumnIndex("name"))
                    val secret = cursor.getString(cursor.getColumnIndex("secret"))
                    resultView.append("\nUser: $name | Secret: $secret")
                } while (cursor.moveToNext())
            } else {
                resultView.append("\nNo users found.")
            }
            cursor.close()

        } catch (e: Exception) {
            resultView.append("\nError: ${e.message}")
        }
    }

    class VulnerableDBHelper(context: Context) : SQLiteOpenHelper(context, "VulnerableDB", null, 1) {
        override fun onCreate(db: SQLiteDatabase) {
            db.execSQL("CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT UNIQUE, secret TEXT)")
        }

        override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
            db.execSQL("DROP TABLE IF EXISTS users")
            onCreate(db)
        }
    }
}
