package com.mgd.painmapp.controller

import android.app.Activity
import android.content.Intent
import android.os.Environment
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
import com.mgd.painmapp.model.database.CSVTable
import com.mgd.painmapp.model.database.PatientDatabase
import com.mgd.painmapp.model.database.entities.EvaluationEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object NavigationHelper {
    fun navigateToChoose(context: Activity, patientName: String, researcherName: String){ //solo ocurre desde summary view si el paciente ya ha sido registrado y cerrado
        val intent = Intent(context, ChooseActivity::class.java).apply {
            putExtra("patient_name", patientName)
            putExtra("researcher_name", researcherName)
        }
        context.startActivity(intent)
    }
    fun navigateToSensorial(context: Activity, patientName: String, researcherName: String, currentDate: String, idGeneratedEvaluation: Long) {
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

    }
    fun navigateToPsychosocial() {

    }
    private fun navigateToSummary(context: Activity, idGeneratedEvaluation: Long, alreadyExists:Boolean) {
        val intent = Intent(context, SummaryActivity::class.java).apply {
            putExtra("idGeneratedEvaluation", idGeneratedEvaluation)
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
            val csvTable = database.getEvaluationDao().getFullCSV()
            context.runOnUiThread {
                exportCSV(csvTable)
            }
        }
    }
    fun navigateToNewPatient(context:Activity) {
        val intent = Intent(context, MainActivity::class.java)
        context.startActivity(intent)
    }


    //Menu
    fun setupMenu(navView: NavigationView, drawerLayout: DrawerLayout, context: Activity, patientName: String,
                  researcherName: String, currentDate: String, idGeneratedEvaluation: Long,
                  listEntities: List<EvaluationEntity>?=null, dialogView: View?=null, database: PatientDatabase?=null,
                  activity:String?=null, alreadyExists: Boolean=false) {
        navView.setNavigationItemSelectedListener { menuItem ->
            handleItemClick(menuItem, drawerLayout, context, patientName, researcherName, currentDate,
                idGeneratedEvaluation, listEntities, dialogView, database, activity, alreadyExists)
            true
        }
    }

    private fun handleItemClick(menuItem: MenuItem, drawerLayout: DrawerLayout, context: Activity,
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
            R.id.item_download -> {
                downloadCSV(context)
            }
            else -> {
            }
        }
        drawerLayout.closeDrawer(GravityCompat.START)
    }

    fun validateUser(test: String, listEntities: List<EvaluationEntity>, patientName: String,
                     dialogView: View, context: Activity, database: PatientDatabase, researcherName: String,
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
    private fun overwrite(test: String, database: PatientDatabase, context: Activity, patientName: String, researcherName: String, currentDate: String){
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

    private fun exportCSV(csvTable: List<CSVTable>):File?{
        val dateFormat = SimpleDateFormat("yyyyMMdd_HH.mm.ss", Locale.getDefault())
        val formattedDate = dateFormat.format(Date())
        val fileName = "PainMApp_${formattedDate}.csv"
        val header = listOf(
            "idEvaluation", "patient", "researcher", "date", "test",
            "idMap", "totalPatientPercentage", "rightPatientPercentage", "leftPatientPercentage",
            "totalPercentage", "rightPercentage", "leftPercentage",
            "nervioMedianoDerecho", "nervioRadialSuperficialDerecho", "nervioCubitalDerecho", "nervioMusculoCutaneoDerecho",
            "nerviosSupraclavicularesDerechos", "nervioFemoralDerecho", "nervioGenitalDerecho", "nervioIlioinguinoDerecho",
            "nervioObturadoDerecho", "nervioFemoralAnteriorDerecho", "nervioSafenoDerecho", "nervioPeroneoDerecho", "nervioSuralDerecho",
            "nervioBraquialDerecho", "nervioAntebrazoDerecho", "nervioRadialDerecho", "nervioAxilarDerecho",
            "nervioMedianoIzquierdo", "nervioRadialSuperficialIzquierdo", "nervioCubitalIzquierdo", "nervioMusculoCutaneoIzquierdo",
            "nerviosSupraclavicularesIzquierdos", "nervioFemoralIzquierdo", "nervioGenitalIzquierdo", "nervioIlioinguinoIzquierdo",
            "nervioObturadoIzquierdo", "nervioFemoralAnteriorIzquierdo", "nervioSafenoIzquierdo", "nervioPeroneoIzquierdo", "nervioSuralIzquierdo",
            "nervioBraquialIzquierdo", "nervioAntebrazoIzquierdo", "nervioRadialIzquierdo", "nervioAxilarIzquierdo",
            "RC3Derecha", "RC4Derecha", "RC5Derecha", "RC6Derecha", "RC7Derecha", "RC8Derecha", "RT1Derecha", "RT2Derecha", "RT3Derecha",
            "RT4Derecha", "RT5Derecha", "RT6Derecha", "RT7Derecha", "RT8Derecha", "RT9Derecha", "RT10Derecha", "RT11Derecha", "RT12Derecha",
            "RL1Derecha", "RL2Derecha", "RL3Derecha", "RL4Derecha", "RL5Derecha", "RS1Derecha", "RS2Derecha",
            "RC3Izquierda", "RC4Izquierda", "RC5Izquierda", "RC6Izquierda", "RC7Izquierda", "RC8Izquierda", "RT1Izquierda", "RT2Izquierda",
            "RT3Izquierda", "RT4Izquierda", "RT5Izquierda", "RT6Izquierda", "RT7Izquierda", "RT8Izquierda", "RT9Izquierda", "RT10Izquierda",
            "RT11Izquierda", "RT12Izquierda", "RL1Izquierda", "RL2Izquierda", "RL3Izquierda", "RL4Izquierda", "RL5Izquierda", "RS1Izquierda", "RS2Izquierda",
            "idSymptom", "intensity", "symptom", "symptomOtherText", "charactAgitating", "charactMiserable", "charactAnnoying", "charactUnbearable", "charactFatiguing",
            "charactPiercing", "charactOther", "charactOtherText", "timeContinuous", "timeWhen"
        )
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloads, fileName)
        return try {
            val writer = file.bufferedWriter()
            writer.write(header.joinToString(";"))
            writer.newLine()
            for (row in csvTable) {
                val csvRow = listOf(
                    row.idEvaluation,
                    row.patient,
                    row.researcher,
                    row.date,
                    row.test,
                    row.idMap,
                    row.totalPatientPercentage.toString().replace('.', ','),
                    row.rightPatientPercentage.toString().replace('.', ','),
                    row.leftPatientPercentage.toString().replace('.', ','),
                    row.totalPercentage.toString().replace('.', ','),
                    row.rightPercentage.toString().replace('.', ','),
                    row.leftPercentage.toString().replace('.', ','),
                    row.nervioMedianoDerecho.toString().replace('.', ','),
                    row.nervioRadialSuperficialDerecho.toString().replace('.', ','),
                    row.nervioCubitalDerecho.toString().replace('.', ','),
                    row.nervioMusculocutaneoDerecho.toString().replace('.', ','),
                    row.nerviosSupraclavicularesDerechos.toString().replace('.', ','),
                    row.nervioFemorocutaneoLatDerecho.toString().replace('.', ','),
                    row.nervioGenitofemoralDerecho.toString().replace('.', ','),
                    row.nervioIlioinguinalDerecho.toString().replace('.', ','),
                    row.nervioIliohipogastricoDerecho.toString().replace(".", ","),
                    row.nervioObturadoDerecho.toString().replace('.', ','),
                    row.nervioCutaneofemoralAntDerecho.toString().replace('.', ','),
                    row.nervioSafenoDerecho.toString().replace('.', ','),
                    row.nervioPeroneoSuperfDerecho.toString().replace('.', ','),
                    row.nervioSuralDerecho.toString().replace('.', ','),
                    row.nervioBraquialDerecho.toString().replace('.', ','),
                    row.nervioAntebrazoDerecho.toString().replace('.', ','),
                    row.nervioRadialDerecho.toString().replace('.', ','),
                    row.nervioAxilarDerecho.toString().replace('.', ','),
                    row.nerviosCervicalesDerechos.toString().replace('.', ','),
                    row.nervioTrigeminoIDerecho.toString().replace('.', ','),
                    row.nervioTrigeminoIIDerecho.toString().replace('.', ','),
                    row.nervioTrigeminoIIIDerecho.toString().replace('.', ','),
                    row.T1Derecho.toString().replace('.', ','),
                    row.T2Derecho.toString().replace('.', ','),
                    row.T3Derecho.toString().replace('.', ','),
                    row.T4Derecho.toString().replace('.', ','),
                    row.T5Derecho.toString().replace('.', ','),
                    row.T6Derecho.toString().replace('.', ','),
                    row.T7Derecho.toString().replace('.', ','),
                    row.T8Derecho.toString().replace('.', ','),
                    row.T9Derecho.toString().replace('.', ','),
                    row.T10Derecho.toString().replace('.', ','),
                    row.T11Derecho.toString().replace('.', ','),
                    row.T12Derecho.toString().replace('.', ','),
                    row.nervioMedianoIzquierdo.toString().replace('.', ','),
                    row.nervioRadialSuperficialIzquierdo.toString().replace('.', ','),
                    row.nervioCubitalIzquierdo.toString().replace('.', ','),
                    row.nervioMusculocutaneoIzquierdo.toString().replace('.', ','),
                    row.nerviosSupraclavicularesIzquierdos.toString().replace('.', ','),
                    row.nervioFemorocutaneoLatIzquierdo.toString().replace('.', ','),
                    row.nervioGenitofemoralIzquierdo.toString().replace('.', ','),
                    row.nervioIlioinguinalIzquierdo.toString().replace('.', ','),
                    row.nervioIliohipogastricoIzquierdo.toString().replace('.', ','),
                    row.nervioObturadoIzquierdo.toString().replace('.', ','),
                    row.nervioCutaneofemoralAntIzquierdo.toString().replace('.', ','),
                    row.nervioSafenoIzquierdo.toString().replace('.', ','),
                    row.nervioPeroneoSuperfIzquierdo.toString().replace('.', ','),
                    row.nervioSuralIzquierdo.toString().replace('.', ','),
                    row.nervioBraquialIzquierdo.toString().replace('.', ','),
                    row.nervioAntebrazoIzquierdo.toString().replace('.', ','),
                    row.nervioRadialIzquierdo.toString().replace('.', ','),
                    row.nervioAxilarIzquierdo.toString().replace('.', ','),
                    row.nerviosCervicalesIzquierdo.toString().replace('.', ','),
                    row.nervioTrigeminoIIzquierdo.toString().replace('.', ','),
                    row.nervioTrigeminoIIIzquierdo.toString().replace('.', ','),
                    row.nervioTrigeminoIIIIzquierdo.toString().replace('.', ','),
                    row.T1Izquierdo.toString().replace('.', ','),
                    row.T2Izquierdo.toString().replace('.', ','),
                    row.T3Izquierdo.toString().replace('.', ','),
                    row.T4Izquierdo.toString().replace('.', ','),
                    row.T5Izquierdo.toString().replace('.', ','),
                    row.T6Izquierdo.toString().replace('.', ','),
                    row.T7Izquierdo.toString().replace('.', ','),
                    row.T8Izquierdo.toString().replace('.', ','),
                    row.T9Izquierdo.toString().replace('.', ','),
                    row.T10Izquierdo.toString().replace('.', ','),
                    row.T11Izquierdo.toString().replace('.', ','),
                    row.T12Izquierdo.toString().replace('.', ','),
                    row.RC3Derecha.toString().replace('.', ','),
                    row.RC4Derecha.toString().replace('.', ','),
                    row.RC5Derecha.toString().replace('.', ','),
                    row.RC6Derecha.toString().replace('.', ','),
                    row.RC7Derecha.toString().replace('.', ','),
                    row.RC8Derecha.toString().replace('.', ','),
                    row.RT1Derecha.toString().replace('.', ','),
                    row.RT2Derecha.toString().replace('.', ','),
                    row.RT3Derecha.toString().replace('.', ','),
                    row.RT4Derecha.toString().replace('.', ','),
                    row.RT5Derecha.toString().replace('.', ','),
                    row.RT6Derecha.toString().replace('.', ','),
                    row.RT7Derecha.toString().replace('.', ','),
                    row.RT8Derecha.toString().replace('.', ','),
                    row.RT9Derecha.toString().replace('.', ','),
                    row.RT10Derecha.toString().replace('.', ','),
                    row.RT11Derecha.toString().replace('.', ','),
                    row.RT12Derecha.toString().replace('.', ','),
                    row.RL1Derecha.toString().replace('.', ','),
                    row.RL2Derecha.toString().replace('.', ','),
                    row.RL3Derecha.toString().replace('.', ','),
                    row.RL4Derecha.toString().replace('.', ','),
                    row.RL5Derecha.toString().replace('.', ','),
                    row.RS1Derecha.toString().replace('.', ','),
                    row.RS2Derecha.toString().replace('.', ','),
                    row.RC3Izquierda.toString().replace('.', ','),
                    row.RC4Izquierda.toString().replace('.', ','),
                    row.RC5Izquierda.toString().replace('.', ','),
                    row.RC6Izquierda.toString().replace('.', ','),
                    row.RC7Izquierda.toString().replace('.', ','),
                    row.RC8Izquierda.toString().replace('.', ','),
                    row.RT1Izquierda.toString().replace('.', ','),
                    row.RT2Izquierda.toString().replace('.', ','),
                    row.RT3Izquierda.toString().replace('.', ','),
                    row.RT4Izquierda.toString().replace('.', ','),
                    row.RT5Izquierda.toString().replace('.', ','),
                    row.RT6Izquierda.toString().replace('.', ','),
                    row.RT7Izquierda.toString().replace('.', ','),
                    row.RT8Izquierda.toString().replace('.', ','),
                    row.RT9Izquierda.toString().replace('.', ','),
                    row.RT10Izquierda.toString().replace('.', ','),
                    row.RT11Izquierda.toString().replace('.', ','),
                    row.RT12Izquierda.toString().replace('.', ','),
                    row.RL1Izquierda.toString().replace('.', ','),
                    row.RL2Izquierda.toString().replace('.', ','),
                    row.RL3Izquierda.toString().replace('.', ','),
                    row.RL4Izquierda.toString().replace('.', ','),
                    row.RL5Izquierda.toString().replace('.', ','),
                    row.RS1Izquierda.toString().replace('.', ','),
                    row.RS2Izquierda.toString().replace('.', ','),
                    row.idSymptom,
                    row.intensity.toString().replace('.', ','),
                    row.symptom,
                    row.symptomOtherText,
                    row.charactAgitating,
                    row.charactMiserable,
                    row.charactAnnoying,
                    row.charactUnbearable,
                    row.charactFatiguing,
                    row.charactPiercing,
                    row.charactOther,
                    row.charactOtherText,
                    row.timeContinuous,
                    row.timeWhen
                ).joinToString(";") { it.toString() }
                writer.write(csvRow)
                writer.newLine()
            }
            writer.close()
            file
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

}