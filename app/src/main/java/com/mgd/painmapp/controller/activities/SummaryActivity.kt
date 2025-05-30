package com.mgd.painmapp.controller.activities

import android.os.Bundle
import android.view.View
import android.widget.TableLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.google.android.material.navigation.NavigationView
import com.mgd.painmapp.R
import com.mgd.painmapp.controller.NavigationHelper
import com.mgd.painmapp.controller.TablesHelper
import com.mgd.painmapp.databinding.ActivitySummaryBinding
import com.mgd.painmapp.model.database.PatientDatabase
import com.mgd.painmapp.view.MapViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SummaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySummaryBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var cvCSV: CardView
    private lateinit var cvMenu: CardView
    private lateinit var tlPercentages: TableLayout
    private lateinit var tlNerves: TableLayout
    private lateinit var tlDermatomes: TableLayout
    private var dialogView: View? = null
    private lateinit var mvFront: MapViews
    private lateinit var mvBack: MapViews
    private var idGeneratedEvaluation: Long = -1
    private var alreadyExists: Boolean = false
    private lateinit var database: PatientDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        idGeneratedEvaluation = intent.getLongExtra("idGeneratedEvaluation", -1)
        alreadyExists = intent.getBooleanExtra("alreadyExists", false)
        database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()

        initComponents()
        initListeners()

        CoroutineScope(Dispatchers.IO).launch {
            val listEntities = database.getEvaluationDao().getEvaluations()
            val symptomsTable = database.getSymptomDao().getSymptomsTableByEvaluation(idGeneratedEvaluation)
            val symptomFilas = TablesHelper.getSymptomsTable(symptomsTable)
            val nervesTable = database.getMapDao().getNervesTableByEvaluation(idGeneratedEvaluation)
            val nerveFilas = TablesHelper.prepareTableNerves(nervesTable, this@SummaryActivity)
            val dermatomeFilas = TablesHelper.prepareTableDermatomes(nervesTable, this@SummaryActivity)
            val evaluationEntity = database.getEvaluationDao().getEvaluationById(idGeneratedEvaluation)
            runOnUiThread{
                NavigationHelper.setupMenu(
                    navView,
                    drawerLayout,
                    this@SummaryActivity,
                    evaluationEntity.patientName,
                    evaluationEntity.researcherName,
                    evaluationEntity.date,
                    idGeneratedEvaluation,
                    listEntities, dialogView, database, "summary",alreadyExists
                )
                TablesHelper.createSymptomsTable(symptomFilas, tlPercentages, this@SummaryActivity)
                TablesHelper.createTables(nerveFilas, tlNerves, this@SummaryActivity)
                TablesHelper.createTables(dermatomeFilas, tlDermatomes, this@SummaryActivity)
            }
        }
    }

    private fun initComponents(){
        cvMenu = binding.cvMenu
        drawerLayout = binding.main
        navView = binding.navView
        tlPercentages = binding.tlPercentages
        tlNerves = binding.tlNerves
        tlDermatomes = binding.tlDermatomes
        if(alreadyExists) {
            dialogView = layoutInflater.inflate(R.layout.dialog_choose, null)
        }
        cvCSV = binding.cvCSV
        mvFront = binding.mvFront
        mvBack = binding.mvBack
        CoroutineScope(Dispatchers.IO).launch{
            val bPathFront = getFrontDrawings()
            val bPathBack = getBackDrawings()
            mvFront.paths = bPathFront
            mvBack.paths = bPathBack
        }
    }

    private fun initListeners(){
        cvMenu.setOnClickListener {
            drawerLayout.open()
        }
        cvCSV.setOnClickListener {
            NavigationHelper.downloadCSV(this)
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
}