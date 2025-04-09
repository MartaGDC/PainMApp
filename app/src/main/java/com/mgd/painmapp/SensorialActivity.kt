package com.mgd.painmapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    private lateinit var patientName: String
    private lateinit var researcherName: String
    private lateinit var currentDate: String
    private lateinit var type: String
    private var idGeneradoEvaluation: Long = -1
    private lateinit var CVAdd: CardView
    private lateinit var adapter: SymptomsAdapter
    private lateinit var database: PatientDatabase
    //Menu
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigaionView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySensorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        patientName = intent.getStringExtra("PATIENT_NAME").toString()
        researcherName = intent.getStringExtra("RESEARCHER_NAME").toString()
        currentDate = intent.getStringExtra("DATE").toString()
        type = intent.getStringExtra("TYPE").toString()
        idGeneradoEvaluation = intent.getLongExtra("ID", -1) //Intent desde Survey
        Log.d("ID", idGeneradoEvaluation.toString() + " en SensorialActivity desde survey")
        /*database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()
        this.deleteDatabase("patient_database") */
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
                    Log.d("Symptoms", symptoms.toString())
                }
            }
        }
        else{
            adapter.updateList(emptyList())
        }
        binding.RVsymptoms.setHasFixedSize(true)
        binding.RVsymptoms.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.RVsymptoms.adapter = adapter

        CVAdd = binding.CVAdd
        //Menu:
        drawerLayout = binding.main
        navigaionView = binding.navView
        setupDrawerContent()
    }

    private fun initListeners(){
        CVAdd.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch { //Creamos aqui la coroutine, llamando a una funcion suspend
                fillDatabase()
                val intent = Intent(this@SensorialActivity, LocationActivity::class.java).apply {
                    putExtra("ID", idGeneradoEvaluation)
                }
                startActivity(intent)
            }

        }
    }

    private suspend fun fillDatabase() { //Para que el hilo principal espere
        if (idGeneradoEvaluation == (-1).toLong()) { //Si no hay registro de evaluación
            val evaluationEntity =
                Evaluation(patientName, researcherName, currentDate, type).toDatabase()
                idGeneradoEvaluation = database.getEvaluationDao().insertEvaluation(evaluationEntity) //Se elimina la coroutine, porque se lanza desde el listener
        }
        else { //Si ya se ha registrado
            return
        }

    }


    fun setupDrawerContent() {
        navigaionView.setNavigationItemSelectedListener { menuItem ->
            handleMenuItemClick(menuItem)
            true
        }
    }

    private fun handleMenuItemClick(item: MenuItem) {
        var chooseActivity = ChooseActivity()
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