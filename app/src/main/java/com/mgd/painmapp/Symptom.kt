package com.mgd.painmapp

data class Symptom (
    var idEvaluation : Long,
    var intensity : Float,
    var symptomPain : Boolean,
    var symptomItch : Boolean,
    var symptomBurn : Boolean,
    var symptomSharp : Boolean,
    var symptomNumb : Boolean,
    var symptomCramps : Boolean,
    var symptomSore : Boolean,
    var symptomTingling : Boolean,
    var symptomOther : Boolean,
    var symptomOtherText : String,
    var charactAgitating : Boolean,
    var charactMiserable : Boolean,
    var charactAnnoying : Boolean,
    var charactUnbearable : Boolean,
    var charactFatiguing : Boolean,
    var charactPiercing : Boolean,
    var charactOther : Boolean,
    var charactOtherText : String,
    var timeContinuous : Boolean,
    var timeIntermittent : Boolean,
    var timeMomentary : Boolean,
    var timeWhen : String
)