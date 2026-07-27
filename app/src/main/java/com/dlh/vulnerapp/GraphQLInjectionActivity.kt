package com.dlh.vulnerapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class GraphQLInjectionActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_graphql)

        val inputUser = findViewById<EditText>(R.id.inputGraphQLParams)
        val btnSearch = findViewById<Button>(R.id.btnExecuteQuery)
        val txtResult = findViewById<TextView>(R.id.txtGraphQLResult)

        btnSearch.setOnClickListener {
            val userInput = inputUser.text.toString()
            executeVulnerableQuery(userInput, txtResult)
        }
    }

    private fun executeVulnerableQuery(userInput: String, resultView: TextView) {
        // SCANNER TRAP: Explicit string that survives compilation
        val scannerTrap = "VULNERABILITY_TARGET: graphql_injection with string concatenation"
        
        // VULNERABILITY: GraphQL Injection
        // Rule: graphql_injection
        // Concatenating user input directly into the query string allows
        // attackers to modify the query structure (e.g. adding fields, bypassing checks).
        // Attack payload example: "admin\") { id password } user(name: \"bob"
        
        // Explicit "query" keyword for grep search
        val queryPrefix = "{\"query\": \"query { user(name: \\\""
        val querySuffix = "\\\") { id name email } }\"}"
        
        // VULNERABLE: Direct concatenation
        val query = queryPrefix + userInput + querySuffix

        // Mocking the execution
        resultView.text = "Executing GraphQL Query:\n$query\n\n(Note: In a real attack, the backend would process this injected query)"
    }
}
