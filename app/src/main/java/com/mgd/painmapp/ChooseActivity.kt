package com.mgd.painmapp

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.Gravity
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.mgd.painmapp.databinding.ActivityChooseBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ChooseActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var binding: ActivityChooseBinding
    private lateinit var patientName: String
    private lateinit var researcherName: String
    private lateinit var sensorial: CardView
    private lateinit var motor: CardView
    private lateinit var psicosocial: CardView
    private lateinit var currentDate: String

    private lateinit var drawer: DrawerLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        patientName = intent.getStringExtra("PATIENT_NAME").toString()
        researcherName = intent.getStringExtra("RESEARCHER_NAME").toString()
        currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())

        initComponents()
        initListener()
    }

    private fun initComponents(){
        sensorial = binding.sensorial
        motor = binding.motor
        psicosocial = binding.psicosocial
        //Menu
        drawer = binding.main
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeButtonEnabled(true)
        val navigationView : NavigationView = findViewById(R.id.nav_view)
        navigationView.setNavigationItemSelectedListener(this)
    }
    private fun initListener(){
        sensorial.setOnClickListener {
            navigateToSensorial()
        }
        motor.setOnClickListener {
            navigateToMotor()
        }
        psicosocial.setOnClickListener {
            navigateToPsicosocial()
        }
    }

    private fun navigateToSensorial() {
        val intent = Intent(this, SensorialActivity::class.java).apply {
            putExtra("PATIENT_NAME", patientName)
            putExtra("RESEARCHER_NAME", researcherName)
            putExtra("DATE", currentDate)
            putExtra("TYPE", "sensorial")
        }
        startActivity(intent)
    }
    private fun navigateToMotor() {
        TODO()
    }
    private fun navigateToPsicosocial() {
        TODO()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val intent_sensorial = Intent(this, SensorialActivity::class.java) //Habra que inlcuir  informaicón aqui, derivada de consultas a la base de datos
        when(item.itemId) {
            R.id.item_sensorial -> startActivity(intent_sensorial)
        }
        drawer.closeDrawer(GravityCompat.START)
        return true
    }
}