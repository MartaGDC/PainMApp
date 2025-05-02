package com.mgd.painmapp.controller.activities

import android.os.Bundle
import android.os.Environment
import android.util.Log
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
    private lateinit var mvFront: MapViews
    private lateinit var mvBack: MapViews
    private var idGeneradoEvaluation: Long = -1
    private lateinit var database: PatientDatabase
    private lateinit var symptomsTable: List<SymptomTable>
    private lateinit var nervesTable: List<NervesTable>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        idGeneradoEvaluation = intent.getLongExtra("idGeneradoEvaluation", -1)

        database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()

        initComponents()
        initListeners()

        CoroutineScope(Dispatchers.IO).launch {
            symptomsTable = database.getSymptomDao().getSymptomsTableByEvaluation(idGeneradoEvaluation)
            nervesTable = database.getMapDao().getNervesTableByEvaluation(idGeneradoEvaluation)
            val evaluationEntity = database.getEvaluationDao().getEvaluationById(idGeneradoEvaluation)
            runOnUiThread{
                NavigationHelper.setupMenu(
                    navView,
                    drawerLayout,
                    this@SummaryActivity,
                    evaluationEntity.patientName,
                    evaluationEntity.researcherName,
                    evaluationEntity.date,
                    idGeneradoEvaluation
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
            Toast.makeText(this,"Descargando CSV...", Toast.LENGTH_SHORT).show()
            CoroutineScope(Dispatchers.IO).launch {
                val dataCSV = database.getEvaluationDao().getFullCSV()
                runOnUiThread {
                    exportCSV(dataCSV)
                }
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

    private fun exportCSV(data: List<CSVTable>):File?{
        val dateFormat = SimpleDateFormat("yyyyMMdd_HH.mm.ss", Locale.getDefault())
        val formattedDate = dateFormat.format(Date())
        val fileName = "PainMApp_${formattedDate}.csv"
        val header = listOf(
            "idEvaluation", "patient", "researcher", "date", "test",
            "idMap", "pathsDrawnFront", "pathsDrawnBack", "totalPercentage", "rightPercentage", "leftPercentage",
            "nervioMedianoDerecho", "nervioRadialSuperficialDerecho", "nervioCubitalDerecho", "nervioMusculoCutaneoDerecho",
            "nerviosSupraclavicularesDerechos", "nervioFemoralDerecho", "nervioGenitalDerecho", "nervioIlioinguinoDerecho",
            "nervioObturadoDerecho", "nervioFemoralAnteriorDerecho", "nervioSafenoDerecho", "nervioPeroneoDerecho", "nervioSuralDerecho",
            "nervioBraquialDerecho", "nervioAntebrazoDerecho", "nervioRadialDerecho", "nervioAxilarDerecho",
            "nervioMedianoIzquierdo", "nervioRadialSuperficialIzquierdo", "nervioCubitalIzquierdo", "nervioMusculoCutaneoIzquierdo",
            "nerviosSupraclavicularesIzquierdos", "nervioFemoralIzquierdo", "nervioGenitalIzquierdo", "nervioIlioinguinoIzquierdo",
            "nervioObturadoIzquierdo", "nervioFemoralAnteriorIzquierdo", "nervioSafenoIzquierdo", "nervioPeroneoIzquierdo", "nervioSuralIzquierdo",
            "nervioBraquialIzquierdo", "nervioAntebrazoIzquierdo", "nervioRadialIzquierdo", "nervioAxilarIzquierdo",
            "idSymptom", "intensity", "symptom", "symptomOtherText", "charactAgitating", "charactMiserable", "charactAnnoying", "charactUnbearable", "charactFatiguing",
            "charactPiercing", "charactOther", "charactOtherText", "timeContinuous", "timeWhen"
        )
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloads, fileName)
        return try {
            val writer = file.bufferedWriter()
            writer.write(header.joinToString(";"))
            writer.newLine()
            for (row in data) {
                val csvRow = listOf(
                    row.idEvaluation,
                    row.patient,
                    row.researcher,
                    row.date,
                    row.test,
                    row.idMap,
                    row.pathsDrawnFront.replace(Regex("M[\\d.,\\s]+L[\\d.,\\s]+L[\\d.,\\s]+L[\\d.,\\s]+Z"), ""),
                    row.pathsDrawnBack.replace(Regex("M[\\d.,\\s]+L[\\d.,\\s]+L[\\d.,\\s]+L[\\d.,\\s]+Z"), ""),
                    row.totalPercentage.toString().replace('.', ','),
                    row.rightPercentage.toString().replace('.', ','),
                    row.leftPercentage.toString().replace('.', ','),
                    row.nervioMedianoDerecho.toString().replace('.', ','),
                    row.nervioRadialSuperficialDerecho.toString().replace('.', ','),
                    row.nervioCubitalDerecho.toString().replace('.', ','),
                    row.nervioMusculoCutaneoDerecho.toString().replace('.', ','),
                    row.nerviosSupraclavicularesDerechos.toString().replace('.', ','),
                    row.nervioFemoralDerecho.toString().replace('.', ','),
                    row.nervioGenitalDerecho.toString().replace('.', ','),
                    row.nervioIlioinguinoDerecho.toString().replace('.', ','),
                    row.nervioObturadoDerecho.toString().replace('.', ','),
                    row.nervioFemoralAnteriorDerecho.toString().replace('.', ','),
                    row.nervioSafenoDerecho.toString().replace('.', ','),
                    row.nervioPeroneoDerecho.toString().replace('.', ','),
                    row.nervioSuralDerecho.toString().replace('.', ','),
                    row.nervioBraquialDerecho.toString().replace('.', ','),
                    row.nervioAntebrazoDerecho.toString().replace('.', ','),
                    row.nervioRadialDerecho.toString().replace('.', ','),
                    row.nervioAxilarDerecho.toString().replace('.', ','),
                    row.nervioMedianoIzquierdo.toString().replace('.', ','),
                    row.nervioRadialSuperficialIzquierdo.toString().replace('.', ','),
                    row.nervioCubitalIzquierdo.toString().replace('.', ','),
                    row.nervioMusculoCutaneoIzquierdo.toString().replace('.', ','),
                    row.nerviosSupraclavicularesIzquierdos.toString().replace('.', ','),
                    row.nervioFemoralIzquierdo.toString().replace('.', ','),
                    row.nervioGenitalIzquierdo.toString().replace('.', ','),
                    row.nervioIlioinguinoIzquierdo.toString().replace('.', ','),
                    row.nervioObturadoIzquierdo.toString().replace('.', ','),
                    row.nervioFemoralAnteriorIzquierdo.toString().replace('.', ','),
                    row.nervioSafenoIzquierdo.toString().replace('.', ','),
                    row.nervioPeroneoIzquierdo.toString().replace('.', ','),
                    row.nervioSuralIzquierdo.toString().replace('.', ','),
                    row.nervioBraquialIzquierdo.toString().replace('.', ','),
                    row.nervioAntebrazoIzquierdo.toString().replace('.', ','),
                    row.nervioRadialIzquierdo.toString().replace('.', ','),
                    row.nervioAxilarIzquierdo.toString().replace('.', ','),
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
            insertCell("",  false, 1)
            insertCell("",  false, 1)
            insertCell("",  false, 1)
        }
        table.addView(rowGeneral)
        var firstSymptom = true
        for (symptom in symptomsTable){
            if (firstSymptom) {
                val row = TableRow(this).apply {
                    insertCell("Por síntomas",  true, 0)
                    insertCell(symptom.symptom,  false, 0)
                    insertCell(String.format("%.2f%%", symptom.totalPercentage), false, 1)
                    insertCell(String.format("%.2f%%", symptom.rightPercentage), false, 1)
                    insertCell(String.format("%.2f%%", symptom.leftPercentage),  false, 1)
                }
                table.addView(row)
                firstSymptom = false
            } else {
                val row = TableRow(this).apply {
                    insertCell("",  true, 0)
                    insertCell(symptom.symptom, false, 0)
                    insertCell(String.format("%.2f%%", symptom.totalPercentage), false, 1)
                    insertCell(String.format("%.2f%%", symptom.rightPercentage), false, 1)
                    insertCell(String.format("%.2f%%", symptom.leftPercentage), false, 1)
                }
                table.addView(row)
            }
        }
    }

    private fun tableNerves(nervesTable: List<NervesTable>) {
        val nervios = InterpretationHelper.obtenerNerviosPerifericosFrente(this)
        val table = binding.tlNerves
        table.removeAllViews()
        for (sintoma in nervesTable){
            var row = TableRow(this).apply {
                insertCell(sintoma.symptom, true, 1)
                insertCell("", false, 0)
            }
            var count = 0
            table.addView(row)
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioMedianoDerecho), false, 1)
            }
            if(sintoma.nervioMedianoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioRadialSuperficialDerecho), false, 1)
            }
            if(sintoma.nervioRadialSuperficialDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioCubitalDerecho), false, 1)
            }
            if(sintoma.nervioCubitalDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioMusculoCutaneoDerecho), false, 1)
            }
            if(sintoma.nervioMusculoCutaneoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nerviosSupraclavicularesDerechos), false, 1)
            }
            if(sintoma.nerviosSupraclavicularesDerechos!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioFemoralDerecho), false, 1)
            }
            if(sintoma.nervioFemoralDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioGenitalDerecho), false, 1)
            }
            if(sintoma.nervioGenitalDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioIlioinguinoDerecho), false, 1)
            }
            if(sintoma.nervioIlioinguinoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioObturadoDerecho), false, 1)
            }
            if(sintoma.nervioObturadoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioFemoralAnteriorDerecho), false, 1)
            }
            if(sintoma.nervioFemoralAnteriorDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioSafenoDerecho), false, 1)
            }
            if(sintoma.nervioSafenoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioPeroneoDerecho), false, 1)
            }
            if(sintoma.nervioPeroneoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioSuralDerecho), false, 1)
            }
            if(sintoma.nervioSuralDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioBraquialDerecho), false, 1)
            }
            if(sintoma.nervioBraquialDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioAntebrazoDerecho), false, 1)
            }
            if(sintoma.nervioAntebrazoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioRadialDerecho), false, 1)
            }
            if(sintoma.nervioRadialDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioAxilarDerecho), false, 1)
            }
            if(sintoma.nervioAxilarDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioMedianoIzquierdo), false, 1)
            }
            if(sintoma.nervioMedianoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioRadialSuperficialIzquierdo), false, 1)
            }
            if(sintoma.nervioRadialSuperficialIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioCubitalIzquierdo), false, 1)
            }
            if(sintoma.nervioCubitalIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioMusculoCutaneoIzquierdo), false, 1)
            }
            if(sintoma.nervioMusculoCutaneoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nerviosSupraclavicularesIzquierdos), false, 1)
            }
            if(sintoma.nerviosSupraclavicularesIzquierdos!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioFemoralIzquierdo), false, 1)
            }
            if(sintoma.nervioFemoralIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioGenitalIzquierdo), false, 1)
            }
            if(sintoma.nervioGenitalIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioIlioinguinoIzquierdo), false, 1)
            }
            if(sintoma.nervioIlioinguinoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioObturadoIzquierdo), false, 1)
            }
            if(sintoma.nervioObturadoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioFemoralAnteriorIzquierdo), false, 1)
            }
            if(sintoma.nervioFemoralAnteriorIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                Log.d("TAG", nervios[count])
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioSafenoIzquierdo), false, 1)
                Log.d("TAG", sintoma.nervioSafenoIzquierdo.toString())
            }
            if(sintoma.nervioSafenoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioPeroneoIzquierdo), false, 1)
            }
            if(sintoma.nervioPeroneoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioSuralIzquierdo), false, 1)
            }
            if(sintoma.nervioSuralIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioBraquialIzquierdo), false, 1)
            }
            if(sintoma.nervioBraquialIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioAntebrazoIzquierdo), false, 1)
            }
            if(sintoma.nervioAntebrazoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count++], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioRadialIzquierdo), false, 1)
            }
            if(sintoma.nervioRadialIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(this).apply {
                insertCell(nervios[count], false, 0)
                insertCell(String.format("%.2f%%", sintoma.nervioAxilarIzquierdo), false, 1)
            }
            if(sintoma.nervioAxilarIzquierdo!=0f){
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