package com.example.datacollector

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.sqrt

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener, SensorEventListener {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var currentSensorData: CurrentSensorData
    private lateinit var googleMapView: GoogleMapView
    private lateinit var settings: Settings
    private lateinit var collectedData: CollectedData

    //TextViews in ArrayListe
    private var tvAccelerometer:ArrayList<TextView> = ArrayList()

    //View ID:
    private var idAccelerometer:ArrayList<Int> = arrayListOf(R.id.tv_acc1, R.id.tv_acc2, R.id.tv_acc3, R.id.tv_accTime)

    //Sensor
    private lateinit var sensorManager : SensorManager
    private lateinit var sensorAccelerometer : Sensor

    private var accelerometerData : SensorData? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setActivities()
        initViews()
        initSensors()

    }

    private fun initViews(){
        for(i in idAccelerometer){
            tvAccelerometer.add(findViewById(i))
        }
        //Init Button: Button
        //Button:
        val bcollectData : Button = findViewById(R.id.b_collectData)
        bcollectData.setOnClickListener(){
            if(bcollectData.getText().toString().trim().equals("Collect data: ON")){
                bcollectData.setText("Collect data: OFF")
                onPause()

            } else if (bcollectData.getText().toString().trim().equals("Collect data: OFF")){
                bcollectData.setText("Collect data: ON")
                onResume()
            }
        }
    }

    private fun initSensors(){
        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        if(sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)!=null) {
            sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)
        }
    }

    private fun setActivities(){
        val drawerLayout:DrawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this, drawerLayout, R.string.open, R.string.close)
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        currentSensorData = CurrentSensorData()
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, currentSensorData).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
        googleMapView = GoogleMapView()
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, googleMapView).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
        settings = Settings()
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, settings).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
        collectedData = CollectedData()
        supportFragmentManager.beginTransaction().replace(R.id.frame_layout, collectedData).setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
        val navView:NavigationView = findViewById(R.id.navView)
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onOptionsItemSelected(item: MenuItem):Boolean{
        if(toggle.onOptionsItemSelected(item)){
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.item1 -> {
                googleMapView = GoogleMapView()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, googleMapView)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
            }
            R.id.item2 -> {
                currentSensorData = CurrentSensorData()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, currentSensorData)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
            }
            R.id.item3 -> {
                settings = Settings()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, settings)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
            }
            R.id.item4 -> {
                collectedData = CollectedData()
                supportFragmentManager.beginTransaction()
                        .replace(R.id.frame_layout, collectedData)
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit()
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if(event!!.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION){
            getAccelerometerData(event)
        }
    }

    private fun getAccelerometerData(e:SensorEvent?){
        //Kann man noch schoener machen!
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        if(accelerometerData == null) {
            accelerometerData = SensorData(e!!.values[0], e.values[1], e.values[2], e.timestamp)
        }
        else{
            accelerometerData!!.x1 = e!!.values[0]
            accelerometerData!!.x2 = e.values[1]
            accelerometerData!!.x3 = e.values[2]
            accelerometerData!!.timestamp = e.timestamp
        }

        tvAccelerometer[0].text = "X: ${"%.2f".format(accelerometerData!!.x1)}"
        tvAccelerometer[1].text = "Y: ${"%.2f".format(accelerometerData!!.x2)}"
        tvAccelerometer[2].text = "Z: ${"%.2f".format(accelerometerData!!.x3)}"
        //val magnitude : Float = sqrt(accelerometerData!!.x1*accelerometerData!!.x1+accelerometerData!!.x2*accelerometerData!!.x2+accelerometerData!!.x3*accelerometerData!!.x3)
        tvAccelerometer[3].text = "Time-stamp: ${timeStamp}"
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    override fun onPause() { //abmelden des Sensors
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onResume() { //anmelden des Sensors
        super.onResume()
        sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL)
    }

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            super.onBackPressed()
        }
    }
}