package com.itsc.tuwoda

import com.yandex.mapkit.geometry.Point

data class RiverData(
    val id:Int,
    val name:String?,
    val width:Double?,
    val depth:Double?,
    val bridge:Double?,
    val distance:Double?,
    val layer:Int?,
    val rgeometry:List<Point>
)
