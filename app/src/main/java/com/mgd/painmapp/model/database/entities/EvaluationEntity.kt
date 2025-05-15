package com.mgd.painmapp.model.database.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mgd.painmapp.model.database.Evaluation

@Entity(tableName="evaluations_table")
data class EvaluationEntity (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name="idEvaluation") val idEvaluation: Long = 0,
    @ColumnInfo(name="patient") val patientName : String,
    @ColumnInfo(name="researcher") val researcherName : String,
    @ColumnInfo(name="date") val date : String,
    @ColumnInfo(name="test") val test : String
)

fun Evaluation.toDatabase() = EvaluationEntity(patientName = name, researcherName = researcher, date = date, test = test)
//fun EvaluationEntity.toEvaluation() = Evaluation(idEvaluation = idEvaluation, name = patientName, researcher = researcherName, date = date, test = test )
