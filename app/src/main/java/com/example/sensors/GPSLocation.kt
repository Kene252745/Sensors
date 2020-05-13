package com.example.sensors

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*


class GPSLocation  : AppCompatActivity() {
    private var mFusedLocationProviderClient: FusedLocationProviderClient? = null
    private val locationInterval: Long = 2000
    private val locationFastestInterval: Long = 1000
    lateinit var mLastLocation: Location
    private lateinit var mLocationRequest: LocationRequest
    private val LocatePermit = 10
    private var outsideArea: Boolean = false

    lateinit var latTv: TextView
    lateinit var longTv: TextView
    lateinit var showLocatstat: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_g_p_s_location)

        mLocationRequest = LocationRequest()

        latTv = findViewById(R.id.showlatitude);
        longTv = findViewById(R.id.showLongitude);
        showLocatstat = findViewById(R.id.showLocatstat);
        showLocatstat.text = getString(R.string.inNe)
        showLocatstat.setTextColor(Color.GREEN)

        val locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            buildAlertMessageNoGps()
        }
    }

    override fun onResume(){
        super.onResume()
        if (checkPermissionForLocation(this)) {
            refreshLocation()
            Toast.makeText(this, getString(R.string.listening), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onPause(){
        super.onPause()
        stoplocationUpdates()
        showLocatstat.text = getString(R.string.stopped)
        Toast.makeText(this, getString(R.string.stoppedListening), Toast.LENGTH_SHORT).show()
    }

    private fun buildAlertMessageNoGps() {

        val builder = AlertDialog.Builder(this)
        builder.setMessage(getString(R.string.enableGpsQ))
            .setCancelable(false)
            .setPositiveButton(getString(R.string.y)) { _, _ ->
                startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                    , 11)
            }
            .setNegativeButton(getString(R.string.n)) { dialog, _ ->
                dialog.cancel()
                finish()
            }
        val alert: AlertDialog = builder.create()
        alert.show()
    }

    protected fun refreshLocation() {
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        mLocationRequest.interval = locationInterval
        mLocationRequest.fastestInterval = locationFastestInterval
        val builder = LocationSettingsRequest.Builder()
        builder.addLocationRequest(mLocationRequest)
        val locationSettingsRequest = builder.build()

        val settingsClient = LocationServices.getSettingsClient(this)
        settingsClient.checkLocationSettings(locationSettingsRequest)

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }
        mFusedLocationProviderClient!!.requestLocationUpdates(mLocationRequest, mLocationCallback,
            Looper.myLooper())
    }

    private val mLocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            locationResult.lastLocation
            onLocationChanged(locationResult.lastLocation)
        }
    }

    @SuppressLint("SetTextI18n")
    fun onLocationChanged(location: Location) {
        mLastLocation = location
        latTv.text = "Latitude : ${mLastLocation.latitude}"
        longTv.text = "Longitude : ${mLastLocation.longitude}"
        checkIfOutOfArea()
    }

    private fun checkIfOutOfArea() {
        if (mLastLocation.latitude < 51.1035677 || mLastLocation.latitude > 51.103881
            || mLastLocation.longitude < 17.0861545|| mLastLocation.longitude > 17.0859439){
            if(!outsideArea){
                Toast.makeText(this, getString(R.string.Outarea), Toast.LENGTH_SHORT).show()
                outsideArea = true
                showLocatstat.text = getString(R.string.Outside)
                showLocatstat.setTextColor(Color.BLACK)
            }
        }
        else {
            if(outsideArea) {
                Toast.makeText(this, getString(R.string.backInNe), Toast.LENGTH_SHORT).show()
                outsideArea = false
                showLocatstat.text = getString(R.string.inNe)
                showLocatstat.setTextColor(Color.BLUE)
            }
        }
    }

    private fun stoplocationUpdates() {
        mFusedLocationProviderClient!!.removeLocationUpdates(mLocationCallback)
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        if (requestCode == LocatePermit) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                refreshLocation()
            } else {
                Toast.makeText(this, getString(R.string.Accessdenied), Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun checkPermissionForLocation(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
                true
            } else {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),
                    LocatePermit)
                false
            }
        } else {
            true
        }
    }

}
