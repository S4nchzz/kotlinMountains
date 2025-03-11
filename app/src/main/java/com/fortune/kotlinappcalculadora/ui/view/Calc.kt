package com.fortune.kotlinappcalculadora.ui.view

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.fortune.kotlinappcalculadora.R

class Calc : AppCompatActivity() {
    private var firstValueComplete: Boolean = false
    private var n1: Long? = null
    private var n2: Long? = null
    private var operatorValue: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calc)
        adjustScreenInsets()
        setupButtons()
    }

    private fun setupButtons() {
        val calculatedValue = findViewById<TextView>(R.id.calculated_value)

        val buttons = mapOf(
            R.id.one to 1, R.id.two to 2, R.id.three to 3, R.id.four to 4,
            R.id.five to 5, R.id.six to 6, R.id.seven to 7, R.id.eight to 8,
            R.id.nine to 9, R.id.zero to 0
        )

        buttons.forEach { (id, value) ->
            findViewById<Button>(id).setOnClickListener {
                if (!firstValueComplete) {
                    n1 = ((n1?.toString() ?: "") + value).toLong()
                    calculatedValue.text = n1.toString()
                } else {
                    n2 = ((n2?.toString() ?: "") + value).toLong()
                    calculatedValue.text = "$n1 $operatorValue $n2"
                }
            }
        }

        val operators = mapOf(
            R.id.plus to "+", R.id.minus to "-",
            R.id.multiplication to "*", R.id.division to "/"
        )

        operators.forEach { (id, value) ->
            findViewById<Button>(id).setOnClickListener {
                if (n1 != null) {
                    operatorValue = value
                    firstValueComplete = true
                    calculatedValue.text = "$n1 $operatorValue"
                }
            }
        }

        findViewById<Button>(R.id.equals).setOnClickListener {
            if (n1 != null && n2 != null) {
                val result = calculateResult(n1!!, n2!!, operatorValue)
                calculatedValue.text = result.toString()
                resetValues(result)
            }
        }

        findViewById<Button>(R.id.btn_borrar).setOnClickListener {
            resetValues(null)
            calculatedValue.text = ""
        }

        findViewById<Button>(R.id.btn_inv).setOnClickListener {
            if (calculatedValue.text.isNotEmpty()) {
                calculatedValue.text = calculatedValue.text.toString().reversed()
            }
        }
    }

    private fun calculateResult(a: Long, b: Long, operator: String): Long {
        return when (operator) {
            "+" -> a + b
            "-" -> a - b
            "*" -> a * b
            "/" -> if (b != 0L) a / b else 0L
            else -> 0L
        }
    }

    private fun resetValues(newValue: Long?) {
        n1 = newValue
        n2 = null
        operatorValue = ""
        firstValueComplete = false
    }

    private fun adjustScreenInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.act_calc)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }
}
