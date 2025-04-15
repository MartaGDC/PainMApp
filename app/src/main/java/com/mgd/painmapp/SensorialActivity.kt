package com.mgd.painmapp

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.room.Room
import com.google.android.material.navigation.NavigationView
import com.mgd.painmapp.Database.Entities.toDatabase
import com.mgd.painmapp.Database.Entities.toSymptom
import com.mgd.painmapp.Database.PatientDatabase
import com.mgd.painmapp.databinding.ActivitySensorialBinding
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
    private var idGeneradoEvaluation: Long = -1
    private lateinit var database: PatientDatabase
    //Menu
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        patientName = intent.getStringExtra("PATIENT_NAME").toString()
        researcherName = intent.getStringExtra("RESEARCHER_NAME").toString()
        currentDate = intent.getStringExtra("DATE").toString()
        type = intent.getStringExtra("TYPE").toString()
        idGeneradoEvaluation = intent.getLongExtra("idGeneradoEvaluation", -1) //Intent desde Survey
        /*database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()
        this.deleteDatabase("patient_database")*/
        database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()


        initComponents()
        initListeners()
    }

    private fun initComponents(){
        adapter = SymptomsAdapter(emptyList(), this)
        if(idGeneradoEvaluation != (-1).toLong()){
            CoroutineScope(Dispatchers.IO).launch {
                val symptomsList = database.getSymptomDao().getSymptomsByEvaluation(idGeneradoEvaluation)
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

        //Menu:
        drawerLayout = binding.main
        navView = binding.navView
        setupMenu()
    }

    private fun initListeners(){
        cvAdd.setOnClickListener { //Se guarda la información de la evaluation (sin contenido en la evaluacion, en este caso sintomas, no se guarda nada en la tabla de evaluaciones)
            CoroutineScope(Dispatchers.IO).launch { //Creamos aqui la coroutine, llamando a una funcion suspend
                fillDatabase()
                val intent = Intent(this@SensorialActivity, LocationActivity::class.java).apply {
                    putExtra("idGeneradoEvaluation", idGeneradoEvaluation)
                }
                startActivity(intent)
            }

        }
    }

    private fun getFrontDrawings(): List<String> {
        val bPath: List<String>
        if (idGeneradoEvaluation != (-1).toLong()) { //Hay registro de evaluación
            bPath = database.getMapDao().getFrontPathsDrawnById(idGeneradoEvaluation)
            return bPath
        }
        return emptyList()
    }
    private fun getBackDrawings(): List<String> {
        val bPath: List<String>
        if (idGeneradoEvaluation != (-1).toLong()) { //Hay registro de evaluación
            bPath = database.getMapDao().getBackPathsDrawnById(idGeneradoEvaluation)
            return bPath
        }
        return emptyList()
    }


    private suspend fun fillDatabase() { //Suspend para que el hilo principal espere
        if (idGeneradoEvaluation == (-1).toLong()) { //Si no hay registro de evaluación
            val evaluationEntity =
                Evaluation(patientName, researcherName, currentDate, type).toDatabase()
                idGeneradoEvaluation = database.getEvaluationDao().insertEvaluation(evaluationEntity) //Se elimina la coroutine, porque se lanza desde el listener
        }
        else { //Si ya se ha registrado
            return
        }
    }


    private fun setupMenu() {
        navView.setNavigationItemSelectedListener { menuItem ->
            handleMenuItemClick(menuItem)
            true
        }
    }
    private fun handleMenuItemClick(item: MenuItem) {
        val chooseActivity = ChooseActivity()
        try{
            when (item.itemId) {
                R.id.item_sensorial -> {
                    chooseActivity.navigateToSensorial(patientName, researcherName, currentDate)
                }
                R.id.item_motor -> {
                    chooseActivity.navigateToMotor()
                }
                else -> {
                }
            }
        }
        catch (e: Exception) {
            e.printStackTrace()
            //Quiero creer que el error procede de querer invocar la misma activity
        }

        drawerLayout.closeDrawer(GravityCompat.START)
    }




}