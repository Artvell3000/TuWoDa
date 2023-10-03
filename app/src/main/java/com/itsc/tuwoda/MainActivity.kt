package com.itsc.tuwoda

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ShapeDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.test.ui.MapViewModel
import com.itsc.tuwoda.ui.theme.MyFABWithText
import com.yandex.mapkit.Animation
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.geometry.Point
import com.yandex.mapkit.location.FilteringMode
import com.yandex.mapkit.location.Location
import com.yandex.mapkit.location.LocationListener
import com.yandex.mapkit.location.LocationManager
import com.yandex.mapkit.location.LocationStatus
import com.yandex.mapkit.map.CameraPosition
import com.yandex.mapkit.mapview.MapView

class MainActivity : ComponentActivity() {

    val model = MyViewModel()
    private var mapViewModel: MapViewModel? = null

    private lateinit var context: Context
    private lateinit var locationManager: LocationManager
    private lateinit var pLauncher: ActivityResultLauncher<String>

    //region Permission func
    private fun registerPermissionListener(){
        pLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()){
            if(it){
                //mapViewModel?.goToMyLocation()
            }
            else{

            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun doItAndCheckPermissions(
        action:() -> Unit
    ){
        when{
            (ContextCompat.checkSelfPermission(
                this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
                    &&
                    ContextCompat.checkSelfPermission(
                        this@MainActivity, Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    &&
                    ContextCompat.checkSelfPermission(
                        this@MainActivity, Manifest.permission.ACCESS_BACKGROUND_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                    &&
                    ContextCompat.checkSelfPermission(
                        this@MainActivity, Manifest.permission.INTERNET
                    ) == PackageManager.PERMISSION_GRANTED )-> {
                action()
            }
            else -> {
                pLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                pLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION)
                pLauncher.launch(Manifest.permission.ACCESS_BACKGROUND_LOCATION)
                pLauncher.launch(Manifest.permission.INTERNET)
            }
        }
    }

    //endregion

    @RequiresApi(Build.VERSION_CODES.Q)
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        MapKitFactory.setApiKey("f3d7b1d9-51c9-46c4-9c3c-c3e25d30f0d8")
        MapKitFactory.initialize(this)
        locationManager = MapKitFactory.getInstance().createLocationManager()
        //region locationManager Setting

        locationManager.requestSingleUpdate(
            object: LocationListener {
                override fun onLocationUpdated(p0: Location) {
                    mapViewModel?.setMyLocation(
                        p0.position
                    )
                }

                override fun onLocationStatusUpdated(p0: LocationStatus) {
                    Toast(
                        context,

                        )
                }
            }
        )

        locationManager.subscribeForLocationUpdates(
            50.0,
            10,
            50.0,
            false,
            FilteringMode.ON,
            object: LocationListener {
                override fun onLocationUpdated(p0: Location) {
                    mapViewModel?.setMyLocation(
                        p0.position
                    )
                }

                override fun onLocationStatusUpdated(p0: LocationStatus) {
                    Toast.makeText(context,"123", Toast.LENGTH_LONG).show()
                }

            }
        )
        //endregion

        setContent {
            context = LocalContext.current
            mapViewModel = viewModel<MapViewModel>(
                factory = object : ViewModelProvider.Factory {
                    override fun<T: ViewModel> create(modelClass: Class<T>): T{
                        return MapViewModel(
                            mapView = MapView(context).apply{
                                this.map.move(
                                    CameraPosition(
                                        Point(
                                            56.452387,
                                            84.972267
                                        ),
                                        10.0f,
                                        0.0f,
                                        0.0f),
                                    Animation(Animation.Type.SMOOTH, 0f),
                                    null
                                )
                            },
                            context = context
                        ) as T
                    }
                }
            )

            Scaffold(
                bottomBar = {
                    MyBottomBar(model = model)
                },
                content = {
                    YandexMap(modifier = Modifier)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(it),
                        contentAlignment = Alignment.TopEnd,
                    ){
                        MyFloatingActionButton(
                            background = R.drawable.more,
                            icon = R.drawable.morepoint,
                            size = 50.dp
                        )
                    }
                },
                floatingActionButton = {
                    Column(
                        modifier = Modifier.offset(y = 25.dp),
                        horizontalAlignment = Alignment.End
                    ) {
                        Box(
                            contentAlignment = Alignment.TopEnd
                        ) {
                            if (model.stateRouting){
                                MyFABWithText(
                                    text = "Построить новый маршрут",
                                    x = (-15).dp,
                                    y = (-45).dp,
                                    paddingCardHorizontal = 20.dp,
                                    paddingTextHorizontal = 10.dp,
                                    sizeButton = 45.dp,
                                    backgroundButton = R.drawable.ellipsefull,
                                    iconButton = R.drawable.plus,
                                    colorBackgroundButton = R.color.blue_light_main,
                                    colorBackgroundText = R.color.blue_light_main_alfa,
                                    model = model
                                )
                                if(model.stateMapDialog){
                                    var stateBeginPoint by remember {
                                        mutableStateOf("")
                                    }
                                    var stateEndPoint by remember {
                                        mutableStateOf("")
                                    }
                                    if (model.stateMapDialog){
                                        AlertDialog(
                                            containerColor = colorResource(id = R.color.blue_main_alfa),
                                            onDismissRequest = { model.stateMapDialog = false },
                                            confirmButton = {
                                                Button(
                                                    onClick = { /*TODO*/ }
                                                ) {
                                                    Text(text = "Построить")
                                                }
                                            },
                                            dismissButton = {
                                                Button(
                                                    onClick = { /*TODO*/ }
                                                ) {
                                                    Text(text = "Назад")
                                                }
                                            },
                                            text = {
                                                Column(
                                                    horizontalAlignment = Alignment.End
                                                ) {
                                                    Text(text = "Начальная точка")
                                                    TextField(
                                                        value = stateBeginPoint,
                                                        onValueChange = {text ->
                                                            stateBeginPoint = text
                                                        },
                                                        trailingIcon = {
                                                            Icon(
                                                                painter = painterResource(id = R.drawable.geoicon),
                                                                contentDescription = "geoicon",
                                                                modifier = Modifier.size(30.dp)
                                                            )
                                                        }
                                                    )
                                                    Text(text = "Конечная точка")

                                                    TextField(
                                                        value = stateEndPoint,
                                                        onValueChange = {text ->
                                                            stateEndPoint = text
                                                        }
                                                    )
                                                }

                                            },
                                        )
                                    }

                                }
                                MyFABWithText(
                                    backgroundButton = R.drawable.ellipsefull,
                                    iconButton = R.drawable.star,
                                    colorBackgroundButton = R.color.blue_light_main,
                                    colorBackgroundText = R.color.blue_light_main_alfa,
                                    text = "Повторить маршрут",
                                    sizeButton = 45.dp,
                                    x = (-65).dp,
                                    paddingCardHorizontal = 20.dp,
                                    paddingTextHorizontal = 10.dp,
                                )
                            }
                            MyFloatingActionButton(
                                background = R.drawable.ellipsefull,
                                icon = R.drawable.routing,
                                padding = 5.dp,
                                state = model.stateRouting,
                                onState = {state ->
                                    model.stateRouting = state
                                }
                            )
                        }
                        MyFloatingActionButton(
                            background = R.drawable.ellipsefull,
                            icon = R.drawable.geo,
                            padding = 5.dp,
                            onClick = {
                                doItAndCheckPermissions {
                                    Toast.makeText(
                                        this@MainActivity,
                                        "place: ${
                                            mapViewModel?.myGeolocationPlacemark?.geometry?.longitude.toString()

                                        } : ${
                                            mapViewModel?.myGeolocationPlacemark?.geometry?.latitude.toString()
                                        }",
                                        Toast.LENGTH_LONG)
                                        .show()
                                    mapViewModel?.goToMyLocation()
                                }
                            }
                        )
                    }
                },
                floatingActionButtonPosition = FabPosition.End
            )
        }
        registerPermissionListener()
    }

    override fun onStop() {
        mapViewModel?.mapView?.onStop()
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }

    override fun onStart() {
        mapViewModel?.mapView?.onStart()
        MapKitFactory.getInstance().onStart()
        super.onStart()
    }

    @Composable
    fun YandexMap(modifier: Modifier = Modifier) {
        AndroidView(
            modifier = modifier.fillMaxSize(),
            factory = {
                mapViewModel!!.mapView
            },
            update = {
                mapViewModel!!.mapView
            }
        )
    }
}
