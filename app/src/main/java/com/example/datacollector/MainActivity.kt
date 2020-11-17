package com.example.datacollector

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.FragmentTransaction
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(),NavigationView.OnNavigationItemSelectedListener {
    private lateinit var toggle: ActionBarDrawerToggle
    private lateinit var currentSensorData: CurrentSensorData
    private lateinit var googleMapView: GoogleMapView
    private lateinit var settings: Settings
    private lateinit var collectedData: CollectedData
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val drawerLayout:DrawerLayout = findViewById(R.id.drawerLayout)
        toggle = ActionBarDrawerToggle(this,drawerLayout,R.string.open,R.string.close)
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

    override fun onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START)
        }
        else{
            super.onBackPressed()
        }
    }
}