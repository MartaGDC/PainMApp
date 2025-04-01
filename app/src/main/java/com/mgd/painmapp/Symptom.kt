package com.mgd.painmapp

import android.text.Selection
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

data class Symptom (
    var idEvaluation : Long,
    var intensity : Float,
    var symptomType : String,
    var interpretation : String,
    var time : String
)