package com.mgd.painmapp.model.database

import androidx.room.Embedded

data class NervesTable (
    val symptom: String, //para mostrar la afectación nerviosa por síntoma
    val symptomOtherText: String,
    @Embedded val map: MapInterpretation
)