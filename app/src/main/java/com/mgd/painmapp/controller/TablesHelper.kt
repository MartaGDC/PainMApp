package com.mgd.painmapp.controller

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.os.Environment
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.mgd.painmapp.R
import com.mgd.painmapp.model.database.CSVTable
import com.mgd.painmapp.model.database.NervesTable
import com.mgd.painmapp.model.database.SymptomTable
import com.mgd.painmapp.model.storage.ColorBrush.colorList
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TablesHelper {
    fun getSymptomsTable(symptomsTable: List<SymptomTable>): List<MutableList<String>> {
        val valoresFila = mutableListOf<MutableList<String>>()
        valoresFila.add(mutableListOf("", "", "% de área corporal", "% derecho", "% izquierdo"))
        val general = symptomsTable.first()
        valoresFila.add(
            mutableListOf(
                "General",
                "",
                String.format(Locale.getDefault(), "%.1f%%", general.totalPatientPercentage),
                String.format(Locale.getDefault(), "%.1f%%", general.rightPatientPercentage),
                String.format(Locale.getDefault(), "%.1f%%", general.leftPatientPercentage)
            )
        )
        var firstSymptom = true
        var symptomName: String
        for (symptom in symptomsTable) {
            if (symptom.symptomOtherText.isNotEmpty()){
                symptomName = symptom.symptomOtherText +"_:_"
            }
            else {
                symptomName = symptom.symptom + "_:_"
            }
            val valores = mutableListOf<String>()
            if(firstSymptom) {
                valores.add("Por síntomas")
            }
            else{
                valores.add("")
            }
            valores.add(symptomName)
            valores.add(String.format(Locale.getDefault(), "%.1f%%", symptom.totalPercentage))
            valores.add(String.format(Locale.getDefault(), "%.1f%%", symptom.rightPercentage))
            valores.add(String.format(Locale.getDefault(), "%.1f%%", symptom.leftPercentage))
            firstSymptom = false
            valoresFila.add(valores)
        }
        return valoresFila
    }

    fun createSymptomsTable(filas: List<MutableList<String>>, table:TableLayout, context:Context) {
        table.removeAllViews()
        var index = 0
        for (fila in filas) {
            val row = TableRow(context)
            for (x in fila.indices) {
                val caps = fila[x] == "General" || fila[x] == "Por síntomas"
                val gravity = when (x) {
                    0 -> Gravity.START
                    else -> Gravity.CENTER
                }
                if(fila[x].contains("_:_")){
                    var color = colorList[index++]
                    fila[x] = fila[x].removeSuffix("_:_")
                    row.insertCell(fila[x], caps, gravity, color)
                }
                else{
                    row.insertCell(fila[x], caps, gravity)
                }
            }
            table.addView(row)
        }
    }

    fun prepareTableNerves(nervesTable: List<NervesTable>, context:Context): List<MutableList<String>> {
        val nerves = InterpretationHelper.getPeripheralNerves(context)
        val valoresFila = mutableListOf<MutableList<String>>()
        var symptomName: String
        for (symptom in nervesTable){
            if (symptom.symptomOtherText.isNotEmpty()){
                symptomName = symptom.symptomOtherText +"_:_"
            }
            else {
                symptomName = symptom.symptom + "_:_"
            }
            valoresFila.add(mutableListOf(symptomName, "")) //nombre + celda vacia
            val typeToken = object : TypeToken<Map<String, Float>>() {}.type
            val mapNervios = Gson().fromJson<Map<String, Float>>(symptom.map.nervios, typeToken)
            val valores = mapNervios.values.toList()
            for(x in valores.indices step 1){
                if (valores[x] != 0f) {
                    val name = nerves[x]
                    valoresFila.add(mutableListOf(name, String.format(Locale.getDefault(), "%.1f%%", valores[x])))
                }
            }
        }
        return valoresFila
    }

    fun prepareTableDermatomes(nervesTable: List<NervesTable>, context:Context): List<MutableList<String>> {
        val dermatomes = InterpretationHelper.getDermatomes(context)
        val valoresFila = mutableListOf<MutableList<String>>()
        var symptomName: String
        for (symptom in nervesTable){
            if (symptom.symptomOtherText.isNotEmpty()){
                symptomName = symptom.symptomOtherText +"_:_"
            }
            else {
                symptomName = symptom.symptom + "_:_"
            }
            valoresFila.add(mutableListOf(symptomName, ""))
            val typeToken = object : TypeToken<Map<String, Float>>() {}.type
            val mapDermatomas = Gson().fromJson<Map<String, Float>>(symptom.map.dermatomas, typeToken)
            val valores = mapDermatomas.values.toList()
            for(x in valores.indices step 1){
                if (valores[x] != 0f) {
                    val name = dermatomes[x]
                    valoresFila.add(mutableListOf(name, String.format(Locale.getDefault(), "%.1f%%", valores[x])))
                }
            }
        }
        return valoresFila
    }

    fun createTables(filas: List<MutableList<String>>, table:TableLayout, context:Context) {
        var index = 0
        table.removeAllViews()
        for (fila in filas) {
            val row = TableRow(context)
            for (x in fila.indices) {
                var gravity = if (x == 0) {
                    Gravity.START
                } else {
                    Gravity.CENTER
                }
                if (fila[x].contains("_:_")) {
                    val color = colorList[index++]
                    fila[x] = fila[x].removeSuffix("_:_")
                    gravity = Gravity.CENTER
                    row.insertCell(fila[x], true, gravity, color)
                }
                else{
                    row.insertCell(fila[x], false, gravity)
                }
            }
            table.addView(row)
        }
    }

    private fun TableRow.insertCell(userText: String, caps: Boolean, gravity: Int, color:Int?=null) {
        val cell = TextView(context).apply {
            text = userText
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            setTextAppearance(R.style.Small_table)
            isAllCaps = caps
            setGravity(gravity)
            setPadding(8, 8, 8, 8)
            if (color != null) {
                background = GradientDrawable().apply {
                    shape = GradientDrawable.RECTANGLE
                    setStroke(5, color)
                    cornerRadius = 8f
                }
            }
        }
        addView(cell)
    }

    fun exportCSV(csvTable: List<CSVTable>, context: Context): File?{
        val dateFormat = SimpleDateFormat("yyyyMMdd_HH.mm.ss", Locale.getDefault())
        val formattedDate = dateFormat.format(Date())
        val fileName = "PainMApp_${formattedDate}.csv"
        val nerviosNames = InterpretationHelper.getPeripheralNerves(context)
        val dermatomasNames = InterpretationHelper.getDermatomes(context)

        val header = listOf(
            "idEvaluation", "patient", "researcher", "date", "test",
            "idMap", "totalPatientPercentage", "rightPatientPercentage", "leftPatientPercentage",
            "totalPercentage", "rightPercentage", "leftPercentage") +
                nerviosNames + dermatomasNames + listOf(
            "idSymptom", "intensity", "symptom", "symptomOtherText", "charactAgitating", "charactMiserable", "charactAnnoying", "charactUnbearable", "charactFatiguing",
            "charactPiercing", "charactOther", "charactOtherText", "time", "timeWhen"
        )
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloads, fileName)
        return try {
            val writer = file.bufferedWriter()
            writer.write(header.joinToString(";"))
            writer.newLine()
            for (row in csvTable) {
                val typeToken = object : TypeToken<Map<String, Float>>() {}.type
                val mapNervios = Gson().fromJson<Map<String, Float>>(row.map.nervios, typeToken)
                val nerviosValues = nerviosNames.map { nerveName ->
                    mapNervios[nerveName].toString().replace('.', ',')
                }
                val mapDermatomas = Gson().fromJson<Map<String, Float>>(row.map.dermatomas, typeToken)
                val dermatomasValues = dermatomasNames.map { dermatomeName ->
                    mapDermatomas[dermatomeName].toString().replace('.', ',')
                }
                val csvRow = (listOf(
                    row.idEvaluation,
                    row.evaluation.name,
                    row.evaluation.researcher,
                    row.evaluation.date,
                    row.evaluation.test,
                    row.idMap,
                    row.map.totalPatientPercentage.toString().replace('.', ','),
                    row.map.rightPatientPercentage.toString().replace('.', ','),
                    row.map.leftPatientPercentage.toString().replace('.', ','),
                    row.map.totalPercentage.toString().replace('.', ','),
                    row.map.rightPercentage.toString().replace('.', ','),
                    row.map.leftPercentage.toString().replace('.', ',')) +
                        nerviosValues +
                        dermatomasValues +
                        listOf(
                    row.idSymptom,
                    row.symptom.intensity.toString().replace('.', ','),
                    row.symptom.symptom,
                    row.symptom.symptomOtherText,
                    row.symptom.charactAgitating,
                    row.symptom.charactMiserable,
                    row.symptom.charactAnnoying,
                    row.symptom.charactUnbearable,
                    row.symptom.charactFatiguing,
                    row.symptom.charactPiercing,
                    row.symptom.charactOther,
                    row.symptom.charactOtherText,
                    row.symptom.time,
                    row.symptom.timeWhen)
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