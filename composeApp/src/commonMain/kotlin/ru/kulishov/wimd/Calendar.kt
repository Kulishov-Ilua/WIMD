package ru.kulishov.wimd

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

//##################################################################################################
//##################################################################################################
//#####################                   WIMD - Calendar                    #######################
//##################################################################################################
//####  Author:Kulishov I.V.                         ###############################################
//####  Version:0.1.0                                ###############################################
//####  Date:29.01.2025                              ###############################################
//##################################################################################################
//##################################################################################################

//=====================================================================================
//calendarScreen
//Input values:
//              taskList:List<Task> - tasks
//              currentDay:DateAndTimeS -
//              backgroundColor:Color -
//              primaryColor:Color -
//              title:TextStyle -
//              body:TextStyle -
//              onNewDay:(DateAndTimeS) -> Unit -
//              onTapTask:(Task)->Unit -
//=====================================================================================
@Composable
fun calendarScreen(taskList:List<Task>, currentDay:DateAndTimeS,backgroundColor: Color, primaryColor:Color, title: TextStyle, body:TextStyle, onNewDay:(DateAndTimeS) -> Unit, onTapTask:(Task)->Unit){
    var calendarState by remember { mutableStateOf(0) }
    bottomIslandScreen(calendarState, listOf(100.dp,100.dp,500.dp),{state -> calendarState=state},{
        hourColumn(emptyList(),title,primaryColor)
    },{when(calendarState) {
        1 -> {
            Text("start")
        }
    }
    },primaryColor,backgroundColor)
}

//=====================================================================================
//dateCalendarblock
//Input values:
//              date:DateAndTimeS -
//              onRedact:(DateAndTimeS) -> Unit -
//              backgroundColor:Color -
//              primaryColor:Color -
//=====================================================================================
@Composable
fun dateCalendarblock(date:DateAndTimeS,onRedact:(DateAndTimeS) -> Unit,backgroundColor:Color,primaryColor:Color){
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Row(verticalAlignment = Alignment.CenterVertically){

        }
    }
}

//=====================================================================================
//hourColumn
//Input values:
//              listTask:List<List<TaskView>> - primaryColor
//              title:TextStyle -
//              primaryColor:Color - primary
//              corpColor:Color - corp color
//=====================================================================================
@Composable
fun hourColumn(listTask:List<List<TaskView>>,title: TextStyle,corpColor:Color){
    LazyColumn {
        item{
            Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.TopCenter){
                Column(Modifier.padding(start = 25.dp).fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    for(i in 1 until 24){
                        Row(Modifier.padding(bottom = 50.dp), verticalAlignment = Alignment.CenterVertically){
                            Text(if(i<10) "0${i}:00" else "$i:00", modifier = Modifier.padding(end=5.dp), style = TextStyle(
                                fontSize = title.fontSize,
                                fontWeight = title.fontWeight,
                                fontFamily = title.fontFamily,
                                color = corpColor
                            )  )
                            Box(Modifier.fillMaxWidth().height(2.dp).background(color = corpColor))
                        }
                    }
                }
                val myTime = DateAndTimeS(0,0,0,0,0,0)
                myTime.convertUnixTimeToDate1(getSystemTime())
                myTime.hour+=3
                val newPading = 70*myTime.hour+ myTime.minute/60*70
                Box(Modifier.padding(top = newPading.dp)) {
                    Box(
                        Modifier.padding(top = 10.dp, end = 100.dp).fillMaxWidth().height(2.dp)
                            .background(color = Color.Red)
                    )
                    Box(
                        Modifier.padding(end = 25.dp).fillMaxWidth(),
                        contentAlignment = Alignment.CenterEnd
                    ) {
                        Text(
                            if (myTime.hour < 10) "0${myTime.hour}:${if (myTime.minute < 10) "0${myTime.minute}" else myTime.minute}"
                            else "${myTime.hour}:${if (myTime.minute < 10) "0${myTime.minute}" else myTime.minute}",
                            modifier = Modifier.padding(end = 5.dp), style = TextStyle(
                                fontSize = title.fontSize,
                                fontWeight = title.fontWeight,
                                fontFamily = title.fontFamily,
                                color = Color.Red
                            )
                        )
                    }
                }

            }
        }
    }

}

