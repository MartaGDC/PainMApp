package com.mgd.painmapp.Database.Entities

import android.text.Selection
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mgd.painmapp.Symptom

@Entity(tableName="symptoms_table")
data class SymptomEntity (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name="idSymptom") val idSymptom: Long = 0,
    @ColumnInfo(name="idEvaluation") val idEvaluation : Long,
    @ColumnInfo(name="intensity") val intensity : Float,
    @ColumnInfo(name="symptomType") val symptomType : String,
    @ColumnInfo(name="interpretation") val interpretation : String,
    @ColumnInfo(name="time") val time : String
)

fun Symptom.toDatabase() = SymptomEntity(idEvaluation = idEvaluation, intensity = intensity, symptomType = symptomType, interpretation = interpretation, time = time)
fun SymptomEntity.toSymptom() = Symptom(idEvaluation = idEvaluation, intensity = intensity, symptomType = symptomType, interpretation = interpretation, time = time)
