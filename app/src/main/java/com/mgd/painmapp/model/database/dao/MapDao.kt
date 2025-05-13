package com.mgd.painmapp.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mgd.painmapp.model.database.NervesTable
import com.mgd.painmapp.model.database.SymptomTable
import com.mgd.painmapp.model.database.entities.MapEntity

@Dao
interface MapDao {
    @Query("SELECT * FROM map_table")
    fun getMaps(): List<MapEntity>

    @Query("SELECT * FROM map_table WHERE idMap = :idMap")
    fun getMapById(idMap: Long): MapEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMap(map: MapEntity): Long //Para recuperar el id del registro insertado

    @Query("SELECT pathsDrawnFront FROM map_table INNER JOIN symptoms_table ON map_table.idMap = symptoms_table.idMap WHERE map_table.idEvaluation = :idEvaluation")
    fun getFrontPathsDrawnById(idEvaluation: Long): List<String>

    @Query("SELECT pathsDrawnBack FROM map_table INNER JOIN symptoms_table ON map_table.idMap = symptoms_table.idMap WHERE map_table.idEvaluation = :idEvaluation")
    fun getBackPathsDrawnById(idEvaluation: Long): List<String>

    @Query("SELECT pathsDrawnFront FROM map_table")
    fun getFrontPathsDrawn(): List<String>

    @Query("SELECT pathsDrawnBack FROM map_table")
    fun getBackPathsDrawn(): List<String>

    @Query("SELECT symptoms_table.symptom, symptoms_table.symptomOtherText, nervioMedianoDerecho, nervioRadialSuperficialDerecho, nervioCubitalDerecho, nervioMusculoCutaneoDerecho, nerviosSupraclavicularesDerechos, " +
            "nervioFemoralDerecho, nervioGenitalDerecho, nervioIlioinguinoDerecho, nervioObturadoDerecho, nervioFemoralAnteriorDerecho, nervioSafenoDerecho, nervioPeroneoDerecho, " +
            "nervioSuralDerecho, nervioBraquialDerecho, nervioAntebrazoDerecho, nervioRadialDerecho, nervioAxilarDerecho, nervioMedianoIzquierdo, " +
            "nervioRadialSuperficialIzquierdo, nervioCubitalIzquierdo, nervioMusculoCutaneoIzquierdo,nerviosSupraclavicularesIzquierdos, nervioFemoralIzquierdo, nervioGenitalIzquierdo, " +
            "nervioIlioinguinoIzquierdo, nervioObturadoIzquierdo, nervioFemoralAnteriorIzquierdo, nervioSafenoIzquierdo, nervioPeroneoIzquierdo, nervioSuralIzquierdo, nervioBraquialIzquierdo, " +
            "nervioAntebrazoIzquierdo, nervioRadialIzquierdo, nervioAxilarIzquierdo FROM map_table " +
            "INNER JOIN symptoms_table ON map_table.idMap = symptoms_table.idMap " +
            "WHERE map_table.idEvaluation = :idEvaluation")
    fun getNervesTableByEvaluation(idEvaluation: Long): List<NervesTable>

    @Query("UPDATE map_table " +
            "SET totalPatientPercentage = :totalPatientPercentage, rightPatientPercentage = :rightPatientPercentage, leftPatientPercentage = :leftPatientPercentage " +
            "WHERE idEvaluation = :idEvaluation")
    fun updatePatientPercentages(idEvaluation: Long, totalPatientPercentage: Float, rightPatientPercentage: Float, leftPatientPercentage: Float)

    @Query("DELETE FROM map_table WHERE idMap NOT IN (SELECT idMap FROM symptoms_table)")
    fun eliminarMapasSinSintomas()
}