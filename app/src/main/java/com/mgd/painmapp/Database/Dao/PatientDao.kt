package com.mgd.painmapp.Database.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mgd.painmapp.Database.Entities.PatientEntity

@Dao

interface PatientDao {
    @Query("SELECT * FROM patient_table")
    fun getPatients(): List<PatientEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPatient(patient: List<PatientEntity>)
}