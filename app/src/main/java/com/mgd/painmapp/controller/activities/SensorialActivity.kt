package com.mgd.painmapp.controller.activities

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.navigation.NavigationView
import com.mgd.painmapp.R
import com.mgd.painmapp.controller.InterpretationHelper
import com.mgd.painmapp.model.database.entities.toDatabase
import com.mgd.painmapp.model.database.entities.toSymptom
import com.mgd.painmapp.model.database.PatientDatabase
import com.mgd.painmapp.model.database.Evaluation
import com.mgd.painmapp.controller.NavigationHelper
import com.mgd.painmapp.view.adapters.SymptomsAdapter
import com.mgd.painmapp.databinding.ActivitySensorialBinding
import com.mgd.painmapp.view.MapViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SensorialActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySensorialBinding
    private lateinit var cvAdd: CardView
    private lateinit var adapter: SymptomsAdapter
    private lateinit var mvFront: MapViews
    private lateinit var mvBack: MapViews
    private lateinit var patientName: String
    private lateinit var researcherName: String
    private lateinit var currentDate: String
    private lateinit var type: String
    private var idGeneratedEvaluation: Long = -1
    private lateinit var database: PatientDatabase
    private var mvFrontReady: Boolean = false
    private var mvBackReady: Boolean = false

    //Menu
    private lateinit var cvMenu: CardView
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        patientName = intent.getStringExtra("patient_name").toString()
        researcherName = intent.getStringExtra("researcher_name").toString()
        currentDate = intent.getStringExtra("date").toString()
        type = intent.getStringExtra("type").toString()
        idGeneratedEvaluation = intent.getLongExtra("idGeneratedEvaluation", -1)

        database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()


        initComponents()
        initListeners()
    }

    private fun initComponents(){
        adapter = SymptomsAdapter(emptyList(), this)
        if(idGeneratedEvaluation != (-1).toLong()){
            CoroutineScope(Dispatchers.IO).launch {
                val symptomsList = database.getSymptomDao().getSymptomsByEvaluation(idGeneratedEvaluation)
                val symptoms = symptomsList.map { it.toSymptom() }
                runOnUiThread {
                    adapter.updateList(symptoms)
                }
            }
        }
        else{
            adapter.updateList(emptyList())
        }
        binding.rvSymptoms.setHasFixedSize(true)
        binding.rvSymptoms.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rvSymptoms.adapter = adapter
        cvAdd = binding.cvAdd
        mvFront = binding.mvFront
        mvBack = binding.mvBack
        CoroutineScope(Dispatchers.IO).launch{
            val bPathFront = getFrontDrawings()
            val bPathBack = getBackDrawings()
            mvFront.paths = bPathFront
            mvBack.paths = bPathBack
        }
        mvFront.post {
            mvFrontReady = true
            calcularTotales()
        }
        mvBack.post {
            mvBackReady = true
            calcularTotales()
        }


        //Menu:
        cvMenu = binding.cvMenu
        drawerLayout = binding.main
        navView = binding.navView
        NavigationHelper.setupMenu(
            navView,
            drawerLayout,
            this,
            patientName,
            researcherName,
            currentDate,
            idGeneratedEvaluation
        )
    }

    private fun initListeners(){
        cvMenu.setOnClickListener {
            drawerLayout.open()
        }
        cvAdd.setOnClickListener { //Se guarda la información de la evaluation (sin contenido en la evaluacion, en este caso sintomas, no se guarda nada en la tabla de evaluaciones)
            CoroutineScope(Dispatchers.IO).launch { //Creamos aqui la coroutine, llamando a una funcion suspend
                fillDatabase()
                val intent = Intent(this@SensorialActivity, LocationActivity::class.java).apply {
                    putExtra("idGeneratedEvaluation", idGeneratedEvaluation)
                }
                startActivity(intent)
            }

        }
    }

    private suspend fun getFrontDrawings(): List<String> {
        val bPath: List<String>
        if (idGeneratedEvaluation != (-1).toLong()) { //Hay registro de evaluación
            bPath = database.getMapDao().getFrontPathsDrawnById(idGeneratedEvaluation)
            return bPath
        }
        return emptyList()
    }
    private suspend fun getBackDrawings(): List<String> {
        val bPath: List<String>
        if (idGeneratedEvaluation != (-1).toLong()) { //Hay registro de evaluación
            bPath = database.getMapDao().getBackPathsDrawnById(idGeneratedEvaluation)
            return bPath
        }
        return emptyList()
    }

    private suspend fun fillDatabase() { //Suspend para que el hilo principal espere
        if (idGeneratedEvaluation == (-1).toLong()) { //Si no hay registro de evaluación
            val evaluationEntity =
                Evaluation(patientName, researcherName, currentDate, type).toDatabase()
            idGeneratedEvaluation = database.getEvaluationDao().insertEvaluation(evaluationEntity) //Se elimina la coroutine, porque se lanza desde el listener
        }
        else { //Si ya se ha registrado
            return
        }
    }

    private fun calcularTotales() {
        if (mvFrontReady && mvBackReady && idGeneratedEvaluation != (-1).toLong()) { //Hay registro de evaluación, y mapas disponibles
            val resultFront = mvFront.calculatePixels("frente")
            val resultBack = mvBack.calculatePixels("espalda")
            val results = InterpretationHelper.calculatePercentage(resultFront, resultBack)
            val totalPercentage = results["total"] ?: 0f
            val totalRightPercentage = results["derecha"] ?: 0f
            val totalLeftPercentage = results["izquierda"] ?: 0f
            CoroutineScope(Dispatchers.IO).launch {
                database.getMapDao().updatePatientPercentages(idGeneratedEvaluation, totalPercentage, totalRightPercentage, totalLeftPercentage)
            }
        }
    }

    @Suppress("MissingSuperCall")
    override fun onBackPressed(){
        if(idGeneratedEvaluation != (-1).toLong()) {
            Toast.makeText(this, getString(R.string.leaving_registry), Toast.LENGTH_SHORT).show()
            NavigationHelper.navigateToNewPatient(this)
        }
        else{
            NavigationHelper.navigateToChoose(this, patientName, researcherName)
        }
    }
}