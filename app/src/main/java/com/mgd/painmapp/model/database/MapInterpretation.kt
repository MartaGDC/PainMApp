package com.mgd.painmapp.model.database

data class MapInterpretation (
    var idEvaluation : Long,
    var pathsDrawnFront : String,
    var pathsDrawnBack : String,
    var totalPatientPercentage : Float?,
    var rightPatientPercentage : Float?,
    var leftPatientPercentage : Float?,
    var totalPercentage : Float,
    var rightPercentage : Float,
    var leftPercentage : Float,
    var nervios : String,
    var dermatomas : String
)