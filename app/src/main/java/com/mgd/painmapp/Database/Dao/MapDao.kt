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
}