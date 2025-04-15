package com.mgd.painmapp.model.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mgd.painmapp.model.database.dao.EvaluationDao
import com.mgd.painmapp.model.database.dao.MapDao
import com.mgd.painmapp.model.database.dao.SymptomDao
import com.mgd.painmapp.model.database.entities.EvaluationEntity
import com.mgd.painmapp.model.database.entities.MapEntity
import com.mgd.painmapp.model.database.entities.SymptomEntity

@Database(entities = [EvaluationEntity::class, SymptomEntity::class, MapEntity::class], version = 1)
abstract class PatientDatabase : RoomDatabase() {
    abstract fun getEvaluationDao(): EvaluationDao
    abstract fun getSymptomDao(): SymptomDao
    abstract fun getMapDao(): MapDao

}