package com.mgd.painmapp.controller.activities

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
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
    private lateinit var listEntities: List<EvaluationEntity>

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
            idGeneradoEvaluation = -1
        )
    }

    private fun initListeners(){
        cvMenu.setOnClickListener {
            drawerLayout.open()
        }
        sensorial.setOnClickListener {
            if(validarUsuario("sensorial")){
                NavigationHelper.navigateToSensorial(
                    this,
                    patientName,
                    researcherName,
                    currentDate,
                    idGeneradoEvaluation = -1
                )
            }
        }
        motor.setOnClickListener {
            if(validarUsuario("motor")){
                NavigationHelper.navigateToMotor()
            }
        }
        psychosocial.setOnClickListener {
            if(validarUsuario("psychosocial")){
                NavigationHelper.navigateToPsychosocial()
            }
        }
    }

    private fun validarUsuario(testIntroducido: String):Boolean {
        Log.d("listEntities", listEntities.toString())
        if (listEntities.any {it.patientName == patientName && it.test == testIntroducido}) { //Al iterar sobre cada entity de la lista, buscar el item con ese nombre y ese test
            val idEvaluation = listEntities.first {it.patientName == patientName && it.test == testIntroducido}.idEvaluation //Solo debe haber un registro. Elegimos first porque será el unico
            val dialog = AlertDialog.Builder(this).setView(dialogView).create()
            dialogView.findViewById<CardView>(R.id.btnSobreescribir).setOnClickListener {
                sobrescribir(idEvaluation, testIntroducido)
                dialog.dismiss()
            }
            dialogView.findViewById<CardView>(R.id.btnResumen).setOnClickListener {
                NavigationHelper.navigateToSummary(this, idEvaluation)
                dialog.dismiss()
            }

            dialogView.findViewById<CardView>(R.id.btnCancelar).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
            return false
        }
        return true
    }
    private fun sobrescribir(idEvaluation: Long, test: String){
        CoroutineScope(Dispatchers.IO).launch {
            database.getMapDao().deleteMapsByEvaluation(idEvaluation) //No es necesario eliminar en symptom table por las inner joins.
        }
        if(test == "sensorial"){
            NavigationHelper.navigateToSensorial(
                this,
                patientName,
                researcherName,
                currentDate,
                idGeneradoEvaluation = idEvaluation
            )
        }
        else if (test == "motor"){
            NavigationHelper.navigateToMotor()
        }
        else {
            NavigationHelper.navigateToPsychosocial()
        }
    }
}