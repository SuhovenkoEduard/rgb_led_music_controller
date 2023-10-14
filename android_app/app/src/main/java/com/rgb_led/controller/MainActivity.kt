package com.rgb_led.controller

import android.hardware.ConsumerIrManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.rgb_led.controller.ui.theme.RGBLedControllerTheme

class MainActivity : ComponentActivity() {
    final var FREQUENCY: Int = 38020;
    var consumerIrManager: ConsumerIrManager? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        consumerIrManager = getSystemService(CONSUMER_IR_SERVICE) as ConsumerIrManager?;

        setContent {
            RGBLedControllerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting(this)
                }
            }
        }
    }

    fun onClick(ledPattern: LedPatterns) {
        if (consumerIrManager == null || !consumerIrManager!!.hasIrEmitter()) {
            println("Doesn't have IR emitter.")
            return
        }

        consumerIrManager!!.transmit(FREQUENCY, convertPatternToIntArrayOfMicroseconds(ledPattern.pattern));

        println("Clicked ${ledPattern.label}.")
    }
}

@Composable
fun LedButton(mainActivity: MainActivity, ledPattern: LedPatterns) {
    Button(
        onClick = { mainActivity.onClick(ledPattern) },
        modifier = Modifier
            .width(95.dp)
            .height(60.dp)
            .padding(2.dp, 8.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color(android.graphics.Color.parseColor(ledPattern.color)),
            contentColor = Color.White
        )
    ) {
        if (ledPattern.icon == null) {
            Text(ledPattern.label, fontSize = 12.sp)
        } else {
            Icon(ledPattern.icon, ledPattern.label)
        }
    }
}

@Composable
fun LedRow(mainActivity: MainActivity, ledPatters: List<LedPatterns>) {
    Row (
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ledPatters.map { ledPattern -> LedButton(mainActivity = mainActivity, ledPattern = ledPattern) }
    }
}

@Composable
fun Greeting(mainActivity: MainActivity) {
    Column (
        modifier = Modifier
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        LedPatterns.values().toList()
            .chunked(4)
            .map { ledPatterns -> LedRow(mainActivity = mainActivity, ledPatters = ledPatterns ) }
    }
}
