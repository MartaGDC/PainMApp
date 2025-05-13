package com.mgd.painmapp.controller.activities

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.google.android.material.navigation.NavigationView
import com.mgd.painmapp.R
import com.mgd.painmapp.controller.InterpretationHelper
import com.mgd.painmapp.controller.NavigationHelper
import com.mgd.painmapp.databinding.ActivitySummaryBinding
import com.mgd.painmapp.model.database.CSVTable
import com.mgd.painmapp.model.database.NervesTable
import com.mgd.painmapp.model.database.PatientDatabase
import com.mgd.painmapp.model.database.SymptomTable
import com.mgd.painmapp.model.database.entities.EvaluationEntity
import com.mgd.painmapp.view.MapViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SummaryActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySummaryBinding
    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navView: NavigationView
    private lateinit var cvCSV: CardView
    private lateinit var cvMenu: CardView
    private var dialogView: View? = null
    private lateinit var mvFront: MapViews
    private lateinit var mvBack: MapViews
    private var idGeneratedEvaluation: Long = -1
    private var alreadyExists: Boolean = false
    private lateinit var database: PatientDatabase
    private lateinit var listEntities: List<EvaluationEntity>
    private lateinit var symptomsTable: List<SymptomTable>
    private lateinit var nervesTable: List<NervesTable>

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
        CoroutineScope(Dispatchers.IO).launch {
            listEntities = database.getEvaluationDao().getEvaluations()
        }

        initComponents()
        initListeners()

        CoroutineScope(Dispatchers.IO).launch {
            symptomsTable = database.getSymptomDao().getSymptomsTableByEvaluation(idGeneratedEvaluation)
            nervesTable = database.getMapDao().getNervesTableByEvaluation(idGeneratedEvaluation)
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
                tableSymptoms(symptomsTable)
                tableNerves(nervesTable)
            }
        }
    }

    private fun initComponents(){
        cvMenu = binding.cvMenu
        drawerLayout = binding.main
        navView = binding.navView
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

    private fun getFrontDrawings(): List<String> {
        val bPath: List<String>
        if (idGeneratedEvaluation != (-1).toLong()) { //Hay registro de evaluación
            bPath = database.getMapDao().getFrontPathsDrawnById(idGeneratedEvaluation)
            return bPath
        }
        return emptyList()
    }
    private fun getBackDrawings(): List<String> {
        val bPath: List<String>
        if (idGeneratedEvaluation != (-1).toLong()) { //Hay registro de evaluación
            bPath = database.getMapDao().getBackPathsDrawnById(idGeneratedEvaluation)
            return bPath
        }
        return emptyList()
    }

    private fun tableSymptoms(symptomsTable: List<SymptomTable>){
        val table = binding.tlPercentages
        table.removeAllViews() //Comenzar con tabla limpia
        val titles = TableRow(this).apply{ // para añadir las celdas con las caracteristicas correctas, creamos una función extensión para TableRow (celda para la fila)
            insertCell("",  false, 1)
            insertCell("",  true,0 )
            insertCell("% de área corporal", false, 1)
            insertCell("% derecho",  false, 1)
            insertCell("% izquierdo",  false, 1)
        }
        table.addView(titles)
        val rowGeneral = TableRow(this).apply { //No es la suma de los porcentajes, ya que pueden solaparse
            insertCell("General",  true,0 )
            insertCell("",  true,0 )
            insertCell(String.format(Locale.getDefault(),"%.1f%%", symptomsTable[0].totalPatientPercentage), false, 1)
            insertCell(String.format(Locale.getDefault(),"%.1f%%", symptomsTable[0].rightPatientPercentage),  false, 1)
            insertCell(String.format(Locale.getDefault(),"%.1f%%", symptomsTable[0].leftPatientPercentage),  false, 1)
        }
        table.addView(rowGeneral)
        var firstSymptom = true
        for (symptom in symptomsTable){
            if (firstSymptom) {
                val row = TableRow(this).apply {
                    insertCell("Por síntomas", true, 0)
                    Log.d("symptom", symptom.symptomOtherText)
                    if(symptom.symptomOtherText.isNotEmpty()){
                        insertCell(symptom.symptomOtherText, false,0)
                    }
                    else{
                        insertCell(symptom.symptom, false, 0)
                    }
                    insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.totalPercentage), false, 1)
                    insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.rightPercentage), false, 1)
                    insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.leftPercentage),  false, 1)
                }
                table.addView(row)
                firstSymptom = false
            } else {
                val row = TableRow(this).apply {
                    insertCell("",  true, 0)
                    insertCell(symptom.symptom, false, 0)
                    insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.totalPercentage), false, 1)
                    insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.rightPercentage), false, 1)
                    insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.leftPercentage), false, 1)
                }
                table.addView(row)
            }
        }
    }

    private fun tableNerves(nervesTable: List<NervesTable>) {
        val nerves = InterpretationHelper.getFrontPeripheralNerves(this)
        val table = binding.tlNerves
        table.removeAllViews()
        for (symptom in nervesTable){
            var row = TableRow(this).apply {
                if(symptom.symptomOtherText.isNotEmpty()){
                    insertCell(symptom.symptomOtherText, false,0)
                }
                else{
                    insertCell(symptom.symptom, true, 1)
                }
                insertCell("", false, 0)
            }
            var count = 0
            table.addView(row)
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioMedianoDerecho), false, 1)
            }
            if(symptom.nervioMedianoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioRadialSuperficialDerecho), false, 1)
            }
            if(symptom.nervioRadialSuperficialDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioCubitalDerecho), false, 1)
            }
            if(symptom.nervioCubitalDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioMusculoCutaneoDerecho), false, 1)
            }
            if(symptom.nervioMusculoCutaneoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nerviosSupraclavicularesDerechos), false, 1)
            }
            if(symptom.nerviosSupraclavicularesDerechos!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioFemoralDerecho), false, 1)
            }
            if(symptom.nervioFemoralDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioGenitalDerecho), false, 1)
            }
            if(symptom.nervioGenitalDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioIlioinguinoDerecho), false, 1)
            }
            if(symptom.nervioIlioinguinoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioObturadoDerecho), false, 1)
            }
            if(symptom.nervioObturadoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioFemoralAnteriorDerecho), false, 1)
            }
            if(symptom.nervioFemoralAnteriorDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioSafenoDerecho), false, 1)
            }
            if(symptom.nervioSafenoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioPeroneoDerecho), false, 1)
            }
            if(symptom.nervioPeroneoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioSuralDerecho), false, 1)
            }
            if(symptom.nervioSuralDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioBraquialDerecho), false, 1)
            }
            if(symptom.nervioBraquialDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioAntebrazoDerecho), false, 1)
            }
            if(symptom.nervioAntebrazoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioRadialDerecho), false, 1)
            }
            if(symptom.nervioRadialDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioAxilarDerecho), false, 1)
            }
            if(symptom.nervioAxilarDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioMedianoIzquierdo), false, 1)
            }
            if(symptom.nervioMedianoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioRadialSuperficialIzquierdo), false, 1)
            }
            if(symptom.nervioRadialSuperficialIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioCubitalIzquierdo), false, 1)
            }
            if(symptom.nervioCubitalIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioMusculoCutaneoIzquierdo), false, 1)
            }
            if(symptom.nervioMusculoCutaneoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nerviosSupraclavicularesIzquierdos), false, 1)
            }
            if(symptom.nerviosSupraclavicularesIzquierdos!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioFemoralIzquierdo), false, 1)
            }
            if(symptom.nervioFemoralIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioGenitalIzquierdo), false, 1)
            }
            if(symptom.nervioGenitalIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioIlioinguinoIzquierdo), false, 1)
            }
            if(symptom.nervioIlioinguinoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioObturadoIzquierdo), false, 1)
            }
            if(symptom.nervioObturadoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioFemoralAnteriorIzquierdo), false, 1)
            }
            if(symptom.nervioFemoralAnteriorIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioSafenoIzquierdo), false, 1)
            }
            if(symptom.nervioSafenoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioPeroneoIzquierdo), false, 1)
            }
            if(symptom.nervioPeroneoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioSuralIzquierdo), false, 1)
            }
            if(symptom.nervioSuralIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioBraquialIzquierdo), false, 1)
            }
            if(symptom.nervioBraquialIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioAntebrazoIzquierdo), false, 1)
            }
            if(symptom.nervioAntebrazoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioRadialIzquierdo), false, 1)
            }
            if(symptom.nervioRadialIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioAxilarIzquierdo), false, 1)
            }
            if(symptom.nervioAxilarIzquierdo!=0f){
                table.addView(row)
            }
        }
    }


    private fun TableRow.insertCell(text: String, caps: Boolean, gravity: Int){
        val cell = TextView(context).apply{
            this.text = text
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            setTextAppearance(R.style.Small)
            isAllCaps = caps
            setGravity(gravity)
            setPadding(8, 8, 8, 8)
        }
        addView(cell)
    }


}