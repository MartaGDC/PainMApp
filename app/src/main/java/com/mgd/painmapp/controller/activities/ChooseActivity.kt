package com.mgd.painmapp.controller.activities

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.google.android.material.navigation.NavigationView
import com.mgd.painmapp.R
import com.mgd.painmapp.controller.NavigationHelper
import com.mgd.painmapp.databinding.ActivityChooseBinding
import com.mgd.painmapp.model.database.PatientDatabase
import com.mgd.painmapp.model.database.entities.EvaluationEntity
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
    private lateinit var psychosocial: CardView
    private lateinit var dialogView: View
    private lateinit var currentDate: String
    private lateinit var database: PatientDatabase
    private var listEntities: List<EvaluationEntity> = emptyList()

    //Menu:
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var cvMenu: CardView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChooseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        patientName = intent.getStringExtra("patient_name").toString()
        researcherName = intent.getStringExtra("researcher_name").toString()
        currentDate = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()
        CoroutineScope(Dispatchers.IO).launch {
            listEntities = database.getEvaluationDao().getEvaluations()
        }
        initComponents()
        initListeners()
    }

    private fun initComponents(){
        sensorial = binding.cvSensorial
        motor = binding.cvMotor
        psychosocial = binding.cvPsychosocial
        dialogView = layoutInflater.inflate(R.layout.dialog_choose, null)

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
            idGeneratedEvaluation = -1,
            listEntities, dialogView, database
        )
    }

    private fun initListeners(){
        cvMenu.setOnClickListener {
            drawerLayout.open()
        }
        sensorial.setOnClickListener {
            if(NavigationHelper.validateUser("sensorial", listEntities, patientName, dialogView, this, database, researcherName, currentDate)){
                NavigationHelper.navigateToSensorial(
                    this,
                    patientName,
                    researcherName,
                    currentDate,
                    idGeneratedEvaluation = -1
                )
            }
        }
        motor.setOnClickListener {
            if(NavigationHelper.validateUser("motor", listEntities, patientName, dialogView, this, database, researcherName, currentDate)){
                NavigationHelper.navigateToMotor()
            }
        }
        psychosocial.setOnClickListener {
            if(NavigationHelper.validateUser("psychosocial", listEntities, patientName, dialogView, this, database, researcherName, currentDate)){
                NavigationHelper.navigateToPsychosocial()
            }
        }
    }
}