package com.mgd.painmapp.Database.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mgd.painmapp.Patient

@Entity(tableName="patient_table")
data class PatientEntity (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name="id") val id: Int = 0,
    @ColumnInfo(name="name") val name : String
)

fun Patient.toDatabase() = PatientEntity( name = name)
fun PatientEntity.toStudent() = Patient(name)
