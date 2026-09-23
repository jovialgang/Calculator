package com.example.calculator

import android.content.res.Configuration
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.calculator.ui.theme.CalculatorTheme
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CalculatorTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Calculator(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

val buttons = listOf(
    listOf("7", "8", "9", "÷"),
    listOf("4", "5", "6", "×"),
    listOf("1", "2", "3", "-"),
    listOf("AC", "0", "=", "+")
)

fun calculate(a: Double, b: Double, operation: String): Double = when (operation) {
    "+" -> a + b
    "-" -> a - b
    "×" -> a * b
    "÷" -> a / b
    else -> b
}

fun format(value: Double): String {
    if (value.isNaN() || value.isInfinite()) return "Ошибка"
    if (value == value.toLong().toDouble()) return value.toLong().toString()
    return String.format(Locale.US, "%.4f", value).trimEnd('0').trimEnd('.')
}

@Composable
fun Calculator(modifier: Modifier = Modifier) {
    var display by rememberSaveable { mutableStateOf("0") }
    var operand by rememberSaveable { mutableStateOf(0.0) }
    var operation by rememberSaveable { mutableStateOf("") }
    var newNumber by rememberSaveable { mutableStateOf(true) }

    fun press(label: String) {
        val current = display.toDoubleOrNull() ?: 0.0
        when (label) {
            "AC" -> {
                display = "0"
                operand = 0.0
                operation = ""
                newNumber = true
            }
            "+", "-", "×", "÷" -> {
                if (operation != "" && !newNumber) {
                    display = format(calculate(operand, current, operation))
                }
                operand = display.toDoubleOrNull() ?: 0.0
                operation = label
                newNumber = true
            }
            "=" -> {
                if (operation != "") {
                    display = format(calculate(operand, current, operation))
                    operation = ""
                    newNumber = true
                }
            }
            else -> {
                if (newNumber) {
                    display = label
                    newNumber = false
                } else if (display.length < 10) {
                    display += label
                }
            }
        }
    }

    val landscape = LocalConfiguration.current.orientation == Configuration.ORIENTATION_LANDSCAPE
    if (landscape) {
        Row(modifier.fillMaxSize().padding(8.dp)) {
            Display(display, Modifier.weight(1f).fillMaxHeight())
            Keypad({ label -> press(label) }, Modifier.weight(1f).fillMaxHeight())
        }
    } else {
        Column(modifier.fillMaxSize().padding(8.dp)) {
            Display(display, Modifier.weight(1f).fillMaxWidth())
            Keypad({ label -> press(label) }, Modifier.weight(3f).fillMaxWidth())
        }
    }
}

@Composable
fun Display(text: String, modifier: Modifier = Modifier) {
    Box(modifier.padding(16.dp), contentAlignment = Alignment.CenterEnd) {
        Text(text = text, fontSize = 40.sp, maxLines = 1)
    }
}

@Composable
fun Keypad(onPress: (String) -> Unit, modifier: Modifier = Modifier) {
    Column(modifier) {
        for (row in buttons) {
            Row(Modifier.weight(1f).fillMaxWidth()) {
                for (label in row) {
                    Button(
                        onClick = { onPress(label) },
                        modifier = Modifier.weight(1f).fillMaxHeight().padding(4.dp)
                    ) {
                        Text(text = label, fontSize = 20.sp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CalculatorPreview() {
    CalculatorTheme {
        Calculator()
    }
}
