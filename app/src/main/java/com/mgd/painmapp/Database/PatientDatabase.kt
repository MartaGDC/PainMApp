package com.mgd.painmapp.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mgd.painmapp.Database.Dao.PatientDao
import com.mgd.painmapp.Database.Entities.PatientEntity

@Database(entities = [PatientEntity::class], version = 1)
abstract class PatientDatabase : RoomDatabase() {
    abstract fun getPatientDao(): PatientDao
}