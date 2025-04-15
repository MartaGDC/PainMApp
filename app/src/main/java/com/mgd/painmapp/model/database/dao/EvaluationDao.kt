package com.mgd.painmapp.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mgd.painmapp.model.database.entities.EvaluationEntity

@Dao

interface EvaluationDao {
    @Query("SELECT * FROM evaluations_table")
    fun getEvaluations(): List<EvaluationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvaluation(evaluation: EvaluationEntity): Long //Para recuperar el id del registro insertado
}