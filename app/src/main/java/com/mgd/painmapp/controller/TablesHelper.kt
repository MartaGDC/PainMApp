package com.mgd.painmapp.controller

import android.content.Context
import android.os.Environment
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
    fun tableSymptoms(symptomsTable: List<SymptomTable>, tlPercentages: TableLayout, context: Context){
        tlPercentages.removeAllViews() //Comenzar con tabla limpia
        val titles = TableRow(context).apply{ // para añadir las celdas con las caracteristicas correctas, creamos una función extensión para TableRow (celda para la fila)
            insertCell("",  false, 1)
            insertCell("",  true,0 )
            insertCell("% de área corporal", false, 1)
            insertCell("% derecho",  false, 1)
            insertCell("% izquierdo",  false, 1)
        }
        tlPercentages.addView(titles)
        val rowGeneral = TableRow(context).apply { //No es la suma de los porcentajes, ya que pueden solaparse
            insertCell("General",  true,0 )
            insertCell("",  true,0 )
            insertCell(String.format(Locale.getDefault(),"%.1f%%", symptomsTable[0].totalPatientPercentage), false, 1)
            insertCell(String.format(Locale.getDefault(),"%.1f%%", symptomsTable[0].rightPatientPercentage),  false, 1)
            insertCell(String.format(Locale.getDefault(),"%.1f%%", symptomsTable[0].leftPatientPercentage),  false, 1)
        }
        tlPercentages.addView(rowGeneral)
        var firstSymptom = true
        for (symptom in symptomsTable){
            if (firstSymptom) {
                val row = TableRow(context).apply {
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
                tlPercentages.addView(row)
                firstSymptom = false
            } else {
                val row = TableRow(context).apply {
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
                tlPercentages.addView(row)
            }
        }
    }

    fun tableNerves(nervesTable: List<NervesTable>, table: TableLayout, context:Context) {
        val nerves = InterpretationHelper.getPeripheralNerves(context)
        table.removeAllViews()
        for (symptom in nervesTable){
            var row = TableRow(context).apply {
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
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioMedianoDerecho), false, 1)
            }
            if(symptom.map.nervioMedianoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioRadialSuperficialDerecho), false, 1)
            }
            if(symptom.map.nervioRadialSuperficialDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioCubitalDerecho), false, 1)
            }
            if(symptom.map.nervioCubitalDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioMusculocutaneoDerecho), false, 1)
            }
            if(symptom.map.nervioMusculocutaneoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nerviosSupraclavicularesDerechos), false, 1)
            }
            if(symptom.map.nerviosSupraclavicularesDerechos!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioFemorocutaneoLatDerecho), false, 1)
            }
            if(symptom.map.nervioFemorocutaneoLatDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioGenitofemoralDerecho), false, 1)
            }
            if(symptom.map.nervioGenitofemoralDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioIlioinguinalDerecho), false, 1)
            }
            if(symptom.map.nervioIlioinguinalDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioIliohipogastricoDerecho), false, 1)
            }
            if(symptom.map.nervioIliohipogastricoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioObturadoDerecho), false, 1)
            }
            if(symptom.map.nervioObturadoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioCutaneofemoralAntDerecho), false, 1)
            }
            if(symptom.map.nervioCutaneofemoralAntDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioSafenoDerecho), false, 1)
            }
            if(symptom.map.nervioSafenoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioPeroneoSuperfDerecho), false, 1)
            }
            if(symptom.map.nervioPeroneoSuperfDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioSuralDerecho), false, 1)
            }
            if(symptom.map.nervioSuralDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioBraquialDerecho), false, 1)
            }
            if(symptom.map.nervioBraquialDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioAntebrazoDerecho), false, 1)
            }
            if(symptom.map.nervioAntebrazoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioRadialDerecho), false, 1)
            }
            if(symptom.map.nervioRadialDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioAxilarDerecho), false, 1)
            }
            if(symptom.map.nervioAxilarDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nerviosCervicalesDerechos), false, 1)
            }
            if(symptom.map.nerviosCervicalesDerechos!=0f){
                table.addView(row)

            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioTrigeminoIDerecho), false, 1)
            }
            if(symptom.map.nervioTrigeminoIDerecho!=0f){
                table.addView(row)

            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioTrigeminoIIDerecho), false, 1)
            }
            if(symptom.map.nervioTrigeminoIIDerecho!=0f){
                table.addView(row)

            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioTrigeminoIIIDerecho), false, 1)
            }
            if(symptom.map.nervioTrigeminoIIIDerecho!=0f){
                table.addView(row)

            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T1Derecho), false, 1)
            }
            if(symptom.map.T1Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T2Derecho), false, 1)
            }
            if(symptom.map.T2Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T3Derecho), false, 1)
            }
            if(symptom.map.T3Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T4Derecho), false, 1)
            }
            if(symptom.map.T4Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T5Derecho), false, 1)
            }
            if(symptom.map.T5Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T6Derecho), false, 1)
            }
            if(symptom.map.T6Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T7Derecho), false, 1)
            }
            if(symptom.map.T7Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T8Derecho), false, 1)
            }
            if(symptom.map.T8Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T9Derecho), false, 1)
            }
            if(symptom.map.T9Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T10Derecho), false, 1)
            }
            if(symptom.map.T10Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T11Derecho), false, 1)
            }
            if(symptom.map.T11Derecho!=0f){
                table.addView(row)

            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T12Derecho), false, 1)
            }
            if(symptom.map.T12Derecho!=0f){
                table.addView(row)
            }

            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioMedianoIzquierdo), false, 1)
            }
            if(symptom.map.nervioMedianoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioRadialSuperficialIzquierdo), false, 1)
            }
            if(symptom.map.nervioRadialSuperficialIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioCubitalIzquierdo), false, 1)
            }
            if(symptom.map.nervioCubitalIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioMusculocutaneoIzquierdo), false, 1)
            }
            if(symptom.map.nervioMusculocutaneoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nerviosSupraclavicularesIzquierdos), false, 1)
            }
            if(symptom.map.nerviosSupraclavicularesIzquierdos!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioFemorocutaneoLatIzquierdo), false, 1)
            }
            if(symptom.map.nervioFemorocutaneoLatIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioGenitofemoralIzquierdo), false, 1)
            }
            if(symptom.map.nervioGenitofemoralIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioIlioinguinalIzquierdo), false, 1)
            }
            if(symptom.map.nervioIlioinguinalIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioIliohipogastricoIzquierdo), false, 1)
            }
            if(symptom.map.nervioIliohipogastricoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioObturadoIzquierdo), false, 1)
            }
            if(symptom.map.nervioObturadoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioCutaneofemoralAntIzquierdo), false, 1)
            }
            if(symptom.map.nervioCutaneofemoralAntIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioSafenoIzquierdo), false, 1)
            }
            if(symptom.map.nervioSafenoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioPeroneoSuperfIzquierdo), false, 1)
            }
            if(symptom.map.nervioPeroneoSuperfIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioSuralIzquierdo), false, 1)
            }
            if(symptom.map.nervioSuralIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioBraquialIzquierdo), false, 1)
            }
            if(symptom.map.nervioBraquialIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioAntebrazoIzquierdo), false, 1)
            }
            if(symptom.map.nervioAntebrazoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioRadialIzquierdo), false, 1)
            }
            if(symptom.map.nervioRadialIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioAxilarIzquierdo), false, 1)
            }
            if(symptom.map.nervioAxilarIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nerviosCervicalesIzquierdo), false, 1)
            }
            if(symptom.map.nerviosCervicalesIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioTrigeminoIIzquierdo), false, 1)
            }
            if(symptom.map.nervioTrigeminoIIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioTrigeminoIIIzquierdo), false, 1)
            }
            if(symptom.map.nervioTrigeminoIIIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioTrigeminoIIIIzquierdo), false, 1)
            }
            if(symptom.map.nervioTrigeminoIIIIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T1Izquierdo), false, 1)
            }
            if(symptom.map.T1Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T2Izquierdo), false, 1)
            }
            if(symptom.map.T2Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T3Izquierdo), false, 1)
            }
            if(symptom.map.T3Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T4Izquierdo), false, 1)
            }
            if(symptom.map.T4Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T5Izquierdo), false, 1)
            }
            if(symptom.map.T5Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T6Izquierdo), false, 1)
            }
            if(symptom.map.T6Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T7Izquierdo), false, 1)
            }
            if(symptom.map.T7Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T8Izquierdo), false, 1)
            }
            if(symptom.map.T8Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T9Izquierdo), false, 1)
            }
            if(symptom.map.T9Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T10Izquierdo), false, 1)
            }
            if(symptom.map.T10Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T11Izquierdo), false, 1)
            }
            if(symptom.map.T11Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.T12Izquierdo), false, 1)
            }
            if(symptom.map.T12Izquierdo!=0f){
                table.addView(row)
            }

            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioCutAnteroBraqPostDerecho), false, 1)
            }
            if(symptom.map.nervioCutAnteroBraqPostDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioCutAnteroBraqPostIzquierdo), false, 1)
            }
            if(symptom.map.nervioCutAnteroBraqPostIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioCalcaneoDerecho), false, 1)
            }
            if(symptom.map.nervioCalcaneoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioCalcaneoIzquierdo), false, 1)
            }
            if(symptom.map.nervioCalcaneoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioPlantarLateralDerecho), false, 1)
            }
            if(symptom.map.nervioPlantarLateralDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioPlantarLateralIzquierdo), false, 1)
            }
            if(symptom.map.nervioPlantarLateralIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioCutFemoralPostDerecho), false, 1)
            }
            if(symptom.map.nervioCutFemoralPostDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioCutFemoralPostIzquierdo), false, 1)
            }
            if(symptom.map.nervioCutFemoralPostIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioCluneosDerecho), false, 1)
            }
            if(symptom.map.nervioCluneosDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioCluneosIzquierdo), false, 1)
            }
            if(symptom.map.nervioCluneosIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.L1Derecho), false, 1)
            }
            if(symptom.map.L1Derecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.L1Izquierdo), false, 1)
            }
            if(symptom.map.L1Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.L2Derecho), false, 1)
            }
            if(symptom.map.L2Derecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.L2Izquierdo), false, 1)
            }
            if(symptom.map.L2Izquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.SacrosDerecho), false, 1)
            }
            if(symptom.map.SacrosDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.SacrosIzquierdo), false, 1)
            }
            if(symptom.map.SacrosIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioOccipitalMayorDerecho), false, 1)
            }
            if(symptom.map.nervioOccipitalMayorDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioOccipitalMayorIzquierdo), false, 1)
            }
            if(symptom.map.nervioOccipitalMayorIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioOccipitalMenorDerecho), false, 1)
            }
            if(symptom.map.nervioOccipitalMenorDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioOccipitalMenorIzquierdo), false, 1)
            }
            if(symptom.map.nervioOccipitalMenorIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioAuricularMayorDerecho), false, 1)
            }
            if(symptom.map.nervioAuricularMayorDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioAuricularMayorIzquierdo), false, 1)
            }
            if(symptom.map.nervioAuricularMayorIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioTransversoDerecho), false, 1)
            }
            if(symptom.map.nervioTransversoDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioTransversoIzquierdo), false, 1)
            }
            if(symptom.map.nervioTransversoIzquierdo!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioPlatarMedialDerecho), false, 1)
            }
            if(symptom.map.nervioPlatarMedialDerecho!=0f){
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(nerves[count++], false, 0)
                insertCell(String.format(Locale.getDefault(),"%.1f%%", symptom.map.nervioPlatarMedialIzquierdo), false, 1)
            }
            if(symptom.map.nervioPlatarMedialIzquierdo!=0f){
                table.addView(row)
            }
        }
    }

    fun tableDermatomes(nervesTable: List<NervesTable>, table:TableLayout, context:Context) {
        val dermatomes = InterpretationHelper.getDermatomes(context)
        table.removeAllViews()
        for (symptom in nervesTable) {
            var row = TableRow(context).apply {
                if (symptom.symptomOtherText.isNotEmpty()) {
                    insertCell(symptom.symptomOtherText, true, 1)
                } else {
                    insertCell(symptom.symptom, true, 1)
                }
                insertCell("", false, 0)
            }
            var count = 0
            table.addView(row)

            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RC3Derecha),false, 1)
            }
            if (symptom.map.RC3Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RC4Derecha), false, 1)
            }
            if (symptom.map.RC4Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RC5Derecha), false, 1)
            }
            if (symptom.map.RC5Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RC6Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RC6Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RC7Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RC7Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RC8Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RC8Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT1Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RT1Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT2Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RT2Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT3Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RT3Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT4Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RT4Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT5Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RT5Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT6Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RT6Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT7Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RT7Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT8Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RT8Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT9Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RT9Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT10Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RT10Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT11Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RT11Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT12Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RT12Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RL1Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RL1Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RL2Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RL2Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RL3Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RL3Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RL4Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RL4Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RL5Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RL5Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RS1Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RS1Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RS2Derecha),
                    false,
                    1
                )
            }
            if (symptom.map.RS2Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RC3Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RC3Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RC4Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RC4Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RC5Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RC5Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RC6Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RC6Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RC7Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RC7Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RC8Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RC8Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT1Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RT1Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT2Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RT2Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT3Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RT3Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT4Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RT4Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT5Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RT5Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT6Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RT6Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT7Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RT7Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT8Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RT8Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT9Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RT9Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT10Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RT10Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT11Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RT11Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(
                    String.format(Locale.getDefault(), "%.1f%%", symptom.map.RT12Izquierda),
                    false,
                    1
                )
            }
            if (symptom.map.RT12Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RL1Izquierda), false, 1)
            }
            if (symptom.map.RL1Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RL2Izquierda), false, 1)
            }
            if (symptom.map.RL2Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RL3Izquierda), false, 1)
            }
            if (symptom.map.RL3Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RL4Izquierda), false, 1)
            }
            if (symptom.map.RL4Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RL5Izquierda), false, 1)
            }
            if (symptom.map.RL5Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RS1Izquierda), false, 1)
            }
            if (symptom.map.RS1Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RS2Izquierda), false, 1)
            }
            if (symptom.map.RS2Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RC2Derecha), false, 1)
            }
            if (symptom.map.RC2Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RS3Derecha), false, 1)
            }
            if (symptom.map.RS3Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RS4Derecha), false, 1)
            }
            if (symptom.map.RS4Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RS5Derecha), false, 1)
            }
            if (symptom.map.RS5Derecha != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RC2Izquierda), false, 1)
            }
            if (symptom.map.RC2Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RS3Izquierda), false, 1)
            }
            if (symptom.map.RS3Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count++], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RS4Izquierda), false, 1)
            }
            if (symptom.map.RS4Izquierda != 0f) {
                table.addView(row)
            }
            row = TableRow(context).apply {
                insertCell(dermatomes[count], false, 0)
                insertCell(String.format(Locale.getDefault(), "%.1f%%", symptom.map.RS5Izquierda), false, 1)
            }
            if (symptom.map.RS5Izquierda != 0f) {
                table.addView(row)
            }
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