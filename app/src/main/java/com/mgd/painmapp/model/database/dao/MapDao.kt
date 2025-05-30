package com.mgd.painmapp.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mgd.painmapp.model.database.CSVTable
import com.mgd.painmapp.model.database.NervesTable
import com.mgd.painmapp.model.database.entities.MapEntity

@Dao
interface MapDao {
    @Query("SELECT * FROM map_table WHERE idMap = :idMap")
    suspend fun getMapById(idMap: Long): MapEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMap(map: MapEntity): Long //Para recuperar el id del registro insertado

    @Query("SELECT pathsDrawnFront " +
            "FROM map_table " +
            "INNER JOIN symptoms_table " +
            "ON map_table.idMap = symptoms_table.idMap " +
            "WHERE map_table.idEvaluation = :idEvaluation")
    suspend fun getFrontPathsDrawnById(idEvaluation: Long): List<String>

    @Query("SELECT pathsDrawnBack " +
            "FROM map_table " +
            "INNER JOIN symptoms_table " +
            "ON map_table.idMap = symptoms_table.idMap " +
            "WHERE map_table.idEvaluation = :idEvaluation")
    suspend fun getBackPathsDrawnById(idEvaluation: Long): List<String>

    @Query("SELECT symptoms_table.symptom, symptoms_table.symptomOtherText, " +
            "map_table.* " +
            "FROM map_table " +
            "INNER JOIN symptoms_table ON map_table.idMap = symptoms_table.idMap " +
            "WHERE map_table.idEvaluation = :idEvaluation")
    suspend fun getNervesTableByEvaluation(idEvaluation: Long): List<NervesTable>

    @Query("UPDATE map_table " +
            "SET totalPatientPercentage = :totalPatientPercentage, " +
            "rightPatientPercentage = :rightPatientPercentage, " +
            "leftPatientPercentage = :leftPatientPercentage " +
            "WHERE idEvaluation = :idEvaluation")
    suspend fun updatePatientPercentages(idEvaluation: Long,
                                         totalPatientPercentage: Float,
                                         rightPatientPercentage: Float,
                                         leftPatientPercentage: Float)

    @Query("DELETE FROM map_table WHERE idMap NOT IN " +
            "(SELECT idMap FROM symptoms_table)")
    suspend fun eliminarMapasSinSintomas()


    @Query("SELECT evaluations_table.*, map_table.*, symptoms_table.* " +
            "FROM evaluations_table " +
            "INNER JOIN map_table ON evaluations_table.idEvaluation = " +
            "map_table.idEvaluation "+
            "INNER JOIN symptoms_table ON map_table.idMap = symptoms_table.idMap")
    suspend fun getFullCSV(): List<CSVTable>
}