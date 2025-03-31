package com.mgd.painmapp.Database.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mgd.painmapp.Evaluation
import java.util.Date

@Entity(tableName="evaluations_table")
data class EvaluationEntity (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name="idEvaluation") val idEvaluation: Int = 0,
    @ColumnInfo(name="patient") val patientName : String,
    @ColumnInfo(name="researcher") val researcherName : String,
    @ColumnInfo(name="date") val date : String,
    @ColumnInfo(name="test") val test : String
    )

fun Evaluation.toDatabase() = EvaluationEntity(patientName = name, researcherName = researcher, date = "", test = "")
//fun EvaluationEntity.toEvaluation() = Evaluation(name = patientName, researcher = researcherName, date = TODO(), test = TODO() )
