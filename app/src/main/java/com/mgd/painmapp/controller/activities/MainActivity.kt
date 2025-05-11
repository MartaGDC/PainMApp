package com.mgd.painmapp.controller.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.room.Room
import com.mgd.painmapp.databinding.ActivityMainBinding
import com.mgd.painmapp.model.database.PatientDatabase
import com.mgd.painmapp.model.storage.ColorBrush
import com.mgd.painmapp.model.storage.ColorBrush.saveColorIndex
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding : ActivityMainBinding
    private lateinit var patientName : EditText
    private lateinit var researcherName : EditText
    private lateinit var cvNext : CardView
    private lateinit var database: PatientDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        /*database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()
        this.deleteDatabase("patient_database")*/

        //Establecer en settings el color del pincel
        ColorBrush.initialize(this)
        CoroutineScope(Dispatchers.IO).launch {
            applicationContext.saveColorIndex(0) //al inicio de la app siempre empieza con el mismo color
        }
        database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()
        CoroutineScope(Dispatchers.IO).launch {
            //Lo he visto necesario al introducir un paciente, rellenar el mapa, pero salir antes de completar los sintomas.
            //No se podrá acceder a ese paciente de nuevo a menos que se eliminen los registros incompletos
            //Se considera que si se sale de la aplicación y habiéndose añádido al menos un sintoma completo, la evaluacion de ese paciente esta completa.
            database.getEvaluationDao().eliminarEvaluacionesSinMapa()
            database.getMapDao().eliminarMapasSinSintomas()
        }
        initUI()
    }
    @Suppress("MissingSuperCall")
    override fun onBackPressed(){
        finishAffinity()
    }

    private fun initUI() {
        patientName = binding.etPatientName
        researcherName = binding.etResearcherName
        cvNext = binding.cvNext
        cvNext.setOnClickListener {
            if(patientName.text.isEmpty()){
                patientName.error = "Debe completar este campo"
            }
            else{
                patientName.error = null
            }
            if (researcherName.text.isEmpty()){
                researcherName.error = "Debe completar este campo"
            }
            else{
                researcherName.error = null
            }
            if (patientName.text.isNotEmpty() && researcherName.text.isNotEmpty()) {
                val intent = Intent(this, ChooseActivity::class.java).apply {
                    putExtra("patient_name", patientName.text.toString())
                    putExtra("researcher_name", researcherName.text.toString())
                }
                startActivity(intent)
            }
        }
    }

}