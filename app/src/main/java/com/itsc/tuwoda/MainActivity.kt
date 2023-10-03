package com.itsc.tuwoda

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
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
import androidx.compose.foundation.text.BasicTextField
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
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.itsc.tuwoda.ui.theme.MyFABWithText

class MainActivity : ComponentActivity() {

    val model = MyViewModel()

    private var mapViewModel: MapViewModel? = null
    var s:Int = 1
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
        setContent {
            Scaffold(
                bottomBar = {
                    MyBottomBar(model = model)
                },
                content = {
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
                                                ) {
                                                    Text(
                                                        text = "Начальная точка",
                                                        color = Color.White,
                                                        modifier = Modifier.offset(x = 15.dp)
                                                    )
                                                    Card(
                                                        shape = RoundedCornerShape(100.dp)
                                                    ) {
                                                        TextField(
                                                            value = stateBeginPoint,
                                                            onValueChange = {text ->
                                                                stateBeginPoint = text
                                                            },
                                                            trailingIcon = {
                                                                MyFloatingActionButton(
                                                                    background = R.drawable.ellipsefull,
                                                                    icon = R.drawable.geoicon,
                                                                    size = 50.dp,
                                                                    scaleX = (-5).dp,
                                                                    color = R.color.blue_main_alfa
                                                                )
                                                            }
                                                        )
                                                    }
                                                    Text(
                                                        text = "Конечная точка",
                                                        color = Color.White,
                                                        modifier = Modifier.offset(x = 15.dp)
                                                    )
                                                    Card(
                                                        shape = RoundedCornerShape(100.dp)
                                                    ) {
                                                        TextField(
                                                            value = stateEndPoint,
                                                            onValueChange = {text ->
                                                                stateEndPoint = text
                                                            },
                                                            trailingIcon = {
                                                                MyFloatingActionButton(
                                                                    background = R.drawable.ellipsefull,
                                                                    icon = R.drawable.geoicon,
                                                                    size = 50.dp,
                                                                    scaleX = (-5).dp,
                                                                    color = R.color.blue_main_alfa
                                                                )
                                                            }
                                                        )
                                                    }
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
                            padding = 5.dp
                        )
                    }
                },
                floatingActionButtonPosition = FabPosition.End
            )
        }
    }
}
