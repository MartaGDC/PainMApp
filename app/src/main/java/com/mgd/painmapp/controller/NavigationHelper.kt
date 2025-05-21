package com.mgd.painmapp.controller

import android.app.Activity
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
import androidx.room.Room
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
    fun navigateToChoose(context: Activity, patient: String, researcher: String){ //solo ocurre desde summary view si el paciente ya ha sido registrado y cerrado
        val intent = Intent(context, ChooseActivity::class.java).apply {
            putExtra("patient_name", patient)
            putExtra("researcher_name", researcher)
        }
        context.startActivity(intent)
    }
    fun navigateToSensorial(context: Activity, patient: String, researcher: String, date: String, idEval: Long) {
        val intent = Intent(context, SensorialActivity::class.java).apply {
            putExtra("patient_name", patient)
            putExtra("researcher_name", researcher)
            putExtra("date", date)
            putExtra("type", "sensorial")
            putExtra("idGeneratedEvaluation", idEval)
        }
        context.startActivity(intent)
    }
    fun navigateToMotor() {

    }
    fun navigateToPsychosocial() {

    }
    private fun navigateToSummary(context: Activity, idEval: Long, alreadyExists:Boolean) {
        val intent = Intent(context, SummaryActivity::class.java).apply {
            putExtra("idGeneratedEvaluation", idEval)
            putExtra("alreadyExists", alreadyExists)
        }
        context.startActivity(intent)
    }

    fun downloadCSV(context: Activity){
        Toast.makeText(context, context.getString(R.string.downloading_csv), Toast.LENGTH_SHORT).show()
        val database = Room.databaseBuilder(
            context, PatientDatabase::class.java,
            "patient_database"
        ).build()
        CoroutineScope(Dispatchers.IO).launch {
            val csvTable = database.getMapDao().getFullCSV()
            context.runOnUiThread {
                TablesHelper.exportCSV(csvTable, context)
            }
        }
    }
    fun navigateToNewPatient(context:Activity) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }


    //Menu
    fun setupMenu(navView: NavigationView, drawerLayout: DrawerLayout, context: Activity, patient: String,
                  researcher: String, date: String, idEval: Long, listEntities: List<EvaluationEntity>?=null,
                  dialogView: View?=null, database: PatientDatabase?=null, activity:String?=null,
                  alreadyExists: Boolean=false) {
        navView.setNavigationItemSelectedListener { menuItem ->
            handleItemClick(menuItem, drawerLayout, context, patient, researcher, date,
                idEval, listEntities, dialogView, database, activity, alreadyExists)
            true
        }
    }

    private fun handleItemClick(menuItem: MenuItem, drawerLayout: DrawerLayout, context: Activity,
                                patient: String, researcher: String, date: String, idEval: Long,
                                listEntities: List<EvaluationEntity>?=null, dialogView: View?=null,
                                database: PatientDatabase?=null, actividad:String?=null,
                                alreadyExists: Boolean) {
        when (menuItem.itemId) {
            R.id.item_sensorial -> {
                if(!listEntities.isNullOrEmpty() && dialogView!=null && database!=null) {
                    if(validateUser("sensorial", listEntities, patient, dialogView, context, database, researcher, date, actividad)){
                        navigateToSensorial(context, patient, researcher, date, idEval = -1)
                    }
                }
                else{
                    navigateToSensorial(context, patient, researcher, date, idEval = idEval)
                }
            }
            R.id.item_motor -> {
                if(!listEntities.isNullOrEmpty() && dialogView!=null && database!=null) {
                    if(validateUser("motor", listEntities, patient, dialogView, context, database, researcher, date)){
                        navigateToMotor()
                    }
                }
                else{
                    navigateToMotor()
                }
            }
            R.id.item_psychosocial -> {
                if(!listEntities.isNullOrEmpty() && dialogView!=null && database!=null) {
                    if(validateUser("psychosocial", listEntities, patient, dialogView, context, database, researcher, date)){
                        navigateToPsychosocial()
                    }
                }
                else{
                    navigateToPsychosocial()
                }
            }
            R.id.item_summary -> {
                if (idEval ==(-1).toLong()) {
                    Toast.makeText(context,
                        context.getString(R.string.no_evaluation), Toast.LENGTH_SHORT).show()
                }
                else{
                    navigateToSummary(context, idEval, alreadyExists)
                }
            }
            R.id.item_new -> {
                navigateToNewPatient(context)
            }
            R.id.item_download -> {
                downloadCSV(context)
            }
            else -> {
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    fun validateUser(test: String, listEntities: List<EvaluationEntity>, patient: String, dialogView: View,
                     context: Activity, database: PatientDatabase, researcher: String, date: String,
                     activity:String?=null):Boolean {
        if (listEntities.any {it.patientName == patient && it.test == test}) { //Al iterar sobre cada entity de la lista, buscar el item con ese nombre y ese test
            val idEvaluation = listEntities.first {it.patientName == patient && it.test == test}.idEvaluation //Solo debe haber un registro. Elegimos first porque será el unico
            (dialogView.parent as? ViewGroup)?.removeView(dialogView) // Por si ha salido el dialogo y el usuario despues vuelto hacia atras a esta activity
            val dialog = AlertDialog.Builder(context).setView(dialogView).create()
            dialogView.findViewById<CardView>(R.id.btnOverwrite).setOnClickListener {
                overwrite(test, database, context, patient, researcher, date)
                dialog.dismiss()
            }
            if(activity=="summary"){
                dialogView.findViewById<TextView>(R.id.seeSummary).text =
                    context.getString(R.string.back_main_menu)
                dialogView.findViewById<CardView>(R.id.btnSummary).setOnClickListener {
                    navigateToChoose(context, patient, researcher)
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
    private fun overwrite(test: String, database: PatientDatabase, context: Activity, patient: String,
                          researcher: String, date: String){
        CoroutineScope(Dispatchers.IO).launch {
            database.getEvaluationDao().deleteEvaluationByPatientAndTest(patient, test) //No es necesario eliminar en symptom table por las inner joins.
        }
        when (test) {
            "sensorial" -> { navigateToSensorial(context, patient, researcher, date, idEval = -1) }
            "motor" -> { navigateToMotor() }
            else -> { navigateToPsychosocial() }
        }
    }
}