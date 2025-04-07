package com.mgd.painmapp.Database.Entities

import android.text.Selection
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mgd.painmapp.Symptom

@Entity(tableName="symptoms_table")
data class SymptomEntity (
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name="idSymptom") val idSymptom: Long = 0,
    @ColumnInfo(name="idEvaluation") val idEvaluation : Long,

    @ColumnInfo(name="intensity") val intensity : Float,

    @ColumnInfo(name="symptomPain") val symptomPain : Boolean,
    @ColumnInfo(name="symptomItch") val symptomItch : Boolean,
    @ColumnInfo(name="symptomBurn") val symptomBurn : Boolean,
    @ColumnInfo(name="symptomSharp") val symptomSharp : Boolean,
    @ColumnInfo(name="symptomNumb") val symptomNumb : Boolean,
    @ColumnInfo(name="symptomCramps") val symptomCramps : Boolean,
    @ColumnInfo(name="symptomSore") val symptomSore : Boolean,
    @ColumnInfo(name="symptomTingling") val symptomTingling : Boolean,
    @ColumnInfo(name="symptomOther") val symptomOther : Boolean,
    @ColumnInfo(name="symptomOtherText") val symptomOtherText : String,

    @ColumnInfo(name="charactAgitating") val charactAgitating : Boolean,
    @ColumnInfo(name="charactMiserable") val charactMiserable : Boolean,
    @ColumnInfo(name="charactAnnoying") val charactAnnoying : Boolean,
    @ColumnInfo(name="charactUnbearable") val charactUnbearable : Boolean,
    @ColumnInfo(name="charactFatiguing") val charactFatiguing : Boolean,
    @ColumnInfo(name="charactPiercing") val charactPiercing : Boolean,
    @ColumnInfo(name="charactOther") val charactOther : Boolean,
    @ColumnInfo(name="charactOtherText") val charactOtherText : String,

    @ColumnInfo(name="timeContinuous") val timeContinuous : Boolean,
    @ColumnInfo(name="timeIntermittent") val timeIntermittent : Boolean,
    @ColumnInfo(name="timeMomentary") val timeMomentary : Boolean,
    @ColumnInfo(name="timeWhen") val timeWhen : String


    )

fun Symptom.toDatabase() = SymptomEntity(
    idEvaluation = idEvaluation,
    intensity = intensity,
    symptomPain = symptomPain,
    symptomItch = symptomItch,
    symptomBurn = symptomBurn,
    symptomSharp = symptomSharp,
    symptomNumb = symptomNumb,
    symptomCramps = symptomCramps,
    symptomSore = symptomSore,
    symptomTingling = symptomTingling,
    symptomOther = symptomOther,
    symptomOtherText = symptomOtherText,
    charactAgitating = charactAgitating,
    charactMiserable = charactMiserable,
    charactAnnoying = charactAnnoying,
    charactUnbearable = charactUnbearable,
    charactFatiguing = charactFatiguing,
    charactPiercing = charactPiercing,
    charactOther = charactOther,
    charactOtherText = charactOtherText,
    timeContinuous = timeContinuous,
    timeIntermittent = timeIntermittent,
    timeMomentary = timeMomentary,
    timeWhen = timeWhen
)
fun SymptomEntity.toSymptom() = Symptom(
    idEvaluation = idEvaluation,
    intensity = intensity,
    symptomPain = symptomPain,
    symptomItch = symptomItch,
    symptomBurn = symptomBurn,
    symptomSharp = symptomSharp,
    symptomNumb = symptomNumb,
    symptomCramps = symptomNumb,
    symptomSore = symptomSore,
    symptomTingling = symptomTingling,
    symptomOther = symptomOther,
    symptomOtherText = symptomOtherText,
    charactAgitating = charactAgitating,
    charactMiserable = charactMiserable,
    charactAnnoying = charactAnnoying,
    charactUnbearable = charactUnbearable,
    charactFatiguing = charactFatiguing,
    charactPiercing = charactPiercing,
    charactOther = charactOther,
    charactOtherText = charactOtherText,
    timeContinuous = timeContinuous,
    timeIntermittent = timeIntermittent,
    timeMomentary = timeMomentary,
    timeWhen = timeWhen)
