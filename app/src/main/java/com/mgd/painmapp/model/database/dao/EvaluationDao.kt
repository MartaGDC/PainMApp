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
            "map_table.idMap, pathsDrawnFront, pathsDrawnBack, totalPercentage, rightPercentage, leftPercentage, nervioMedianoDerecho, " +
            "nervioRadialSuperficialDerecho, nervioMusculoCutaneoDerecho, nerviosSupraclavicularesDerechos, nervioFemoralDerecho, " +
            "nervioGenitalDerecho, nervioIlioinguinoDerecho, nervioObturadoDerecho, nervioFemoralAnteriorDerecho, nervioPeroneoDerecho, " +
            "nervioSuralDerecho, nervioBraquialDerecho, nervioAntebrazoDerecho, nervioRadialDerecho, nervioAxilarDerecho, " +
            "nervioMedianoIzquierdo, nervioRadialSuperficialIzquierdo, nervioMusculoCutaneoIzquierdo, nerviosSupraclavicularesIzquierdos, " +
            "nervioFemoralIzquierdo, nervioGenitalIzquierdo, nervioIlioinguinoIzquierdo, nervioObturadoIzquierdo," +
            "nervioFemoralAnteriorIzquierdo, nervioPeroneoIzquierdo, nervioSuralIzquierdo, nervioBraquialIzquierdo, nervioAntebrazoIzquierdo, " +
            "nervioRadialIzquierdo, nervioAxilarIzquierdo, " +
            "idSymptom, intensity, symptom, symptomOtherText, charactAgitating, charactMiserable, charactAnnoying, charactUnbearable, charactFatiguing, " +
            "charactPiercing, charactOther, charactOtherText, timeContinuous, timeWhen " +
            "FROM evaluations_table " +
            "JOIN map_table ON evaluations_table.idEvaluation = map_table.idEvaluation " +
            "JOIN symptoms_table ON map_table.idMap = symptoms_table.idMap")
    suspend fun getFullCSV(): List<CSVTable>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvaluation(evaluation: EvaluationEntity): Long //Para recuperar el id del registro insertado
}