package ru.kulishov.wimd

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import wimd.composeapp.generated.resources.Res
import wimd.composeapp.generated.resources.calendar
import wimd.composeapp.generated.resources.home
import wimd.composeapp.generated.resources.statistic


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
data class BottomNavItem(val label:String,val icon:DrawableResource,val route:String)

//#####################################################################################
//объекты навигации                                                        ############
//#####################################################################################
object Constants {
    val BottomNavItems = listOf(
        BottomNavItem(
            label = "Трекер",
            icon = Res.drawable.home,
            route = "tracker"        ),
        BottomNavItem(
            label = "Календарь",
            icon = Res.drawable.calendar,
            route = "calendar"
        ),
        /* BottomNavItem(
             label = "Счётчик",            icon = R.drawable.counter,            route = "counter"        ),*/
        /*BottomNavItem(
            label = "Статистика",
            icon = Res.drawable.statistic,
            route = "statistic"
        )*/
    )
}

//=====================================================================================
//BottomBar реализация
//=====================================================================================
@Composable
fun BottomNavigationBar(navController: NavController, backgroundColor: Color) {
    BottomNavigation(backgroundColor = backgroundColor) {
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
    navController: NavHostController, padding: PaddingValues,
    listTask:List<Task>, listGroupTask: List<GroupTask>, redactTask: (Boolean,Task) -> Unit, redactGroup: (Boolean, GroupTask) -> Unit,
    primaryColor: Color,
    backgroundColor:Color,
    titleMedium:TextStyle,
    bodyMedium:TextStyle,
    titleLarge:TextStyle
) {
    NavHost(
        navController = navController,
        startDestination = "tracker",
        modifier = Modifier.padding(paddingValues = padding),
        builder = {
            composable ("tracker") {
                TrackerScreen(listTask,listGroupTask,{
                        type, task ->
                    redactTask(type,task)
                }, {
                        type, group ->
                    redactGroup(type,group)
                }, backgroundColor,primaryColor,
                    titleMedium,bodyMedium,titleLarge)
            }

            composable("calendar") {
                calendarScreen(listTask,backgroundColor
                    ,primaryColor, titleMedium, bodyMedium,
                    {
                            time->
                        //selectedDate=time

                    })
            }

            /*composable("counter") {                emptyScreen()            }*/
            composable("statistic") {

            }
        }
    )
}



//=====================================================================================
//NavSingleButtom
//Input values:
//              startState:Boolean
//              onButState:() -> Unit
//              label:String -  element label
//=====================================================================================
//@Composable
//fun NavSingleButtom(startState:Boolean, onButState:(Boolean) -> Unit,label:String){
//    //var navButState by remember { mutableStateOf(startState) }
//    val theme = androidx.compose.material3.MaterialTheme.colorScheme.primary
//    var animateBackground = animateColorAsState(
//        targetValue = if(startState) theme else androidx.compose.ui.graphics.Color.Gray
//    )
//    var animatelabel = animateColorAsState(
//        targetValue = if(!startState) theme else androidx.compose.material3.MaterialTheme.colorScheme.background
//    )
//    Button(
//        onClick = {
//            onButState(true)
//        },
//        //shape = RoundedCornerShape(10),
//        Modifier.width(120.dp),
//        colors = ButtonColors(containerColor = animateBackground.value,
//            contentColor = animatelabel.value, disabledContentColor = animatelabel.value,
//            disabledContainerColor = animateBackground.value)
//    ) {
//        Text(label, style = TextStyle(
//            fontSize = androidx.compose.material3.MaterialTheme.typography.titleSmall.fontSize,
//            fontWeight = androidx.compose.material3.MaterialTheme.typography.titleSmall.fontWeight,
//            color = animatelabel.value
//        )
//        )
//    }
//
//}
//
