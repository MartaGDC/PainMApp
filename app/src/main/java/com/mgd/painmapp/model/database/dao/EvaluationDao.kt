package com.mgd.painmapp.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mgd.painmapp.model.database.CSVTable
import com.mgd.painmapp.model.database.entities.EvaluationEntity

@Dao

interface EvaluationDao {
    @Query("SELECT * FROM evaluations_table")
    fun getEvaluations(): List<EvaluationEntity>

    @Query("SELECT * FROM evaluations_table WHERE idEvaluation = :idEvaluation")
    fun getEvaluationById(idEvaluation: Long): EvaluationEntity

    @Query("SELECT evaluations_table.idEvaluation, patient, researcher, date, test, " +
            "map_table.idMap, totalPatientPercentage, rightPatientPercentage, leftPatientPercentage, " +
            "totalPercentage, rightPercentage, leftPercentage, " +
            "nervioMedianoDerecho, nervioRadialSuperficialDerecho, nervioCubitalDerecho, " +
            "nervioMusculocutaneoDerecho, nerviosSupraclavicularesDerechos, nervioFemorocutaneoLatDerecho, " +
            "nervioGenitofemoralDerecho, nervioIlioinguinalDerecho, nervioIliohipogastricoDerecho, " +
            "nervioObturadoDerecho, nervioCutaneofemoralAntDerecho, nervioSafenoDerecho, " +
            "nervioPeroneoSuperfDerecho, nervioSuralDerecho, nervioBraquialDerecho, " +
            "nervioAntebrazoDerecho, nervioRadialDerecho, nervioAxilarDerecho, nerviosCervicalesDerechos, " +
            "nervioTrigeminoIDerecho, nervioTrigeminoIIDerecho, nervioTrigeminoIIIDerecho, " +
            "T1Derecho, T2Derecho, T3Derecho, T4Derecho, T5Derecho, T6Derecho, T7Derecho, T8Derecho, " +
            "T9Derecho, T10Derecho, T11Derecho, T12Derecho, " +
            "nervioMedianoIzquierdo, nervioRadialSuperficialIzquierdo, nervioCubitalIzquierdo, " +
            "nervioMusculocutaneoIzquierdo, nerviosSupraclavicularesIzquierdos, nervioFemorocutaneoLatIzquierdo, " +
            "nervioGenitofemoralIzquierdo, nervioIlioinguinalIzquierdo, nervioIliohipogastricoIzquierdo, " +
            "nervioObturadoIzquierdo, nervioCutaneofemoralAntIzquierdo, nervioSafenoIzquierdo, " +
            "nervioPeroneoSuperfIzquierdo, nervioSuralIzquierdo, nervioBraquialIzquierdo, " +
            "nervioAntebrazoIzquierdo, nervioRadialIzquierdo, nervioAxilarIzquierdo, nerviosCervicalesIzquierdo, " +
            "nervioTrigeminoIIzquierdo, nervioTrigeminoIIIzquierdo, nervioTrigeminoIIIIzquierdo, " +
            "T1Izquierdo, T2Izquierdo, T3Izquierdo, T4Izquierdo, T5Izquierdo, T6Izquierdo, T7Izquierdo, " +
            "T8Izquierdo, T9Izquierdo, T10Izquierdo, T11Izquierdo, T12Izquierdo, " +
            "nervioCutAnteroBraqPostDerecho, nervioCutAnteroBraqPostIzquierdo, nervioCalcaneoDerecho, " +
            "nervioCalcaneoIzquierdo, nervioPlantarLateralDerecho, nervioPlantarLateralIzquierdo, " +
            "nervioCutFemoralPostDerecho, nervioCutFemoralPostIzquierdo, nervioCluneosDerecho, " +
            "nervioCluneosIzquierdo, L1Derecho, L1Izquierdo, L2Derecho, L2Izquierdo, SacrosDerecho, " +
            "SacrosIzquierdo, nervioOccipitalMayorDerecho, nervioOccipitalMayorIzquierdo, " +
            "nervioOccipitalMenorDerecho, nervioOccipitalMenorIzquierdo, nervioAuricularMayorDerecho, " +
            "nervioAuricularMayorIzquierdo, nervioTransversoDerecho, nervioTransversoIzquierdo, " +
            "nervioPlatarMedialDerecho, nervioPlatarMedialIzquierdo, " +
            "RC3Derecha, RC4Derecha, RC5Derecha, RC6Derecha, RC7Derecha, RC8Derecha, RT1Derecha, " +
            "RT2Derecha, RT3Derecha, RT4Derecha, RT5Derecha, RT6Derecha, RT7Derecha, RT8Derecha, " +
            "RT9Derecha, RT10Derecha, RT11Derecha, RT12Derecha, RL1Derecha, RL2Derecha, RL3Derecha, " +
            "RL4Derecha, RL5Derecha, RS1Derecha, RS2Derecha," +
            "RC3Izquierda, RC4Izquierda, RC5Izquierda, RC6Izquierda, RC7Izquierda, RC8Izquierda, RT1Izquierda, " +
            "RT2Izquierda, RT3Izquierda, RT4Izquierda, RT5Izquierda, RT6Izquierda, RT7Izquierda, RT8Izquierda, " +
            "RT9Izquierda, RT10Izquierda, RT11Izquierda, RT12Izquierda, RL1Izquierda, RL2Izquierda, RL3Izquierda, " +
            "RL4Izquierda, RL5Izquierda, RS1Izquierda, RS2Izquierda, " +
            "idSymptom, intensity, symptom, symptomOtherText, charactAgitating, charactMiserable, charactAnnoying, charactUnbearable, charactFatiguing, " +
            "charactPiercing, charactOther, charactOtherText, timeContinuous, timeWhen " +
            "FROM evaluations_table " +
            "JOIN map_table ON evaluations_table.idEvaluation = map_table.idEvaluation " +
            "JOIN symptoms_table ON map_table.idMap = symptoms_table.idMap")
    fun getFullCSV(): List<CSVTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertEvaluation(evaluation: EvaluationEntity): Long //Para recuperar el id del registro insertado

    @Query("DELETE FROM evaluations_table WHERE patient = :patient AND test = :test")
    fun deleteEvaluationByPatientAndTest(patient: String, test: String)

    @Query("DELETE FROM evaluations_table WHERE idEvaluation NOT IN (SELECT idEvaluation FROM map_table)")
    fun eliminarEvaluacionesSinMapa()
}