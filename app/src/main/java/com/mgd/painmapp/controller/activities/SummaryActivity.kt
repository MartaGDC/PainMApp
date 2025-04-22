package com.mgd.painmapp.controller.activities

import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.drawerlayout.widget.DrawerLayout
import androidx.room.Room
import com.google.android.material.navigation.NavigationView
import com.mgd.painmapp.R
import com.mgd.painmapp.controller.NavigationHelper
import com.mgd.painmapp.databinding.ActivitySummaryBinding
import com.mgd.painmapp.model.database.CSVTable
import com.mgd.painmapp.model.database.PatientDatabase
import com.mgd.painmapp.model.database.SymptomTable
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
    private var idGeneradoEvaluation: Long = -1
    private lateinit var database: PatientDatabase
    private lateinit var symptomsTable: List<SymptomTable>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initComponents()
        initListeners()
        idGeneradoEvaluation = intent.getLongExtra("idGeneradoEvaluation", -1)

        database = Room.databaseBuilder(
            this, PatientDatabase::class.java,
            "patient_database"
        ).build()
        CoroutineScope(Dispatchers.IO).launch {
            symptomsTable = database.getSymptomDao().getSymptomsTableByEvaluation(idGeneradoEvaluation)
            val list = database.getEvaluationDao().getEvaluationById(idGeneradoEvaluation)
            runOnUiThread{
                NavigationHelper.setupMenu(
                    navView,
                    drawerLayout,
                    this@SummaryActivity,
                    list.patientName,
                    list.researcherName,
                    list.date,
                    idGeneradoEvaluation
                )
                tableSymptoms(symptomsTable)
            }
        }
    }

    private fun initComponents(){
        drawerLayout = binding.main
        navView = binding.navView
        cvCSV = binding.cvCSV
    }

    private fun initListeners(){
        cvCSV.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val dataCSV = database.getEvaluationDao().getFullCSV()
                runOnUiThread {
                    exportCSV(dataCSV)
                }
            }
        }
    }

    private fun exportCSV(data: List<CSVTable>):File?{
        val dateFormat = SimpleDateFormat("yyyyMMdd_HH.mm.ss", Locale.getDefault())
        val formattedDate = dateFormat.format(Date())
        val fileName = "PainMApp_${formattedDate}.csv"
        val header = listOf(
            "idEvaluation", "patient", "researcher", "date", "test",
            "idMap", "pathsDrawnFront", "pathsDrawnBack", "totalPercentage", "rightPercentage", "leftPercentage",
            "idSymptom", "intensity", "symptom", "symptomOtherText",
            "charactAgitating", "charactMiserable", "charactAnnoying", "charactUnbearable",
            "charactFatiguing", "charactPiercing", "charactOther", "charactOtherText",
            "timeContinuous", "timeWhen"
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
                    row.pathsDrawnFront,
                    row.pathsDrawnBack,
                    row.totalPercentage.toString().replace('.', ','),
                    row.rightPercentage.toString().replace('.', ','),
                    row.leftPercentage.toString().replace('.', ','),
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

    private fun TableRow.insertCell(text: String, caps: Boolean, gravity: Int){
        val cell = TextView(context).apply{
            this.text = text
            layoutParams = TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            setTextAppearance(R.style.Small)
            isAllCaps = caps
            setGravity(gravity)
            setPadding(8, 8, 8, 8)
        }
        addView(cell)
    }


}