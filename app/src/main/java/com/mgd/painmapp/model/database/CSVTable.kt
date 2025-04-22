package com.mgd.painmapp.model.database

data class CSVTable (
    val idEvaluation: Long,
    val patient: String,
    val researcher: String,
    val date: String,
    val test: String,
    val idMap: Long,
    val pathsDrawnFront: String,
    val pathsDrawnBack: String,
    val totalPercentage: Float,
    val rightPercentage: Float,
    val leftPercentage: Float,
    val idSymptom: Long,
    val intensity: Float,
    val symptom: String,
    val symptomOtherText: String,
    val charactAgitating: Boolean,
    val charactMiserable: Boolean,
    val charactAnnoying: Boolean,
    val charactUnbearable: Boolean,
    val charactFatiguing: Boolean,
    val charactPiercing: Boolean,
    val charactOther: Boolean,
    val charactOtherText: String,
    val timeContinuous: String,
    val timeWhen: String
)