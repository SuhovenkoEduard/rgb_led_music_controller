package com.rgb_led.controller

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.ConsumerIrManager
import android.media.projection.MediaProjectionManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.rgb_led.controller.ui.theme.RGBLedControllerTheme

class MainActivity : ComponentActivity() {
    val FREQUENCY: Int = 38020
    var consumerIrManager: ConsumerIrManager? = null
    lateinit var mediaProjectionManager: MediaProjectionManager
    var isCapturingAudio: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        consumerIrManager = getSystemService(CONSUMER_IR_SERVICE) as ConsumerIrManager?

        setContent {
            RGBLedControllerTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    Greeting(mainActivity = this)
                }
            }
        }
    }

    fun onClick(ledPattern: LedPatterns) {
        if (consumerIrManager == null || !consumerIrManager!!.hasIrEmitter()) {
            println("Doesn't have IR emitter.")
            return
        }

        consumerIrManager!!.transmit(FREQUENCY, convertPatternToIntArrayOfMicroseconds(ledPattern.pattern))

        println("Clicked ${ledPattern.label}.")
    }

    fun startCapturing() {
        if (!isRecordAudioPermissionGranted()) {
            requestRecordAudioPermission()
        } else {
            startMediaProjectionRequest()
        }
    }

    fun stopCapturing() {
        isCapturingAudio = false

        startService(Intent(this, AudioCaptureService::class.java).apply {
            action = AudioCaptureService.ACTION_STOP
        })
    }

    private fun isRecordAudioPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestRecordAudioPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO), RECORD_AUDIO_PERMISSION_REQUEST_CODE)
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == RECORD_AUDIO_PERMISSION_REQUEST_CODE) {
            if (grantResults.firstOrNull() == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permissions to capture audio granted. Click the button once again.", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Permissions to capture audio denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    /**
     * Before a capture session can be started, the capturing app must
     * call MediaProjectionManager.createScreenCaptureIntent().
     * This will display a dialog to the user, who must tap "Start now" in order for a
     * capturing session to be started. This will allow both video and audio to be captured.
     */
    private fun startMediaProjectionRequest() {
        // use applicationContext to avoid memory leak on Android 10.
        // see: https://partnerissuetracker.corp.google.com/issues/139732252
        mediaProjectionManager =
            applicationContext.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
        startActivityForResult(
            mediaProjectionManager.createScreenCaptureIntent(),
            MEDIA_PROJECTION_REQUEST_CODE
        )
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == MEDIA_PROJECTION_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                Toast.makeText(
                    this, "MediaProjection permission obtained. Foreground service will be started to capture audio.", Toast.LENGTH_SHORT).show()

                val audioCaptureIntent = Intent(this, AudioCaptureService::class.java).apply {
                    action = AudioCaptureService.ACTION_START
                    putExtra(AudioCaptureService.EXTRA_RESULT_DATA, data!!)
                }
                startForegroundService(audioCaptureIntent)

                isCapturingAudio = true
            } else {
                Toast.makeText(this, "Request to obtain MediaProjection denied.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        private const val RECORD_AUDIO_PERMISSION_REQUEST_CODE = 42
        private const val MEDIA_PROJECTION_REQUEST_CODE = 13
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
        Row {
            Button (onClick = mainActivity::startCapturing, modifier = Modifier.padding(10.dp)) {
                Text(text = "Start")
            }
            Button (onClick = mainActivity::stopCapturing, modifier = Modifier.padding(10.dp)) {
                Text(text = "Stop")
            }
        }
    }
}
