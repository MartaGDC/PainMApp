package com.mgd.painmapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.room.Room
import com.mgd.painmapp.Database.Entities.EvaluationEntity
import com.mgd.painmapp.Database.Entities.toDatabase
import com.mgd.painmapp.Database.PatientDatabase
import com.mgd.painmapp.databinding.ActivityChooseBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
    private lateinit var database: PatientDatabase
    private var idGenerado: Int = 0

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
            fillDatabase("sensorial")
            navigateToSensorial()
        }
        motor.setOnClickListener {
            fillDatabase("motor")
            navigateToMotor()
        }
        psicosocial.setOnClickListener {
            fillDatabase("psicosocial")
            navigateToPsicosocial()
        }
    }

    private fun fillDatabase(type: String) {
        val EvaluationEntity =
            Evaluation(patientName, researcherName, currentDate, type).toDatabase()
        database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()
        CoroutineScope(Dispatchers.IO).launch {
            idGenerado = database.getEvaluationDao().insertEvaluation(EvaluationEntity)
        }
    }

    private fun navigateToSensorial() {
        val intent = Intent(this, SensorialActivity::class.java).apply {
            putExtra("ID_TEST", idGenerado)
            startActivity(intent)
        }
    }
    private fun navigateToMotor() {
        TODO()
    }
    private fun navigateToPsicosocial() {
        TODO()
    }

}