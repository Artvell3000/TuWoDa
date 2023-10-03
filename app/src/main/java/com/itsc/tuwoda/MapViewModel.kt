package com.example.test.ui
import android.content.Context
import androidx.lifecycle.ViewModel
import com.itsc.tuwoda.R
import com.yandex.mapkit.Animation
import com.yandex.mapkit.ScreenPoint
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.geometry.Polyline
import com.yandex.mapkit.map.CameraListener
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.map.CameraUpdateReason
import com.yandex.mapkit.map.Map
import com.yandex.mapkit.map.PlacemarkMapObject
import com.yandex.mapkit.mapview.MapView
import com.yandex.runtime.image.ImageProvider

class Bridge{

}

class MapViewModel(
    var mapView: MapView,
    var context: Context
): ViewModel() {

    var tempPlacemark:PlacemarkMapObject? = null
    var placemarks:MutableList<PlacemarkMapObject> = mutableListOf(
        //PlacemarkMapObject()
    )

    private var imageProviders: kotlin.collections.Map<String, ImageProvider> = mapOf(
        "dollar" to ImageProvider.fromResource(context, R.drawable.ic_pin),
        "start_end" to ImageProvider.fromResource(context,R.drawable.ic_start_end),
        //"my_geo" to ImageProvider.fromResource(context,R.drawable.ic_people)
    )
    var myGeolocationPlacemark: PlacemarkMapObject? = null

    var cameraListener:CameraListener? = null
    private var addNewPlacemarkState:Boolean = false

    fun setMyLocation(point: Point){
        myGeolocationPlacemark = mapView.map.mapObjects.addPlacemark(
            point,
            imageProviders["start_end"]!!
        )
    }
    fun goToMyLocation(){
        mapView.map.apply {
            myGeolocationPlacemark?.let {
                move(
                    CameraPosition(
                        it.geometry,
                        cameraPosition.zoom,
                        cameraPosition.azimuth,
                        cameraPosition.tilt),
                    Animation(Animation.Type.SMOOTH, 30f),
                    null
                )
            }
        }
    }
    fun addRoute(){
        val l:MutableList<Point> = mutableListOf()
        placemarks.forEach {
            l.add(it.geometry)
        }
        DriwerRoad(l)

    }
    fun addBridge(b:Bridge){

    }

    private fun DriwerRoad(l:List<Point>){
        mapView.map.mapObjects.addPolyline(
            Polyline(
                l
            )
        )
    }

    private fun updateCenterPlacemar(imageProvider:ImageProvider){
        val centerX = mapView.width / 2f
        val centerY = mapView.height / 2f
        val centerPoint = ScreenPoint(centerX, centerY)
        val worldPoint = mapView.screenToWorld(centerPoint)
        tempPlacemark = mapView.map.mapObjects.addPlacemark(
            worldPoint, imageProvider
        )
    }
    fun goTOAddNewPlacemarkState(){
        addNewPlacemarkState = true
        val imageProvider = ImageProvider.fromResource(context,R.drawable.ic_pin)
        updateCenterPlacemar(imageProvider)

        cameraListener = object:CameraListener{
            override fun onCameraPositionChanged(
                p0: Map,
                p1: CameraPosition,
                p2: CameraUpdateReason,
                p3: Boolean
            ) {
                tempPlacemark?.let {
                    mapView.map.mapObjects.remove(it)
                }

                updateCenterPlacemar(imageProvider)
            }
        }
        cameraListener?.let {
            mapView.map.addCameraListener(it)
        }
    }

    fun goToBasicState(){
        addNewPlacemarkState = false
        tempPlacemark?.let {
            placemarks.add(it)
        }
        cameraListener?.let {
            mapView.map.removeCameraListener(it)
        }
        tempPlacemark = null
    }
}

