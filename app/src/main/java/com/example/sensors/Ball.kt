package com.example.sensors

import android.content.Context
import android.content.pm.ActivityInfo
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Point
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NavUtils


class Ball : AppCompatActivity(), SensorEventListener {

    private var xPos: Float = 0.0f
    private var xAccel: Float = 0.0f
    private var xVel: Float = 0.0f
    private var yPos: Float = 0.0f
    private var yAccel: Float = 0.0f
    private var yVel: Float = 0.0f
    private var xMax: Float = 0.0f
    private var yMax: Float = 0.0f

    internal lateinit var ball: Bitmap
    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation =  (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        val ballView = Ball(this)
        setContentView(ballView)

        val size = Point()
        val screenDisplay = windowManager.defaultDisplay
        screenDisplay.getSize(size)
        xMax = size.x.toFloat() - 100
        yMax = size.y.toFloat() - 220
        xPos = size.x.toFloat()/2
        yPos = size.y.toFloat()/2

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
    }

    override fun onStart() {
        super.onStart()
        sensorManager.registerListener(
            this,
            sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
            SensorManager.SENSOR_DELAY_GAME
        )
    }

    override fun onStop() {
        sensorManager.unregisterListener(this)
        super.onStop()
    }

    override fun onSensorChanged(sensorEvent: SensorEvent) {
        if (sensorEvent.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            xAccel = sensorEvent.values[0]
            yAccel = -sensorEvent.values[1]
            updateBallPosition()
        }
    }

    private fun updateBallPosition() {
        val refreshRate = 0.6f
        xVel += xAccel * refreshRate
        yVel += yAccel * refreshRate
        xPos -= xVel / 2 * refreshRate
        yPos -= yVel / 2 * refreshRate
        if (xPos > xMax || yPos > yMax || xPos < 0 || yPos < 0)  gameOver()
    }

    private fun gameOver() {
        sensorManager.unregisterListener(this)
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Ball slipped!")
        builder.setMessage("Do You Want to Play Again?")
        builder.setPositiveButton(android.R.string.yes) { _, _ ->
            this.recreate()
        }

        builder.setNegativeButton(android.R.string.no) { _, _ ->
            NavUtils.navigateUpFromSameTask(this)
        }
        builder.show()
    }

    override fun onAccuracyChanged(sensor: Sensor?, i: Int) {}

    inner class Ball(context: Context?) : View(context) {
        override fun onDraw(canvas: Canvas) {
            canvas.drawBitmap(ball, xPos, yPos, null)
            invalidate()
        }

        init {
            val ballSrc = BitmapFactory.decodeResource(resources, R.drawable.ballimage)
            val dstWidth = 100
            val dstHeight = 100
            ball = Bitmap.createScaledBitmap(ballSrc, dstWidth, dstHeight, true)
        }
    }
}


