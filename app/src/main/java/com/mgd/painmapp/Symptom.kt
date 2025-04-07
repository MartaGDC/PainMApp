package com.mgd.painmapp

data class Symptom (
    var idEvaluation : Long,
    var intensity : Float,
    var symptom : Int,
    var symptomOtherText : String,
    var charactAgitating : Boolean,
    var charactMiserable : Boolean,
    var charactAnnoying : Boolean,
    var charactUnbearable : Boolean,
    var charactFatiguing : Boolean,
    var charactPiercing : Boolean,
    var charactOther : Boolean,
    var charactOtherText : String,
    var time : Int,
    var timeWhen : String
)