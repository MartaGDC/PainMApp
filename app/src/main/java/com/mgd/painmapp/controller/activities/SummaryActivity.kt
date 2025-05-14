package com.mgd.painmapp.controller.activities

import android.os.Bundle
import android.view.View
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.google.android.material.navigation.NavigationView
import com.mgd.painmapp.R
import com.mgd.painmapp.controller.InterpretationHelper
import com.mgd.painmapp.controller.NavigationHelper
import com.mgd.painmapp.databinding.ActivitySummaryBinding
import com.mgd.painmapp.model.database.NervesTable
import com.mgd.painmapp.model.database.PatientDatabase
import com.mgd.painmapp.model.database.SymptomTable
import com.mgd.painmapp.model.database.entities.EvaluationEntity
import com.mgd.painmapp.view.MapViews
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
                tableDermatomes(nervesTable)
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
                    if (symptom.symptomOtherText.isNotEmpty()) {
                        insertCell(symptom.symptomOtherText, false, 0)
                    }
                    else{
                        insertCell(symptom.symptom, false, 0)
                    }
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
                    insertCell(symptom.symptomOtherText, true,1)
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
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioMusculocutaneoDerecho), false, 1)
            }
            if(symptom.nervioMusculocutaneoDerecho!=0f){
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
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioFemorocutaneoLatDerecho), false, 1)
            }
            if(symptom.nervioFemorocutaneoLatDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioGenitofemoralDerecho), false, 1)
            }
            if(symptom.nervioGenitofemoralDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioIlioinguinalDerecho), false, 1)
            }
            if(symptom.nervioIlioinguinalDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioIliohipogastricoDerecho), false, 1)
            }
            if(symptom.nervioIliohipogastricoDerecho!=0f){
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
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioCutaneofemoralAntDerecho), false, 1)
            }
            if(symptom.nervioCutaneofemoralAntDerecho!=0f){
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
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioPeroneoSuperfDerecho), false, 1)
            }
            if(symptom.nervioPeroneoSuperfDerecho!=0f){
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
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nerviosCervicalesDerechos), false, 1)
            }
            if(symptom.nerviosCervicalesDerechos!=0f){
                table.addView(row)

            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioTrigeminoIDerecho), false, 1)
            }
            if(symptom.nervioTrigeminoIDerecho!=0f){
                table.addView(row)

            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioTrigeminoIIDerecho), false, 1)
            }
            if(symptom.nervioTrigeminoIIDerecho!=0f){
                table.addView(row)

            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioTrigeminoIIIDerecho), false, 1)
            }
            if(symptom.nervioTrigeminoIIIDerecho!=0f){
                table.addView(row)

            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T1Derecho), false, 1)
            }
            if(symptom.T1Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T2Derecho), false, 1)
            }
            if(symptom.T2Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T3Derecho), false, 1)
            }
            if(symptom.T3Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T4Derecho), false, 1)
            }
            if(symptom.T4Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T5Derecho), false, 1)
            }
            if(symptom.T5Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T6Derecho), false, 1)
            }
            if(symptom.T6Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T7Derecho), false, 1)
            }
            if(symptom.T7Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T8Derecho), false, 1)
            }
            if(symptom.T8Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T9Derecho), false, 1)
            }
            if(symptom.T9Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T10Derecho), false, 1)
            }
            if(symptom.T10Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T11Derecho), false, 1)
            }
            if(symptom.T11Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T12Derecho), false, 1)
            }
            if(symptom.T12Derecho!=0f){
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
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioMusculocutaneoIzquierdo), false, 1)
            }
            if(symptom.nervioMusculocutaneoIzquierdo!=0f){
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
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioFemorocutaneoLatIzquierdo), false, 1)
            }
            if(symptom.nervioFemorocutaneoLatIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioGenitofemoralIzquierdo), false, 1)
            }
            if(symptom.nervioGenitofemoralIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioIlioinguinalIzquierdo), false, 1)
            }
            if(symptom.nervioIlioinguinalIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioIliohipogastricoIzquierdo), false, 1)
            }
            if(symptom.nervioIliohipogastricoIzquierdo!=0f){
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
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioCutaneofemoralAntIzquierdo), false, 1)
            }
            if(symptom.nervioCutaneofemoralAntIzquierdo!=0f){
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
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioPeroneoSuperfIzquierdo), false, 1)
            }
            if(symptom.nervioPeroneoSuperfIzquierdo!=0f){
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
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioAxilarIzquierdo), false, 1)
            }
            if(symptom.nervioAxilarIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nerviosCervicalesIzquierdo), false, 1)
            }
            if(symptom.nerviosCervicalesIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioTrigeminoIIzquierdo), false, 1)
            }
            if(symptom.nervioTrigeminoIIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioTrigeminoIIIzquierdo), false, 1)
            }
            if(symptom.nervioTrigeminoIIIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.nervioTrigeminoIIIIzquierdo), false, 1)
            }
            if(symptom.nervioTrigeminoIIIIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T1Izquierdo), false, 1)
            }
            if(symptom.T1Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T2Izquierdo), false, 1)
            }
            if(symptom.T2Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T3Izquierdo), false, 1)
            }
            if(symptom.T3Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T4Izquierdo), false, 1)
            }
            if(symptom.T4Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T5Izquierdo), false, 1)
            }
            if(symptom.T5Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T6Izquierdo), false, 1)
            }
            if(symptom.T6Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T7Izquierdo), false, 1)
            }
            if(symptom.T7Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T8Izquierdo), false, 1)
            }
            if(symptom.T8Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T9Izquierdo), false, 1)
            }
            if(symptom.T9Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T10Izquierdo), false, 1)
            }
            if(symptom.T10Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T11Izquierdo), false, 1)
            }
            if(symptom.T11Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nerves[count], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.T12Izquierdo), false, 1)
            }
            if(symptom.T12Izquierdo!=0f){
                table.addView(row)
            }
        }
    }

    private fun tableDermatomes(nervesTable: List<NervesTable>) {
        val dermatomes = InterpretationHelper.getFrontDermatomes(this)
        val table = binding.tlDermatomes
        table.removeAllViews()
        for (symptom in nervesTable) {
            var row = TableRow(this).apply {
                if (symptom.symptomOtherText.isNotEmpty()) {
                    insertCell(symptom.symptomOtherText, true, 1)
                } else {
                    insertCell(symptom.symptom, true, 1)
                }
                insertCell("", false, 0)
            }
            var count = 0
            table.addView(row)

            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.RC3Derecha),false, 1)
            }
            if (symptom.RC3Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.RC4Derecha), false, 1)
            }
            if (symptom.RC4Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.RC5Derecha), false, 1)
            }
            if (symptom.RC5Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RC6Derecha),
                    false,
                    1
                )
            }
            if (symptom.RC6Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RC7Derecha),
                    false,
                    1
                )
            }
            if (symptom.RC7Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RC8Derecha),
                    false,
                    1
                )
            }
            if (symptom.RC8Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT1Derecha),
                    false,
                    1
                )
            }
            if (symptom.RT1Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT2Derecha),
                    false,
                    1
                )
            }
            if (symptom.RT2Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT3Derecha),
                    false,
                    1
                )
            }
            if (symptom.RT3Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT4Derecha),
                    false,
                    1
                )
            }
            if (symptom.RT4Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT5Derecha),
                    false,
                    1
                )
            }
            if (symptom.RT5Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT6Derecha),
                    false,
                    1
                )
            }
            if (symptom.RT6Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT7Derecha),
                    false,
                    1
                )
            }
            if (symptom.RT7Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT8Derecha),
                    false,
                    1
                )
            }
            if (symptom.RT8Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT9Derecha),
                    false,
                    1
                )
            }
            if (symptom.RT9Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT10Derecha),
                    false,
                    1
                )
            }
            if (symptom.RT10Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT11Derecha),
                    false,
                    1
                )
            }
            if (symptom.RT11Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT12Derecha),
                    false,
                    1
                )
            }
            if (symptom.RT12Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RL1Derecha),
                    false,
                    1
                )
            }
            if (symptom.RL1Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RL2Derecha),
                    false,
                    1
                )
            }
            if (symptom.RL2Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RL3Derecha),
                    false,
                    1
                )
            }
            if (symptom.RL3Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RL4Derecha),
                    false,
                    1
                )
            }
            if (symptom.RL4Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RL5Derecha),
                    false,
                    1
                )
            }
            if (symptom.RL5Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RS1Derecha),
                    false,
                    1
                )
            }
            if (symptom.RS1Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RS2Derecha),
                    false,
                    1
                )
            }
            if (symptom.RS2Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RC3Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RC3Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RC4Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RC4Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RC5Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RC5Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RC6Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RC6Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RC7Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RC7Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RC8Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RC8Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT1Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RT1Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT2Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RT2Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT3Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RT3Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT4Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RT4Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT5Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RT5Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT6Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RT6Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT7Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RT7Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT8Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RT8Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT9Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RT9Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT10Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RT10Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT11Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RT11Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.RT12Izquierda),
                    false,
                    1
                )
            }
            if (symptom.RT12Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.RL1Izquierda), false, 1)
            }
            if (symptom.RL1Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.RL2Izquierda), false, 1)
            }
            if (symptom.RL2Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.RL3Izquierda), false, 1)
            }
            if (symptom.RL3Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.RL4Izquierda), false, 1)
            }
            if (symptom.RL4Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.RL5Izquierda), false, 1)
            }
            if (symptom.RL5Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.RS1Izquierda), false, 1)
            }
            if (symptom.RS1Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(dermatomes[count], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.RS2Izquierda), false, 1)
            }
            if (symptom.RS2Izquierda != 0f) {
                table.addView(row)
            }
        }
    }

    private fun TableRow.insertCell(text: String, caps: Boolean, gravity: Int){
        val cell = TextView(context).apply{
            this.text = text
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            setTextAppearance(R.style.Small_table)
            isAllCaps = caps
            setGravity(gravity)
            setPadding(8, 8, 8, 8)
        }
        addView(cell)
    }


}