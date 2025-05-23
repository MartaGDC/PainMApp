package com.mgd.painmapp.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mgd.painmapp.model.database.SymptomTable
import com.mgd.painmapp.model.database.entities.SymptomEntity

@Dao
interface SymptomDao {
    @Query("SELECT * FROM symptoms_table")
    fun getSymptoms(): List<SymptomEntity>

    @Query("SELECT idSymptom, symptoms_table.idMap, intensity, symptom, symptomOtherText, charactAgitating, " +
            "charactMiserable, charactAnnoying, charactUnbearable, charactFatiguing, " +
            "charactPiercing, charactOther, charactOtherText, time, timeWhen " +
            "FROM symptoms_table " +
            "INNER JOIN map_table ON symptoms_table.idMap = map_table.idMap " +
            "WHERE map_table.idEvaluation = :idEvaluation")
    fun getSymptomsByEvaluation(idEvaluation: Long): List<SymptomEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSymptom(symptom: SymptomEntity): Long //Para recuperar el id del registro insertado

    @Query("SELECT symptom, symptomOtherText, totalPatientPercentage, rightPatientPercentage, leftPatientPercentage, " +
            "totalPercentage, rightPercentage, leftPercentage FROM symptoms_table " +
            "INNER JOIN map_table ON symptoms_table.idMap = map_table.idMap " +
            "WHERE map_table.idEvaluation = :idEvaluation")
    fun getSymptomsTableByEvaluation(idEvaluation: Long): List<SymptomTable>
}



