package com.mgd.painmapp.controller

import android.content.Context
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
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TablesHelper {
    fun getSymptomsTable(symptomsTable: List<SymptomTable>): List<List<String>> {
        val valoresFila = mutableListOf<List<String>>()
        valoresFila.add(listOf("", "", "% de área corporal", "% derecho", "% izquierdo"))
        val general = symptomsTable.first()
        valoresFila.add(
            listOf(
                "General",
                "",
                String.format(Locale.getDefault(), "%.1f%%", general.totalPatientPercentage),
                String.format(Locale.getDefault(), "%.1f%%", general.rightPatientPercentage),
                String.format(Locale.getDefault(), "%.1f%%", general.leftPatientPercentage)
            )
        )
        var firstSymptom = true
        for (symptom in symptomsTable) {
            val symptomName = symptom.symptomOtherText.ifEmpty {
                symptom.symptom
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

    fun createSymptomsTable(filas: List<List<String>>, table:TableLayout, context:Context) {
        table.removeAllViews()
        for (fila in filas) {
            val row = TableRow(context)
            for (x in fila.indices) {
                val caps = fila[x] == "General" || fila[x] == "Por síntomas"
                val gravity = when (x) {
                    0 -> Gravity.START
                    else -> Gravity.CENTER
                }
                row.insertCell(fila[x], caps, gravity)
            }
            table.addView(row)
        }
    }

    fun prepareTableNerves(nervesTable: List<NervesTable>, context:Context): List<List<String>> {
        val nerves = InterpretationHelper.getPeripheralNerves(context)
        val valoresFila = mutableListOf<List<String>>()
        for (symptom in nervesTable){
            val symptomName = if(symptom.symptomOtherText.isEmpty()){
                symptom.symptom.uppercase()
            } else{
                symptom.symptomOtherText.uppercase()
            }
            valoresFila.add(listOf(symptomName, "")) //nombre + celda vacia
            val typeToken = object : TypeToken<Map<String, Float>>() {}.type
            val mapNervios = Gson().fromJson<Map<String, Float>>(symptom.map.nervios, typeToken)
            val valores = mapNervios.values.toList()
            for(x in valores.indices step 1){
                if (valores[x] != 0f) {
                    val name = nerves[x]
                    valoresFila.add(listOf(name, String.format(Locale.getDefault(), "%.1f%%", valores[x])))
                }
            }
        }
        return valoresFila
    }

    fun prepareTableDermatomes(nervesTable: List<NervesTable>, context:Context): List<List<String>> {
        val dermatomes = InterpretationHelper.getDermatomes(context)
        val valoresFila = mutableListOf<List<String>>()
        for (symptom in nervesTable){
            val symptomName = if(symptom.symptomOtherText.isEmpty()){
                symptom.symptom.uppercase()
            } else{
                symptom.symptomOtherText.uppercase()
            }
            valoresFila.add(listOf(symptomName, ""))
            val typeToken = object : TypeToken<Map<String, Float>>() {}.type
            val mapDermatomas = Gson().fromJson<Map<String, Float>>(symptom.map.dermatomas, typeToken)
            val valores = mapDermatomas.values.toList()
            for(x in valores.indices step 1){
                if (valores[x] != 0f) {
                    val name = dermatomes[x]
                    valoresFila.add(listOf(name, String.format(Locale.getDefault(), "%.1f%%", valores[x])))
                }
            }
        }
        return valoresFila
    }

    fun createTables(filas: List<List<String>>, table:TableLayout, context:Context) {
        table.removeAllViews()
        for (fila in filas) {
            val row = TableRow(context)
            for (x in fila.indices) {
                val cell = TextView(context).apply {
                    text = fila[x]
                    setTextAppearance(R.style.Small_table)
                    setPadding(8, 8, 8, 8)
                    gravity = if (x == 0) {
                        Gravity.START
                    } else {
                        Gravity.CENTER
                    }
                }
                row.addView(cell)
            }
            table.addView(row)
        }
    }



    private fun TableRow.insertCell(userText: String, caps: Boolean, gravity: Int){
        val cell = TextView(context).apply{
            text = userText
            layoutParams = TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1f)
            setTextAppearance(R.style.Small_table)
            isAllCaps = caps
            setGravity(gravity)
            setPadding(8, 8, 8, 8)
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
            "charactPiercing", "charactOther", "charactOtherText", "timeContinuous", "timeWhen"
        )
        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val file = File(downloads, fileName)
        return try {
            val writer = file.bufferedWriter()
            writer.write(header.joinToString(";"))
            writer.newLine()
            for (row in csvTable) {
                val typeToken = object : TypeToken<Map<String, Float>>() {}.type
                val mapNervios = Gson().fromJson<Map<String, Float>>(row.nervios, typeToken)
                val nerviosValues = nerviosNames.map { nerveName ->
                    mapNervios[nerveName].toString().replace('.', ',')
                }
                val mapDermatomas = Gson().fromJson<Map<String, Float>>(row.nervios, typeToken)
                val dermatomasValues = dermatomasNames.map { dermatomeName ->
                    mapDermatomas[dermatomeName].toString().replace('.', ',')
                }
                val csvRow = (listOf(
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
                    row.leftPercentage.toString().replace('.', ',')) +
                        nerviosValues + dermatomasValues + listOf(
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
                    row.timeWhen)
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