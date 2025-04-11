package com.mgd.painmapp

data class MapInterpretation (
    var idEvaluation : Long,
    var pathsDrawnFront : String,
    var pathsDrawnBack : String,
    var totalPercentage : Float,
    var rightPercentage : Float,
    var leftPercentage : Float
)