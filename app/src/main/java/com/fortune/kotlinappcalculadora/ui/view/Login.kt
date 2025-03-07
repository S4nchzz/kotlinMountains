package com.fortune.kotlinappcalculadora.ui.view

import android.content.Intent
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
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
            if (username.text.isEmpty() || password.text.isEmpty()) {
                Toast.makeText(this@Login, "Los campos no pueden estar vacios.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val rows = db.rawQuery(
                "SELECT * FROM user WHERE username = ? AND password = ?",
                arrayOf(username.text.toString(), password.text.toString())
            )

            if (rows.moveToNext()) {
                val openMountains = Intent(this@Login, Mountain::class.java)

                val type = when(username.text.toString()) {
                    "admin" -> {
                        "A"
                    }

                    "invitado" -> {
                        "G"
                    }

                    "iyan" -> {
                        "I"
                    }

                    else -> {""}
                }

                rows.close()
                db.close()

                openMountains.putExtra("username", username.text.toString())
                openMountains.putExtra("type", type)
                startActivity(openMountains)
                finish()
                return@setOnClickListener
            }

            AlertDialog.Builder(this)
                .setTitle("ERROR")
                .setMessage("El usuario ${username.text} con la contraseña ${password.text} no tienen las credenciales para iniciar sesion")
                .show()

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