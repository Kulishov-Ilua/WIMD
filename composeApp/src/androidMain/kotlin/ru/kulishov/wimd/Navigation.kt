package ru.kulishov.wimd

import android.graphics.Color
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.MaterialTheme
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState

//##################################################################################################
//##################################################################################################
//#####################                  Navigation Android                  #######################
//##################################################################################################
//####  Author:Kulishov I.V.                         ###############################################
//####  Version:0.1.0                                ###############################################
//####  Date:20.01.2025                              ###############################################
//##################################################################################################
//##################################################################################################

//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
//BottomNavItem
//Variables:
//              label:String - label
//              icon:Int - icon
//              route:String - route
//~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
data class BottomNavItem(val label:String,val icon:Int,val route:String)

//#####################################################################################
//объекты навигации                                                        ############
//#####################################################################################
object Constants {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Трекер",
            icon = R.drawable.home,
            route = "tracker"        ),
        BottomNavItem(
            label = "Календарь",
            icon = R.drawable.calendar,
            route = "calendar"
        ),
        /* BottomNavItem(
             label = "Счётчик",            icon = R.drawable.counter,            route = "counter"        ),*/
        BottomNavItem(
            label = "Статистика",
            icon = R.drawable.statistic,
            route = "statistic"
        )
    )
}

//=====================================================================================
//BottomBar реализация
//=====================================================================================
@Composable
fun BottomNavigationBar(navController: NavController) {
    BottomNavigation(backgroundColor = androidx.compose.material3.MaterialTheme.colorScheme.background) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        Constants.BottomNavItems.forEach { navItem ->
            BottomNavigationItem(
                selected = currentRoute == navItem.route,
                onClick = {
                    navController.navigate(navItem.route)
                          },
                {
                    Image(painter = painterResource(navItem.icon), contentDescription = navItem.label,
                        modifier = Modifier.scale(if(currentRoute == navItem.route) 0.9F else 0.7F))
                },

                )
        }
    }
}

@Composable
fun NavHostContainer(
    navController: NavHostController, padding: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "tracker",
        modifier = Modifier.padding(paddingValues = padding),
        builder = {
            composable("tracker") {
                bottomIslandScreen({
                    Box(Modifier.fillMaxSize().background(color = androidx.compose.material3.MaterialTheme.colorScheme.background)
                , contentAlignment = Alignment.TopCenter){
                        var dd by remember { mutableStateOf(true) }
                        Row{
                            Box(Modifier.padding(end=10.dp)){
                                NavSingleButtom(dd,{d -> dd=d},"first")
                            }

                            Box(Modifier.padding(start=10.dp)){
                                NavSingleButtom(!dd,{d -> dd=!d},"second")
                            }
                        }
                    }
                },{}, androidx.compose.material3.MaterialTheme.colorScheme.primary, androidx.compose.material3.MaterialTheme.colorScheme.background)
            }

            composable("calendar") {                //calendarScreen(navController)
            }

            /*composable("counter") {                emptyScreen()            }*/
            composable("statistic") {

            }
        }
    )
}

//=====================================================================================
//NavDoubleButtom
//Input values:
//              startState:Boolean
//              onButState:() -> Unit
//              first:String - first element
//              second:String - second element
//=====================================================================================
@Composable
fun NavDoubleButtom(startState:Boolean, onButState:(Boolean) -> Unit,first:String,second:String){
    var navButState by remember { mutableStateOf(startState) }
    var animateBackground = animateIntOffsetAsState(
        targetValue = if(navButState) IntOffset(0,0) else IntOffset(75,0)
    )
    Box(Modifier.width(150.dp).height(30.dp).border(1.dp,androidx.compose.material3.MaterialTheme.colorScheme.primary,
        RoundedCornerShape(10)
    ).pointerInput(Unit){
        detectHorizontalDragGestures{
            change,dragAmount ->
            change.consume()
            if(dragAmount>0){
                if(navButState==true) navButState = false
            }else{
                if(navButState==false) navButState = true
            }
        }
    }, contentAlignment = Alignment.Center
    ){
        // Background box that moves
        Box(
            Modifier
                .fillMaxHeight()
                .width(75.dp)
                .background(androidx.compose.material3.MaterialTheme.colorScheme.primary,
                    RoundedCornerShape(9.dp)
                )
                .offset { animateBackground.value }
        )
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically){
            Box(Modifier.fillMaxHeight().weight(1f)
                .clickable {
                    navButState=true
                    onButState(navButState)
                }
                , contentAlignment = Alignment.Center){
                Text(first, style = TextStyle(
                    fontWeight = androidx.compose.material3.MaterialTheme.typography.titleSmall.fontWeight,
                    fontSize = androidx.compose.material3.MaterialTheme.typography.titleSmall.fontSize,
                    color = if(navButState) androidx.compose.material3.MaterialTheme.colorScheme.background else androidx.compose.material3.MaterialTheme.colorScheme.primary
                ))
            }
        }
        Row(Modifier.fillMaxSize(), verticalAlignment = Alignment.CenterVertically){
            Box(Modifier.fillMaxHeight().weight(1f)
                .clickable {
                    navButState=false
                    onButState(navButState)
                }
                , contentAlignment = Alignment.Center){
                Text(first, style = TextStyle(
                    fontWeight = androidx.compose.material3.MaterialTheme.typography.titleSmall.fontWeight,
                    fontSize = androidx.compose.material3.MaterialTheme.typography.titleSmall.fontSize,
                    color = if(!navButState) androidx.compose.material3.MaterialTheme.colorScheme.background else androidx.compose.material3.MaterialTheme.colorScheme.primary
                ))
            }
        }
    }
}

//=====================================================================================
//NavSingleButtom
//Input values:
//              startState:Boolean
//              onButState:() -> Unit
//              label:String -  element label
//=====================================================================================
@Composable
fun NavSingleButtom(startState:Boolean, onButState:(Boolean) -> Unit,label:String){
    //var navButState by remember { mutableStateOf(startState) }
    val theme = androidx.compose.material3.MaterialTheme.colorScheme.primary
    var animateBackground = animateColorAsState(
        targetValue = if(startState) theme else androidx.compose.ui.graphics.Color.Gray
    )
    var animatelabel = animateColorAsState(
        targetValue = if(!startState) theme else androidx.compose.material3.MaterialTheme.colorScheme.background
    )
    Button(
        onClick = {
            onButState(true)
        },
        //shape = RoundedCornerShape(10),
        Modifier.width(120.dp),
        colors = ButtonColors(containerColor = animateBackground.value,
            contentColor = animatelabel.value, disabledContentColor = animatelabel.value,
            disabledContainerColor = animateBackground.value)
    ) {
        Text(label, style = TextStyle(
            fontSize = androidx.compose.material3.MaterialTheme.typography.titleSmall.fontSize,
            fontWeight = androidx.compose.material3.MaterialTheme.typography.titleSmall.fontWeight,
            color = animatelabel.value
        )
        )
    }

}

