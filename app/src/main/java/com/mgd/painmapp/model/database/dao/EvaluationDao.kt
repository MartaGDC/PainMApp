package com.mgd.painmapp.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mgd.painmapp.model.database.entities.EvaluationEntity

@Dao

interface EvaluationDao {
    @Query("SELECT * FROM evaluations_table")
    suspend fun getEvaluations(): List<EvaluationEntity>

    @Query("SELECT * FROM evaluations_table WHERE idEvaluation = :idEvaluation")
    suspend fun getEvaluationById(idEvaluation: Long): EvaluationEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvaluation(evaluation: EvaluationEntity): Long //Para recuperar el id del registro insertado

    @Query("DELETE FROM evaluations_table WHERE patient = :patient AND test = :test")
    suspend fun deleteEvaluationByPatientAndTest(patient: String, test: String)

    @Query("DELETE FROM evaluations_table WHERE idEvaluation NOT IN " +
            "(SELECT idEvaluation FROM map_table)")
    suspend fun eliminarEvaluacionesSinMapa()
}