package com.mgd.painmapp.controller

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.google.android.material.navigation.NavigationView
import com.mgd.painmapp.R
import com.mgd.painmapp.controller.activities.ChooseActivity
import com.mgd.painmapp.controller.activities.MainActivity
import com.mgd.painmapp.controller.activities.SensorialActivity
import com.mgd.painmapp.controller.activities.SummaryActivity
import com.mgd.painmapp.model.database.PatientDatabase
import com.mgd.painmapp.model.database.entities.EvaluationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object NavigationHelper {
    fun navigateToChoose(context: Context, patientName: String, researcherName: String){ //solo ocurre desde summary view si el paciente ya ha sido registrado y cerrado
        val intent = Intent(context, ChooseActivity::class.java).apply {
            putExtra("patient_name", patientName)
            putExtra("researcher_name", researcherName)
        }
        context.startActivity(intent)
    }
    fun navigateToSensorial(context: Context, patientName: String, researcherName: String, currentDate: String, idGeneratedEvaluation: Long) {
        val intent = Intent(context, SensorialActivity::class.java).apply {
            putExtra("patient_name", patientName)
            putExtra("researcher_name", researcherName)
            putExtra("date", currentDate)
            putExtra("type", "sensorial")
            putExtra("idGeneratedEvaluation", idGeneratedEvaluation)
        }
        context.startActivity(intent)
    }
    fun navigateToMotor() {
        TODO()
    }
    fun navigateToPsychosocial() {
        TODO()
    }
    private fun navigateToSummary(context: Context, idGeneratedEvaluation: Long, alreadyExists:Boolean) {
        val intent = Intent(context, SummaryActivity::class.java).apply {
            putExtra("idGeneratedEvaluation", idGeneratedEvaluation)
            putExtra("alreadyExists", alreadyExists)
        }
        context.startActivity(intent)
    }
    fun navigateToNewPatient(context:Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }


    //Menu
    fun setupMenu(navView: NavigationView, drawerLayout: DrawerLayout, context: Context, patientName: String,
                  researcherName: String, currentDate: String, idGeneratedEvaluation: Long,
                  listEntities: List<EvaluationEntity>?=null, dialogView: View?=null, database: PatientDatabase?=null,
                  activity:String?=null, alreadyExists: Boolean=false) {
        navView.setNavigationItemSelectedListener { menuItem ->
            handleItemClick(menuItem, drawerLayout, context, patientName, researcherName, currentDate,
                idGeneratedEvaluation, listEntities, dialogView, database, activity, alreadyExists)
            true
        }
    }

    private fun handleItemClick(menuItem: MenuItem, drawerLayout: DrawerLayout, context: Context,
                                patientName: String, researcherName: String, currentDate: String,
                                idGeneratedEvaluation: Long, listEntities: List<EvaluationEntity>?=null,
                                dialogView: View?=null, database: PatientDatabase?=null, actividad:String?=null, alreadyExists: Boolean) {
        when (menuItem.itemId) {
            R.id.item_sensorial -> {
                if(!listEntities.isNullOrEmpty() && dialogView!=null && database!=null) {
                    if(validateUser("sensorial", listEntities, patientName, dialogView, context, database, researcherName, currentDate, actividad)){
                        navigateToSensorial(context, patientName, researcherName, currentDate, idGeneratedEvaluation = -1)
                    }
                }
                else{
                    navigateToSensorial(context, patientName, researcherName, currentDate, idGeneratedEvaluation = idGeneratedEvaluation)
                }
            }
            R.id.item_motor -> {
                if(!listEntities.isNullOrEmpty() && dialogView!=null && database!=null) {
                    if(validateUser("motor", listEntities, patientName, dialogView, context, database, researcherName, currentDate)){
                        navigateToMotor()
                    }
                }
                else{
                    navigateToMotor()
                }
            }
            R.id.item_psychosocial -> {
                if(!listEntities.isNullOrEmpty() && dialogView!=null && database!=null) {
                    if(validateUser("psychosocial", listEntities, patientName, dialogView, context, database, researcherName, currentDate)){
                        navigateToPsychosocial()
                    }
                }
                else{
                    navigateToPsychosocial()
                }
            }
            R.id.item_summary -> {
                if (idGeneratedEvaluation ==(-1).toLong()) {
                    Toast.makeText(context,
                        context.getString(R.string.no_symptom), Toast.LENGTH_SHORT).show()
                }
                else{
                    navigateToSummary(context, idGeneratedEvaluation, alreadyExists)
                }
            }
            R.id.item_new -> {
                navigateToNewPatient(context)
            }
            else -> {
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
    }
    fun validateUser(test: String, listEntities: List<EvaluationEntity>, patientName: String,
                     dialogView: View, context: Context, database: PatientDatabase, researcherName: String,
                     currentDate: String, activity:String?=null):Boolean {
        if (listEntities.any {it.patientName == patientName && it.test == test}) { //Al iterar sobre cada entity de la lista, buscar el item con ese nombre y ese test
            val idEvaluation = listEntities.first {it.patientName == patientName && it.test == test}.idEvaluation //Solo debe haber un registro. Elegimos first porque será el unico
            (dialogView.parent as? ViewGroup)?.removeView(dialogView) // Por si ha salido el dialogo y el usuario despues vuelto hacia atras a esta activity
            val dialog = AlertDialog.Builder(context).setView(dialogView).create()
            dialogView.findViewById<CardView>(R.id.btnOverwrite).setOnClickListener {
                overwrite(test, database, context, patientName, researcherName, currentDate)
                dialog.dismiss()
            }
            if(activity=="summary"){
                dialogView.findViewById<TextView>(R.id.seeSummary).text =
                    context.getString(R.string.back_main_menu)
                dialogView.findViewById<CardView>(R.id.btnSummary).setOnClickListener {
                    navigateToChoose(context, patientName, researcherName)
                    dialog.dismiss()
                }
            }
            else{
                dialogView.findViewById<CardView>(R.id.btnSummary).setOnClickListener {
                    navigateToSummary(context, idEvaluation, true)
                    dialog.dismiss()
                }
            }
            dialogView.findViewById<CardView>(R.id.btnCancel).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
            return false
        }
        return true
    }
    private fun overwrite(test: String, database: PatientDatabase, context: Context, patientName: String, researcherName: String, currentDate: String){
        CoroutineScope(Dispatchers.IO).launch {
            database.getEvaluationDao().deleteEvaluationByPatientAndTest(patientName, test) //No es necesario eliminar en symptom table por las inner joins.
        }
        when (test) {
            "sensorial" -> {
                navigateToSensorial(
                    context,
                    patientName,
                    researcherName,
                    currentDate,
                    idGeneratedEvaluation = -1
                )
            }

            "motor" -> {
                navigateToMotor()
            }

            else -> {
                navigateToPsychosocial()
            }
        }
    }
}