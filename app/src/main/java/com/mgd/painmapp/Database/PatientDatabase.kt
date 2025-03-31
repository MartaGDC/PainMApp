package com.mgd.painmapp.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mgd.painmapp.Database.Dao.EvaluationDao
import com.mgd.painmapp.Database.Entities.EvaluationEntity

@Database(entities = [EvaluationEntity::class], version = 1)
abstract class PatientDatabase : RoomDatabase() {
    abstract fun getEvaluationDao(): EvaluationDao
}