package com.mgd.painmapp.model.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mgd.painmapp.model.database.MapInterpretation

@Entity(tableName="map_table")
data class MapEntity (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name="idMap") val idMap: Long = 0,
    @ColumnInfo(name="idEvaluation") val idEvaluation:Long,
    @ColumnInfo(name="pathsDrawnFront") val pathsDrawnFront: String,
    @ColumnInfo(name="pathsDrawnBack") val pathsDrawnBack: String,
    @ColumnInfo(name="totalPercentage") val totalPercentage:Float,
    @ColumnInfo(name="rightPercentage") val rightPercentage:Float,
    @ColumnInfo(name="leftPercentage") val leftPercentage:Float
)

fun MapInterpretation.toDatabase() = MapEntity(
    idEvaluation = idEvaluation,
    pathsDrawnFront = pathsDrawnFront,
    pathsDrawnBack = pathsDrawnBack,
    totalPercentage = totalPercentage,
    rightPercentage = rightPercentage,
    leftPercentage = leftPercentage
)
fun MapEntity.toMapInterpretation() = MapInterpretation(
    idEvaluation = idEvaluation,
    pathsDrawnFront = pathsDrawnFront,
    pathsDrawnBack = pathsDrawnBack,
    totalPercentage = totalPercentage,
    rightPercentage = rightPercentage,
    leftPercentage = leftPercentage
)
