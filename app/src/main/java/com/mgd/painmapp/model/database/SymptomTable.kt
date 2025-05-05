package com.mgd.painmapp.model.database

data class SymptomTable (
    val symptom: String,
    val totalPatientPercentage:Float,
    val rightPatientPercentage:Float,
    val leftPatientPercentage:Float,
    val totalPercentage: Float,
    val rightPercentage: Float,
    val leftPercentage: Float
)