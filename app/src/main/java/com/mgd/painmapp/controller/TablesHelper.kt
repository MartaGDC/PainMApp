package com.mgd.painmapp.controller

import android.content.Context
import android.os.Environment
import android.view.Gravity
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
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
            val symptomName = symptom.symptomOtherText.ifEmpty {
                symptom.symptom
            }
            valoresFila.add(listOf(symptomName, "")) //nombre + celda vacia
            val valores = listOf(
                symptom.map.nervioMedianoDerecho,
                symptom.map.nervioRadialSuperficialDerecho,
                symptom.map.nervioCubitalDerecho,
                symptom.map.nervioMusculocutaneoDerecho ,
                symptom.map.nerviosSupraclavicularesDerechos,
                symptom.map.nervioFemorocutaneoLatDerecho,
                symptom.map.nervioGenitofemoralDerecho,
                symptom.map.nervioIlioinguinalDerecho,
                symptom.map.nervioIliohipogastricoDerecho,
                symptom.map.nervioObturadoDerecho,
                symptom.map.nervioCutaneofemoralAntDerecho,
                symptom.map.nervioSafenoDerecho,
                symptom.map.nervioPeroneoSuperfDerecho,
                symptom.map.nervioSuralDerecho,
                symptom.map.nervioBraquialDerecho,
                symptom.map.nervioAntebrazoDerecho,
                symptom.map.nervioRadialDerecho,
                symptom.map.nervioAxilarDerecho,
                symptom.map.nerviosCervicalesDerechos,
                symptom.map.nervioTrigeminoIDerecho,
                symptom.map.nervioTrigeminoIIDerecho,
                symptom.map.nervioTrigeminoIIIDerecho,
                symptom.map.T1Derecho,
                symptom.map.T2Derecho,
                symptom.map.T3Derecho,
                symptom.map.T4Derecho,
                symptom.map.T5Derecho,
                symptom.map.T6Derecho,
                symptom.map.T7Derecho,
                symptom.map.T8Derecho,
                symptom.map.T9Derecho,
                symptom.map.T10Derecho,
                symptom.map.T11Derecho,
                symptom.map.T12Derecho,
                symptom.map.nervioMedianoIzquierdo,
                symptom.map.nervioRadialSuperficialIzquierdo,
                symptom.map.nervioCubitalIzquierdo,
                symptom.map.nervioMusculocutaneoIzquierdo,
                symptom.map.nerviosSupraclavicularesIzquierdos,
                symptom.map.nervioFemorocutaneoLatIzquierdo,
                symptom.map.nervioGenitofemoralIzquierdo,
                symptom.map.nervioIlioinguinalIzquierdo,
                symptom.map.nervioIliohipogastricoIzquierdo,
                symptom.map.nervioObturadoIzquierdo,
                symptom.map.nervioCutaneofemoralAntIzquierdo,
                symptom.map.nervioSafenoIzquierdo,
                symptom.map.nervioPeroneoSuperfIzquierdo,
                symptom.map.nervioSuralIzquierdo,
                symptom.map.nervioBraquialIzquierdo,
                symptom.map.nervioAntebrazoIzquierdo,
                symptom.map.nervioRadialIzquierdo,
                symptom.map.nervioAxilarIzquierdo,
                symptom.map.nerviosCervicalesIzquierdo,
                symptom.map.nervioTrigeminoIIzquierdo,
                symptom.map.nervioTrigeminoIIIzquierdo,
                symptom.map.nervioTrigeminoIIIIzquierdo,
                symptom.map.T1Izquierdo,
                symptom.map.T2Izquierdo,
                symptom.map.T3Izquierdo,
                symptom.map.T4Izquierdo,
                symptom.map.T5Izquierdo,
                symptom.map.T6Izquierdo,
                symptom.map.T7Izquierdo,
                symptom.map.T8Izquierdo,
                symptom.map.T9Izquierdo,
                symptom.map.T10Izquierdo,
                symptom.map.T11Izquierdo,
                symptom.map.T12Izquierdo,
                symptom.map.nervioCutAnteroBraqPostDerecho,
                symptom.map.nervioCutAnteroBraqPostIzquierdo,
                symptom.map.nervioCalcaneoDerecho,
                symptom.map.nervioCalcaneoIzquierdo,
                symptom.map.nervioPlantarLateralDerecho,
                symptom.map.nervioPlantarLateralIzquierdo,
                symptom.map.nervioCutFemoralPostDerecho,
                symptom.map.nervioCutFemoralPostIzquierdo,
                symptom.map.nervioCluneosDerecho,
                symptom.map.nervioCluneosIzquierdo,
                symptom.map.L1Derecho,
                symptom.map.L1Izquierdo,
                symptom.map.L2Derecho,
                symptom.map.L2Izquierdo,
                symptom.map.SacrosDerecho,
                symptom.map.SacrosIzquierdo,
                symptom.map.nervioOccipitalMayorDerecho,
                symptom.map.nervioOccipitalMayorIzquierdo,
                symptom.map.nervioOccipitalMenorDerecho,
                symptom.map.nervioOccipitalMenorIzquierdo,
                symptom.map.nervioAuricularMayorDerecho,
                symptom.map.nervioAuricularMayorIzquierdo,
                symptom.map.nervioTransversoDerecho,
                symptom.map.nervioTransversoIzquierdo,
                symptom.map.nervioPlatarMedialDerecho,
                symptom.map.nervioPlatarMedialIzquierdo
            )
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
            val symptomName = symptom.symptomOtherText.ifEmpty {
                symptom.symptom
            }
            valoresFila.add(listOf(symptomName, ""))
            val valores = listOf(
                symptom.map.RC3Derecha,
                symptom.map.RC4Derecha,
                symptom.map.RC5Derecha,
                symptom.map.RC6Derecha,
                symptom.map.RC7Derecha,
                symptom.map.RC8Derecha,
                symptom.map.RT1Derecha,
                symptom.map.RT2Derecha,
                symptom.map.RT3Derecha,
                symptom.map.RT4Derecha,
                symptom.map.RT5Derecha,
                symptom.map.RT6Derecha,
                symptom.map.RT7Derecha,
                symptom.map.RT8Derecha,
                symptom.map.RT9Derecha,
                symptom.map.RT10Derecha,
                symptom.map.RT11Derecha,
                symptom.map.RT12Derecha,
                symptom.map.RL1Derecha,
                symptom.map.RL2Derecha,
                symptom.map.RL3Derecha,
                symptom.map.RL4Derecha,
                symptom.map.RL5Derecha,
                symptom.map.RS1Derecha,
                symptom.map.RS2Derecha,
                symptom.map.RC3Izquierda,
                symptom.map.RC4Izquierda,
                symptom.map.RC5Izquierda,
                symptom.map.RC6Izquierda,
                symptom.map.RC7Izquierda,
                symptom.map.RC8Izquierda,
                symptom.map.RT1Izquierda,
                symptom.map.RT2Izquierda,
                symptom.map.RT3Izquierda,
                symptom.map.RT4Izquierda,
                symptom.map.RT5Izquierda,
                symptom.map.RT6Izquierda,
                symptom.map.RT7Izquierda,
                symptom.map.RT8Izquierda,
                symptom.map.RT9Izquierda,
                symptom.map.RT10Izquierda,
                symptom.map.RT11Izquierda,
                symptom.map.RT12Izquierda,
                symptom.map.RL1Izquierda,
                symptom.map.RL2Izquierda,
                symptom.map.RL3Izquierda,
                symptom.map.RL4Izquierda,
                symptom.map.RL5Izquierda,
                symptom.map.RS1Izquierda,
                symptom.map.RS2Izquierda,
                symptom.map.RC2Derecha,
                symptom.map.RS3Derecha,
                symptom.map.RS4Derecha,
                symptom.map.RS5Derecha,
                symptom.map.RC2Izquierda,
                symptom.map.RS3Izquierda,
                symptom.map.RS4Izquierda,
                symptom.map.RS5Izquierda
            )
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
    
    fun exportCSV(csvTable: List<CSVTable>): File?{
        val dateFormat = SimpleDateFormat("yyyyMMdd_HH.mm.ss", Locale.getDefault())
        val formattedDate = dateFormat.format(Date())
        val fileName = "PainMApp_${formattedDate}.csv"
        val header = listOf(
            "idEvaluation", "patient", "researcher", "date", "test",
            "idMap", "totalPatientPercentage", "rightPatientPercentage", "leftPatientPercentage",
            "totalPercentage", "rightPercentage", "leftPercentage",
            "nervioMedianoDerecho", "nervioRadialSuperficialDerecho", "nervioCubitalDerecho",
            "nervioMusculocutaneoDerecho", "nerviosSupraclavicularesDerechos", "nervioFemorocutaneoLatDerecho",
            "nervioGenitofemoralDerecho", "nervioIlioinguinalDerecho", "nervioIliohipogastricoDerecho",
            "nervioObturadoDerecho", "nervioCutaneofemoralAntDerecho", "nervioSafenoDerecho",
            "nervioPeroneoSuperfDerecho", "nervioSuralDerecho", "nervioBraquialDerecho",
            "nervioAntebrazoDerecho", "nervioRadialDerecho", "nervioAxilarDerecho", "nerviosCervicalesDerechos",
            "nervioTrigeminoIDerecho", "nervioTrigeminoIIDerecho", "nervioTrigeminoIIIDerecho",
            "T1Derecho", "T2Derecho", "T3Derecho", "T4Derecho", "T5Derecho", "T6Derecho", "T7Derecho",
            "T8Derecho", "T9Derecho", "T10Derecho", "T11Derecho", "T12Derecho", "nervioMedianoIzquierdo",
            "nervioRadialSuperficialIzquierdo", "nervioCubitalIzquierdo", "nervioMusculocutaneoIzquierdo",
            "nerviosSupraclavicularesIzquierdos", "nervioFemorocutaneoLatIzquierdo", "nervioGenitofemoralIzquierdo",
            "nervioIlioinguinalIzquierdo", "nervioIliohipogastricoIzquierdo", "nervioObturadoIzquierdo",
            "nervioCutaneofemoralAntIzquierdo", "nervioSafenoIzquierdo", "nervioPeroneoSuperfIzquierdo",
            "nervioSuralIzquierdo", "nervioBraquialIzquierdo", "nervioAntebrazoIzquierdo",
            "nervioRadialIzquierdo", "nervioAxilarIzquierdo", "nerviosCervicalesIzquierdo",
            "nervioTrigeminoIIzquierdo", "nervioTrigeminoIIIzquierdo", "nervioTrigeminoIIIIzquierdo",
            "T1Izquierdo", "T2Izquierdo", "T3Izquierdo", "T4Izquierdo", "T5Izquierdo", "T6Izquierdo",
            "T7Izquierdo", "T8Izquierdo", "T9Izquierdo", "T10Izquierdo", "T11Izquierdo", "T12Izquierdo",

            "nervioCutAnteroBraqPostDerecho", "nervioCutAnteroBraqPostIzquierdo", "nervioCalcaneoDerecho", //solo en mapa posterior
            "nervioCalcaneoIzquierdo", "nervioPlantarLateralDerecho", "nervioPlantarLateralIzquierdo",
            "nervioCutFemoralPostDerecho", "nervioCutFemoralPostIzquierdo", "nervioCluneosDerecho",
            "nervioCluneosIzquierdo", "L1Derecho", "L1Izquierdo", "L2Derecho", "L2Izquierdo", "SacrosDerecho",
            "SacrosIzquierdo", "nervioOccipitalMayorDerecho", "nervioOccipitalMayorIzquierdo",
            "nervioOccipitalMenorDerecho", "nervioOccipitalMenorIzquierdo", "nervioAuricularMayorDerecho",
            "nervioAuricularMayorIzquierdo", "nervioTransversoDerecho", "nervioTransversoIzquierdo",
            "nervioPlatarMedialDerecho", "nervioPlatarMedialIzquierdo",

            "RC3Derecha", "RC4Derecha", "RC5Derecha", "RC6Derecha", "RC7Derecha", "RC8Derecha", "RT1Derecha", //dermatomas
            "RT2Derecha", "RT3Derecha", "RT4Derecha", "RT5Derecha", "RT6Derecha", "RT7Derecha", "RT8Derecha",
            "RT9Derecha", "RT10Derecha", "RT11Derecha", "RT12Derecha", "RL1Derecha", "RL2Derecha", "RL3Derecha",
            "RL4Derecha", "RL5Derecha", "RS1Derecha", "RS2Derecha", "RC3Izquierda", "RC4Izquierda",
            "RC5Izquierda", "RC6Izquierda", "RC7Izquierda", "RC8Izquierda", "RT1Izquierda",
            "RT2Izquierda", "RT3Izquierda", "RT4Izquierda", "RT5Izquierda", "RT6Izquierda", "RT7Izquierda",
            "RT8Izquierda", "RT9Izquierda", "RT10Izquierda", "RT11Izquierda", "RT12Izquierda","RL1Izquierda",
            "RL2Izquierda", "RL3Izquierda", "RL4Izquierda", "RL5Izquierda", "RS1Izquierda", "RS2Izquierda",
            "RC2Derecha", "RS3Derecha", "RS4Derecha", "RS5Derecha", "RC2Izquierda", "RS3Izquierda",  //solo en mapa posterior
            "RS4Izquierda", "RS5Izquierda",
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

                    row.nervioCutAnteroBraqPostDerecho.toString().replace('.', ','), //solo en mapa posterior
                    row.nervioCutAnteroBraqPostIzquierdo.toString().replace('.', ','),
                    row.nervioCalcaneoDerecho.toString().replace('.', ','),
                    row.nervioCalcaneoIzquierdo.toString().replace('.', ','),
                    row.nervioPlantarLateralDerecho.toString().replace('.', ','),
                    row.nervioPlantarLateralIzquierdo.toString().replace('.', ','),
                    row.nervioCutFemoralPostDerecho.toString().replace('.', ','),
                    row.nervioCutFemoralPostIzquierdo.toString().replace('.', ','),
                    row.nervioCluneosDerecho.toString().replace('.', ','),
                    row.nervioCluneosIzquierdo.toString().replace('.', ','),
                    row.L1Derecho.toString().replace('.', ','),
                    row.L1Izquierdo.toString().replace('.', ','),
                    row.L2Derecho.toString().replace('.', ','),
                    row.L2Izquierdo.toString().replace('.', ','),
                    row.SacrosDerecho.toString().replace('.', ','),
                    row.SacrosIzquierdo.toString().replace('.', ','),
                    row.nervioOccipitalMayorDerecho.toString().replace('.', ','),
                    row.nervioOccipitalMayorIzquierdo.toString().replace('.', ','),
                    row.nervioOccipitalMenorDerecho.toString().replace('.', ','),
                    row.nervioOccipitalMenorIzquierdo.toString().replace('.', ','),
                    row.nervioAuricularMayorDerecho.toString().replace('.', ','),
                    row.nervioAuricularMayorIzquierdo.toString().replace('.', ','),
                    row.nervioTransversoDerecho.toString().replace('.', ','),
                    row.nervioTransversoIzquierdo.toString().replace('.', ','),
                    row.nervioPlatarMedialDerecho.toString().replace('.', ','),
                    row.nervioPlatarMedialIzquierdo.toString().replace('.', ','),

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

                    row.RC2Derecha.toString().replace('.', ','), //solo en mapa posterior
                    row.RS3Derecha.toString().replace('.', ','),
                    row.RS4Derecha.toString().replace('.', ','),
                    row.RS5Derecha.toString().replace('.', ','),
                    row.RC2Izquierda.toString().replace('.', ','),
                    row.RS3Izquierda.toString().replace('.', ','),
                    row.RS4Izquierda.toString().replace('.', ','),
                    row.RS5Izquierda.toString().replace('.', ','),

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