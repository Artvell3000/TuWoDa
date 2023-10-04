package com.itsc.tuwoda

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.yandex.mapkit.geometry.Point

class MyViewModel:ViewModel() {
    var stateMap by mutableStateOf(true)
    var stateRoutes by mutableStateOf(false)
    var stateMe by mutableStateOf(false)
    var stateSailing by mutableStateOf(false)

    var stateRouting by mutableStateOf(false)
    var stateMapDialog by mutableStateOf(false)

    fun updateStateMap() {
        stateMap = true
        stateMe = false
        stateRoutes = false
        stateSailing = false
    }
    fun updateStateRoutes() {
        stateRoutes = true
        stateMe = false
        stateMap = false
        stateSailing = false
    }
    fun updateStateMe() {
        stateMe = true
        stateMap = false
        stateRoutes = false
        stateSailing = false
    }
    fun updateStateSailing() {
        stateSailing = true
        stateMe = false
        stateMap = false
        stateRoutes = false

    }
}