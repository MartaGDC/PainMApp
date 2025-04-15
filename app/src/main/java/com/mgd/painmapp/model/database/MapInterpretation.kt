package com.mgd.painmapp.model.database

data class MapInterpretation (
    var idEvaluation : Long,
    var pathsDrawnFront : String,
    var pathsDrawnBack : String,
    var totalPercentage : Float,
    var rightPercentage : Float,
    var leftPercentage : Float
)