package com.fortune.kotlinappcalculadora.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fortune.kotlinappcalculadora.R

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        adjustScreenInsets()

        findViewById<ImageButton>(R.id.btn_calculator).setOnClickListener {
            val openCalc = Intent(this@MainActivity, Calc::class.java)
            startActivity(openCalc)
        }

        findViewById<Button>(R.id.btn_montanas).setOnClickListener {
            val openLogin = Intent(this@MainActivity, Login::class.java)
            startActivity(openLogin)
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