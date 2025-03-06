package com.fortune.kotlinappcalculadora.ui

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fortune.kotlinappcalculadora.R
import com.fortune.kotlinappcalculadora.db.AppDatabaseSqlite

class Mountain : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mountain)
        adjustScreenInsets()

        manageUserData()
        findMountaisByUsername()
    }

    private fun findMountaisByUsername() {
        val conex = AppDatabaseSqlite(this, "exam-db", null, 1)
        val db = conex.readableDatabase

        // get mountains by username and show into listview
    }

    private fun manageUserData() {
        val add_mountain = findViewById<Button>(R.id.add_mountain);
        val user_type: Char = intent.getStringExtra("type").toString()[0];

        if (user_type == 'A') {
            add_mountain.visibility = View.VISIBLE
        }
    }

    private fun adjustScreenInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}