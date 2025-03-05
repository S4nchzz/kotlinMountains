package com.fortune.kotlinappcalculadora.ui

import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fortune.kotlinappcalculadora.R
import com.fortune.kotlinappcalculadora.db.AppDatabaseSqlite

class Login : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        adjustScreenInsets()

        manageLogin(initializeDBConection())
    }

    private fun initializeDBConection(): SQLiteDatabase {
        val conex = AppDatabaseSqlite(this, "exam-db", null, 1)
        return conex.readableDatabase
    }

    private fun manageLogin(db: SQLiteDatabase) {
        val username = findViewById<EditText>(R.id.user_field)
        val password = findViewById<EditText>(R.id.password_field)

        findViewById<Button>(R.id.btn_login).setOnClickListener {
            var rows = db.rawQuery(
                "SELECT * FROM user WHERE username = ? AND password = ?",
                arrayOf(username.text.toString(), password.text.toString())
            )

            while (rows.moveToNext()) {
                val usernameRetrivedFromDB = rows.getInt(rows.getColumnIndexOrThrow("id"))
                val passwordRetrivedFromDB = rows.getInt(rows.getColumnIndexOrThrow("id"))
            }
        }
    }

    private fun adjustScreenInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.act_login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}