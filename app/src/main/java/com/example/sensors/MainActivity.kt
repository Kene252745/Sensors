package com.example.sensors

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorManager
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var sensorManager: SensorManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        val deviceSensors: List<Sensor> = sensorManager.getSensorList(Sensor.TYPE_ALL)
        val listTv = findViewById<ListView>(R.id.listsensor)
        val ballbtn = findViewById<Button>(R.id.ballbtn)
        val locationbtn = findViewById<Button>(R.id.locationbtn)


        val listItems = arrayOfNulls<String>(deviceSensors.size)
        for (i in deviceSensors.indices) {
            val sensor = deviceSensors[i]
            listItems[i] = sensor.name
        }
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, listItems)
        listTv.adapter = adapter

        listTv.onItemClickListener =
            AdapterView.OnItemClickListener { _, _, position, _ ->
                val itemValue = listTv.getItemAtPosition(position) as String

                Toast.makeText(applicationContext,
                    deviceSensors[position].toString(), Toast.LENGTH_LONG)
                    .show()
            }

        ballbtn.setOnClickListener {
            val intent = Intent(this, Ball::class.java)
            startActivity(intent)
        }

        locationbtn.setOnClickListener {
            val intent = Intent(this, GPSLocation::class.java)
            startActivity(intent)
        }
    }
}
