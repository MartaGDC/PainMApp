package com.mgd.painmapp.controller

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat.finishAffinity
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
    fun navigateToSensorial(context: Context, patientName: String, researcherName: String, currentDate: String, idGeneradoEvaluation: Long) {
        val intent = Intent(context, SensorialActivity::class.java).apply {
            putExtra("patient_name", patientName)
            putExtra("researcher_name", researcherName)
            putExtra("date", currentDate)
            putExtra("type", "sensorial")
            putExtra("idGeneradoEvaluation", idGeneradoEvaluation)
        }
        context.startActivity(intent)
    }
    fun navigateToMotor() {
        TODO()
    }
    fun navigateToPsychosocial() {
        TODO()
    }
    fun navigateToSummary(context: Context, idGeneradoEvaluation: Long, yaExiste:Boolean=false) {
        val intent = Intent(context, SummaryActivity::class.java).apply {
            putExtra("idGeneradoEvaluation", idGeneradoEvaluation)
            putExtra("yaExiste", yaExiste)
        }
        context.startActivity(intent)
    }
    fun navigateToNewPatient(context:Context) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }


    //Menu
    fun setupMenu(navView: NavigationView, drawerLayout: DrawerLayout, context: Context, patientName: String,
                  researcherName: String, currentDate: String, idGeneradoEvaluation: Long,
                  listEntities: List<EvaluationEntity>?=null, dialogView: View?=null, database: PatientDatabase?=null,
                  actividad:String?=null) {
        navView.setNavigationItemSelectedListener { menuItem ->
            handleMenuItemClick(menuItem, drawerLayout, context, patientName, researcherName, currentDate,
                idGeneradoEvaluation, listEntities, dialogView, database, actividad)
            true
        }
    }

    private fun handleMenuItemClick(menuItem: MenuItem, drawerLayout: DrawerLayout, context: Context,
                                    patientName: String, researcherName: String, currentDate: String,
                                    idGeneradoEvaluation: Long, listEntities: List<EvaluationEntity>?=null,
                                    dialogView: View?=null, database: PatientDatabase?=null, actividad:String?=null) {
        when (menuItem.itemId) {
            R.id.item_sensorial -> {
                if(!listEntities.isNullOrEmpty() && dialogView!=null && database!=null) {
                    if(validarUsuario("sensorial", listEntities, patientName, dialogView, context, database, researcherName, currentDate, actividad)){
                        navigateToSensorial(context, patientName, researcherName, currentDate, idGeneradoEvaluation = -1)
                    }
                }
                else{
                    navigateToSensorial(context, patientName, researcherName, currentDate, idGeneradoEvaluation = idGeneradoEvaluation)
                }
            }
            R.id.item_motor -> {
                if(!listEntities.isNullOrEmpty() && dialogView!=null && database!=null) {
                    if(validarUsuario("motor", listEntities, patientName, dialogView, context, database, researcherName, currentDate)){
                        navigateToMotor()
                    }
                }
                else{
                    navigateToMotor()
                }
            }
            R.id.item_psicosocial -> {
                if(!listEntities.isNullOrEmpty() && dialogView!=null && database!=null) {
                    if(validarUsuario("psychosocial", listEntities, patientName, dialogView, context, database, researcherName, currentDate)){
                        navigateToPsychosocial()
                    }
                }
                else{
                    navigateToPsychosocial()
                }
            }
            R.id.item_resumen -> {
                if (idGeneradoEvaluation ==-1.toLong()) {
                    Toast.makeText(context, "No hay ningún síntoma que mostrar", Toast.LENGTH_SHORT).show()
                }
                else{
                    navigateToSummary(context, idGeneradoEvaluation)
                }
            }
            R.id.item_nuevo -> {
                navigateToNewPatient(context)
            }
            else -> {
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
    }
    fun validarUsuario(testIntroducido: String, listEntities: List<EvaluationEntity>, patientName: String,
                       dialogView: View, context: Context, database: PatientDatabase, researcherName: String,
                       currentDate: String, actividad:String?=null):Boolean {
        if (listEntities.any {it.patientName == patientName && it.test == testIntroducido}) { //Al iterar sobre cada entity de la lista, buscar el item con ese nombre y ese test
            val idEvaluation = listEntities.first {it.patientName == patientName && it.test == testIntroducido}.idEvaluation //Solo debe haber un registro. Elegimos first porque será el unico
            (dialogView.parent as? ViewGroup)?.removeView(dialogView) // Por si ha salido el dialogo y el usuario despues vuelto hacia atras a esta activity
            val dialog = AlertDialog.Builder(context).setView(dialogView).create()
            dialogView.findViewById<CardView>(R.id.btnSobreescribir).setOnClickListener {
                sobrescribir(idEvaluation, testIntroducido, database, context, patientName, researcherName, currentDate)
                dialog.dismiss()
            }
            if(actividad=="summary"){
                dialogView.findViewById<TextView>(R.id.verResumen).text = "Volver al menú principal"
                dialogView.findViewById<CardView>(R.id.btnResumen).setOnClickListener {
                    navigateToChoose(context, patientName, researcherName)
                    dialog.dismiss()
                }
            }
            else{
                dialogView.findViewById<CardView>(R.id.btnResumen).setOnClickListener {
                    navigateToSummary(context, idEvaluation, true)
                    dialog.dismiss()
                }
            }
            dialogView.findViewById<CardView>(R.id.btnCancelar).setOnClickListener {
                dialog.dismiss()
            }
            dialog.show()
            return false
        }
        return true
    }
    private fun sobrescribir(idEvaluation: Long, test: String, database: PatientDatabase, context: Context, patientName: String, researcherName: String, currentDate: String){
        CoroutineScope(Dispatchers.IO).launch {
            database.getEvaluationDao().deleteEvaluationByPatientAndTest(patientName, test) //No es necesario eliminar en symptom table por las inner joins.
        }
        if(test == "sensorial"){
            navigateToSensorial(
                context,
                patientName,
                researcherName,
                currentDate,
                idGeneradoEvaluation = -1
            )
        }
        else if (test == "motor"){
            navigateToMotor()
        }
        else {
            navigateToPsychosocial()
        }
    }
}