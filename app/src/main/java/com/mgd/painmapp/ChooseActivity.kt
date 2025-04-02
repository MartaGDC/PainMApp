package com.mgd.painmapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
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
    private lateinit var psicosocial: CardView
    private lateinit var currentDate: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseBinding.inflate(layoutInflater)
        setContentView(binding.root)
        sensorial = binding.sensorial
        motor = binding.motor
        psicosocial = binding.psicosocial
        patientName = intent.getStringExtra("PATIENT_NAME").toString()
        researcherName = intent.getStringExtra("RESEARCHER_NAME").toString()
        currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
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

}