package com.mgd.painmapp.model.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mgd.painmapp.model.database.NervesTable
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

    @Query("SELECT symptoms_table.symptom, symptoms_table.symptomOtherText, " +
            "nervioMedianoDerecho, nervioRadialSuperficialDerecho, nervioCubitalDerecho, " +
            "nervioMusculocutaneoDerecho, nerviosSupraclavicularesDerechos, nervioFemorocutaneoLatDerecho, " +
            "nervioGenitofemoralDerecho, nervioIlioinguinalDerecho, nervioIliohipogastricoDerecho, " +
            "nervioObturadoDerecho, nervioCutaneofemoralAntDerecho, nervioSafenoDerecho, " +
            "nervioPeroneoSuperfDerecho, nervioSuralDerecho, nervioBraquialDerecho, " +
            "nervioAntebrazoDerecho, nervioRadialDerecho, nervioAxilarDerecho, nerviosCervicalesDerechos, " +
            "nervioTrigeminoIDerecho, nervioTrigeminoIIDerecho, nervioTrigeminoIIIDerecho, " +
            "T1Derecho, T2Derecho, T3Derecho, T4Derecho, T5Derecho, T6Derecho, T7Derecho, T8Derecho, " +
            "T9Derecho, T10Derecho, T11Derecho, T12Derecho, " +
            "nervioMedianoIzquierdo, nervioRadialSuperficialIzquierdo, nervioCubitalIzquierdo, " +
            "nervioMusculocutaneoIzquierdo, nerviosSupraclavicularesIzquierdos, nervioFemorocutaneoLatIzquierdo, " +
            "nervioGenitofemoralIzquierdo, nervioIlioinguinalIzquierdo, nervioIliohipogastricoIzquierdo, " +
            "nervioObturadoIzquierdo, nervioCutaneofemoralAntIzquierdo, nervioSafenoIzquierdo, " +
            "nervioPeroneoSuperfIzquierdo, nervioSuralIzquierdo, nervioBraquialIzquierdo, " +
            "nervioAntebrazoIzquierdo, nervioRadialIzquierdo, nervioAxilarIzquierdo, nerviosCervicalesIzquierdo, " +
            "nervioTrigeminoIIzquierdo, nervioTrigeminoIIIzquierdo, nervioTrigeminoIIIIzquierdo, " +
            "T1Izquierdo, T2Izquierdo, T3Izquierdo, T4Izquierdo, T5Izquierdo, T6Izquierdo, T7Izquierdo, " +
            "T8Izquierdo, T9Izquierdo, T10Izquierdo, T11Izquierdo, T12Izquierdo, " +
            "RC3Derecha, RC4Derecha, RC5Derecha, RC6Derecha, RC7Derecha, RC8Derecha, RT1Derecha, RT2Derecha, " +
            "RT3Derecha, RT4Derecha, RT5Derecha, RT6Derecha, RT7Derecha, RT8Derecha, RT9Derecha, RT10Derecha, " +
            "RT11Derecha, RT12Derecha, RL1Derecha, RL2Derecha, RL3Derecha, RL4Derecha, RL5Derecha, RS1Derecha, " +
            "RS2Derecha, " +
            "RC3Izquierda, RC4Izquierda, RC5Izquierda, RC6Izquierda, RC7Izquierda, RC8Izquierda, RT1Izquierda, " +
            "RT2Izquierda, RT3Izquierda, RT4Izquierda, RT5Izquierda, RT6Izquierda, RT7Izquierda, RT8Izquierda, " +
            "RT9Izquierda, RT10Izquierda, RT11Izquierda, RT12Izquierda, RL1Izquierda, RL2Izquierda, RL3Izquierda, " +
            "RL4Izquierda, RL5Izquierda, RS1Izquierda, RS2Izquierda " +
            "FROM map_table " +
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