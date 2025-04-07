package com.mgd.painmapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.mgd.painmapp.Database.Entities.toDatabase
import com.mgd.painmapp.Database.Entities.toSymptom
import com.mgd.painmapp.Database.PatientDatabase
import com.mgd.painmapp.databinding.ActivitySensorialBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SensorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySensorialBinding
    private lateinit var patientName: String
    private lateinit var researcherName: String
    private lateinit var currentDate: String
    private lateinit var type: String
    private var idGenerado: Long = 0
    private lateinit var CVAdd: CardView
    private lateinit var adapter: SymptomsAdapter

    private lateinit var database: PatientDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        patientName = intent.getStringExtra("PATIENT_NAME").toString()
        researcherName = intent.getStringExtra("RESEARCHER_NAME").toString()
        currentDate = intent.getStringExtra("DATE").toString()
        type = intent.getStringExtra("TYPE").toString()

        /*database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()
        this.deleteDatabase("patient_database") */
        database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()


        initUI()
    }

    private fun fillDatabase() {
        val evaluationEntity =
            Evaluation(patientName, researcherName, currentDate, type).toDatabase()
        CoroutineScope(Dispatchers.IO).launch {
            idGenerado = database.getEvaluationDao().insertEvaluation(evaluationEntity)
        }
    }

    private fun initUI(){
        adapter = SymptomsAdapter(emptyList(), this)
        if(idGenerado != null ){
            CoroutineScope(Dispatchers.IO).launch {
                val symptomsList = database.getSymptomDao().getSymptomsByEvaluation(idGenerado)
                val symptoms = symptomsList.map { it.toSymptom() }
                runOnUiThread {
                    adapter.updateList(symptoms)
                }
            }
        }
        binding.RVsymptoms.setHasFixedSize(true)
        binding.RVsymptoms.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.RVsymptoms.adapter = adapter

        CVAdd = binding.CVAdd
        CVAdd.setOnClickListener {
            Log.d("Click", "selecciona añadir sintoma")
            fillDatabase()
            val intent = Intent(this, LocationActivity::class.java).apply {
                putExtra("ID", idGenerado)
            }
            startActivity(intent)
        }
    }


}