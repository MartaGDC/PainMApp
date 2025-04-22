package com.mgd.painmapp.controller.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.mgd.painmapp.controller.NavigationHelper
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
        patientName = intent.getStringExtra("patient_name").toString()
        researcherName = intent.getStringExtra("researcher_name").toString()
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
        NavigationHelper.setupMenu(
            navView,
            drawerLayout,
            this,
            patientName,
            researcherName,
            currentDate,
            idGeneradoEvaluation = -1
        )
    }

    private fun initListeners(){
        sensorial.setOnClickListener {
            NavigationHelper.navigateToSensorial(
                this,
                patientName,
                researcherName,
                currentDate,
                idGeneradoEvaluation = -1
            )
        }
        motor.setOnClickListener { NavigationHelper.navigateToMotor() }
        psychosocial.setOnClickListener { NavigationHelper.navigateToPsychosocial() }
    }
}