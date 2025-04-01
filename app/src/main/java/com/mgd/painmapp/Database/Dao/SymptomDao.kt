package com.mgd.painmapp.Database.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mgd.painmapp.Database.Entities.SymptomEntity

@Dao
interface SymptomDao {
    @Query("SELECT * FROM symptoms_table")
    fun getSymptoms(): List<SymptomEntity>

    @Query("SELECT * FROM symptoms_table WHERE idEvaluation = :idEvaluation")
    fun getSymptomsByEvaluation(idEvaluation: Long): List<SymptomEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSymptom(symptom: SymptomEntity): Long //Para recuperar el id del registro insertado


}



