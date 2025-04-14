package com.mgd.painmapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.mgd.painmapp.databinding.ActivityChooseBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChooseActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChooseBinding
    private lateinit var patientName: String
    private lateinit var researcherName: String
    private lateinit var sensorial: CardView
    private lateinit var motor: CardView
    private lateinit var psychosocial: CardView
    private lateinit var currentDate: String

    //Menu:
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        patientName = intent.getStringExtra("PATIENT_NAME").toString()
        researcherName = intent.getStringExtra("RESEARCHER_NAME").toString()
        currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        initComponents()
        initListeners()
    }

    private fun initComponents(){
        sensorial = binding.cvSensorial
        motor = binding.cvMotor
        psychosocial = binding.cvPsychosocial

        //Menu:
        drawerLayout = binding.main
        navView = binding.navView
        setupMenu()
    }

    private fun initListeners(){
        sensorial.setOnClickListener {
            navigateToSensorial(patientName, researcherName, currentDate)
        }
        motor.setOnClickListener {
            navigateToMotor()
        }
        psychosocial.setOnClickListener {
            navigateToPsychosocial()
        }
    }

    fun navigateToSensorial(patientName: String, researcherName: String, currentDate: String) {
        val intent = Intent(this, SensorialActivity::class.java).apply {
            putExtra("PATIENT_NAME", patientName)
            putExtra("RESEARCHER_NAME", researcherName)
            putExtra("DATE", currentDate)
            putExtra("TYPE", "sensorial")
        }
        startActivity(intent)
    }
    fun navigateToMotor() {
        TODO()
    }
    fun navigateToPsychosocial() {
        TODO()
    }

    //Menu
    private fun setupMenu() {
        navView.setNavigationItemSelectedListener { menuItem ->
            handleMenuItemClick(menuItem)
            true
        }
    }

    private fun handleMenuItemClick(item: MenuItem) {
        Log.d("Menu", "Item clicked: ${item.title}")
        when (item.itemId) {
            R.id.item_sensorial -> {
                navigateToSensorial(patientName, researcherName, currentDate)
            }
            R.id.item_motor -> {
                navigateToMotor()
            }
            else -> {
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
    }

}