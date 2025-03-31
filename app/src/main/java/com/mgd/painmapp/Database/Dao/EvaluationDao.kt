package com.mgd.painmapp.Database.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mgd.painmapp.Database.Entities.EvaluationEntity

@Dao

interface EvaluationDao {
    @Query("SELECT * FROM evaluations_table")
    fun getEvaluations(): List<EvaluationEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvaluation(evaluation: EvaluationEntity): Int //Para recuperar el id del registro insertado
}