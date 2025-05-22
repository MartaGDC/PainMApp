package com.mgd.painmapp.model.database

import androidx.room.Embedded

data class CSVTable (
    val idEvaluation: Long,
    @Embedded val evaluation: Evaluation,
    val idMap: Long,
    @Embedded val map: MapInterpretation,
    val idSymptom: Long,
    @Embedded val symptom: Symptom
)