package com.mgd.painmapp.Database.Dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mgd.painmapp.Database.Entities.MapEntity
import com.mgd.painmapp.Database.Entities.SymptomEntity

@Dao
interface MapDao {
    @Query("SELECT * FROM map_table")
    fun getMaps(): List<MapEntity>

    @Query("SELECT * FROM map_table WHERE idMap = :idMap")
    fun getMapById(idMap: Long): MapEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMap(map: MapEntity): Long //Para recuperar el id del registro insertado

    @Query("SELECT pathsDrawnFront FROM map_table INNER JOIN symptoms_table ON map_table.idMap = symptoms_table.idMap WHERE map_table.idEvaluation = :idEvaluation")
    fun getFrontPathsDrawnById(idEvaluation: Long): String

    @Query("SELECT pathsDrawnBack FROM map_table INNER JOIN symptoms_table ON map_table.idMap = symptoms_table.idMap WHERE map_table.idEvaluation = :idEvaluation")
    fun getBackPathsDrawnById(idEvaluation: Long): String
}