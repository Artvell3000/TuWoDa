package com.itsc.tuwoda

import com.yandex.mapkit.geometry.Point

data class BridgeData(
    val id:Int,
    var name:String?,
    var clearance_height:Double?,
    var bgeometry:List<Point>?
)