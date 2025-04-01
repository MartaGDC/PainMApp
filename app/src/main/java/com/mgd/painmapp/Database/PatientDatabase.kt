package com.mgd.painmapp.Database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mgd.painmapp.Database.Dao.EvaluationDao
import com.mgd.painmapp.Database.Dao.SymptomDao
import com.mgd.painmapp.Database.Entities.EvaluationEntity
import com.mgd.painmapp.Database.Entities.SymptomEntity

@Database(entities = [EvaluationEntity::class, SymptomEntity::class], version = 1)
abstract class PatientDatabase : RoomDatabase() {
    abstract fun getEvaluationDao(): EvaluationDao
    abstract fun getSymptomDao(): SymptomDao

}