package com.example.rgb_led

import android.graphics.drawable.shapes.Shape
import android.hardware.ConsumerIrManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonDefaults.shape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.rgb_led.ui.theme.RGBLedControllerTheme

class MainActivity : ComponentActivity() {
    var consumerIrManager: ConsumerIrManager? = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        consumerIrManager = getSystemService(CONSUMER_IR_SERVICE) as ConsumerIrManager?;

        setContent {
            RGBLedControllerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting(this, "Android",)
                }
            }
        }
    }

    fun onClickBlue() {
        if (consumerIrManager == null || !consumerIrManager!!.hasIrEmitter()) {
            println("Doesn't have IR emitter.")
            return
        }

        val frequency : Int = 38020
        val signal : IntArray = intArrayOf(8970, 4498, 546, 572, 546, 572, 520, 598, 520, 572, 546, 572, 546, 572, 546, 572, 546, 572, 546, 1690, 546, 1664, 546, 1690, 546, 1690, 546, 572, 546, 1690, 546, 1690, 520, 1690, 546, 572, 546, 1690, 546, 1690, 520, 598, 520, 572, 546, 572, 546, 572, 546, 572, 546, 1690, 546, 572, 546, 572, 546, 1664, 546, 1690, 546, 1690, 546, 1690, 546, 1690, 520, 223210)

        consumerIrManager!!.transmit(frequency, signal)

        println("Clicked Blue.")
    }

    fun onClickRed() {
        if (consumerIrManager == null || !consumerIrManager!!.hasIrEmitter()) {
            println("Doesn't have IR emitter.")
            return
        }

        val frequency : Int = 38020
        val signal : IntArray = intArrayOf(8970, 4498, 520, 572, 546, 572, 520, 598, 546, 572, 546, 572, 546, 572, 546, 572, 546, 572, 520, 1690, 546, 1690, 546, 1690, 546, 1690, 520, 598, 520, 1690, 546, 1690, 520, 1716, 546, 572, 546, 572, 546, 1664, 546, 572, 546, 572, 546, 572, 546, 572, 520, 598, 546, 1690, 546, 1664, 546, 572, 546, 1690, 546, 1690, 546, 1690, 520, 1690, 520, 1716, 546, 420602)

        consumerIrManager!!.transmit(frequency, signal)

        println("Clicked Blue.")
    }
}

@Composable
fun FilledButton(text: String, onClick: () -> Unit, modifier: Modifier = Modifier, colors: ButtonColors) {
    Button(
        onClick = { onClick() },
        modifier = modifier,
        colors = colors
    ) { Text(text) }
}


@Composable
fun Greeting(mainActivity: MainActivity, name: String, modifier: Modifier = Modifier) {
    Surface (
        modifier = Modifier
            .fillMaxSize()
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
        ) {
            FilledButton(
                text = "Blue",
                onClick = mainActivity::onClickBlue,
                modifier = Modifier.width(80.dp).height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Blue, contentColor = Color.White)
            )
            FilledButton(
                text = "Red",
                onClick = mainActivity::onClickRed,
                modifier = Modifier.width(80.dp).height(40.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Red, contentColor = Color.White)
            )
        }
    }
}
