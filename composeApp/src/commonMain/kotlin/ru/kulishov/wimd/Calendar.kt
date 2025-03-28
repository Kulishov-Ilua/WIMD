package ru.kulishov.wimd

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.jetbrains.compose.resources.painterResource
import wimd.composeapp.generated.resources.Res
import wimd.composeapp.generated.resources.vector

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
//              backgroundColor:Color -
//              primaryColor:Color -
//              title:TextStyle -
//              body:TextStyle -
//              onNewDay:(DateAndTimeS) -> Unit -
//              onTapTask:(Task)->Unit -
//=====================================================================================
@Composable
fun calendarScreen(taskList:List<Task>, backgroundColor: Color, primaryColor:Color, title: TextStyle, body:TextStyle, onRedactDay:(Long)->Unit){
    var calendarState by remember { mutableStateOf(0) }
    var actualDay = remember { mutableStateOf(getSystemTime()) }
    var transferDate = DateAndTimeS(0,0,0,0,0,0)
    transferDate.convertUnixTimeToDate1(actualDay.value)

    var curT by remember { mutableStateOf(getCurrentTask(taskList,actualDay.value)) }
    var nTaskList by remember { mutableStateOf(listOf<TaskView>()) }

    for(task in curT){
        var dateTimeStart = DateAndTimeS(0,0,0,0,0,0)
        dateTimeStart.convertUnixTimeToDate1(task.start)
        var dateTimeEnd = DateAndTimeS(0,0,0,0,0,0)
        dateTimeEnd.convertUnixTimeToDate1(task.endTime)
        var timet = DateAndTimeS(0,0,0,0,0,0)
        timet.calculateDifference(dateTimeStart,dateTimeEnd)
        var tstring=""
        if (timet.day>0) tstring+=timet.day.toString() + "д."
        tstring+= timet.hour.toString() + "ч."
        if(timet.day==0) tstring+= timet.minute.toString() + "м."
        nTaskList+=TaskView(
            task.uid!!, task.name!!, dateTimeStart, dateTimeEnd,
            parseColor(listGroup.value[task.groupID].color), tstring
        )
    }

    bottomIslandScreen(calendarState, listOf(150.dp),{state -> },{
        hourColumn(taskSortedTime(nTaskList),actualDay.value,title,primaryColor)
    },{when(calendarState) {
        0,1 -> {
            dateCalendarblock(actualDay.value,{},backgroundColor,primaryColor,title,{type->
                if(type==-1){
                    transferDate.devideDay()
                    actualDay.value=transferDate.convertToSeconds(transferDate)*1000
                    nTaskList= emptyList()
                    curT= emptyList()
                    curT=getCurrentTask(taskList,actualDay.value)
                }
                if(type==1){
                    transferDate.plusDay()
                    actualDay.value=transferDate.convertToSeconds(transferDate)*1000
                    nTaskList= emptyList()
                    curT= emptyList()
                    curT = getCurrentTask(taskList,actualDay.value)
                }
            })
        }
    }
    },primaryColor,backgroundColor)
}

//=====================================================================================
//dateCalendarblock
//Input values:
//              date:DateAndTimeS -
//              update:(Int) ->Unit -
//              backgroundColor:Color -
//              primaryColor:Color -
//=====================================================================================
@Composable
fun dateCalendarblock(date:Long,onRedact:(DateAndTimeS) -> Unit,backgroundColor:Color,primaryColor:Color, title: TextStyle, update:(Int) -> Unit){
    var transferDate = DateAndTimeS(0,0,0,0,0,0)
    transferDate.convertUnixTimeToDate1(date)
    Box(Modifier.fillMaxSize()
//                .pointerInput(Unit) {
//                detectHorizontalDragGestures { change, dragAmount ->
//                    change.consume()
//                    if (dragAmount > 0) {
//                        currentDay.plusDay()
//                        println(currentDay)
//                    } else {
//                        currentDay.devideDay()
//                        println(currentDay)
//                    }
//                }
//            }
        , contentAlignment = Alignment.Center){
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(painter = painterResource(Res.drawable.vector), contentDescription = "",
                modifier = Modifier.rotate(90f).clickable {
                    update(-1)
                }, tint = backgroundColor)
            Text(transferDate.getTextDate(), style = TextStyle(
                fontSize = 36.sp,
                fontStyle = title.fontStyle,
                fontFamily = title.fontFamily,
                fontWeight = title.fontWeight,
                color = backgroundColor
            ), modifier = Modifier.padding(start = 10.dp, end = 10.dp)
            )
            Icon(painter = painterResource(Res.drawable.vector), contentDescription = "",
                modifier = Modifier.rotate(270f).clickable {
                    update(1)
                }, tint = backgroundColor)
        }

    }
}

//=====================================================================================
//hourColumn
//Input values:
//              listTask:List<List<TaskView>> - primaryColor
//              currentDay:DateAndTimeS -
//              title:TextStyle -
//              primaryColor:Color - primary
//              corpColor:Color - corp color
//=====================================================================================
@Composable
fun hourColumn(listTask:List<List<TaskView>>,currentDay:Long,title: TextStyle,corpColor:Color){
    val myTime = DateAndTimeS(0,0,0,0,0,0)
    myTime.convertUnixTimeToDate1(getSystemTime())
    myTime.hour+=3
    //println(taskSortedTime(testTaskList))
    var transferDate = DateAndTimeS(0,0,0,0,0,0)
    transferDate.convertUnixTimeToDate1(currentDay)
    println(currentDay)
    val stateL = rememberLazyListState()
    LaunchedEffect(1){
        stateL.animateScrollToItem(transferDate.hour)
    }
    Box() {
        LazyColumn(state = stateL, modifier = Modifier.fillMaxWidth()) {
            item {
                Box {
                    Column(
                        Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(Modifier.fillMaxWidth().height(50.dp)) {

                        }
                        for (i in 0 until 23) {
                            Box(Modifier.fillMaxWidth().height(50.dp)) {
                                Row(
                                    modifier = Modifier.padding(start = 25.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        if ((i + 1) < 10) "0${(i + 1)}:00" else "${(i + 1)}:00",
                                        modifier = Modifier.padding(end = 5.dp),
                                        style = TextStyle(
                                            fontSize = title.fontSize,
                                            fontWeight = title.fontWeight,
                                            fontFamily = title.fontFamily,
                                            color = corpColor
                                        )
                                    )
                                    Box(
                                        Modifier.fillMaxWidth().height(2.dp)
                                            .background(color = corpColor)
                                    )
                                }


                            }
                        }
                    }

                    val newPading = (50 * myTime.hour + myTime.minute * 5 / 6)
                    Box(Modifier.padding(top = newPading.dp)) {
                        Box(
                            Modifier.padding(top = 10.dp, end = 90.dp).fillMaxWidth()
                                .height(3.dp)
                                .background(color = if (transferDate.dateCompare(myTime)) Color.Red else Color.Gray)
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
                                    color = if (transferDate.dateCompare(myTime)) Color.Red else Color.Gray
                                )
                            )
                        }
                    }
                    LazyRow(Modifier.padding(start = 85.dp)) {
                        items(listTask){
                                item->
                            //println(item)
                            Box {
                                for (x in item) {
                                    val startPad =
                                        if (x.start.day == transferDate.day) x.start.hour else 0
                                    var startHeight =
                                        if (x.start.day == transferDate.day) x.start.hour else 0
                                    if (x.start.minute > x.end.minute) {
                                        startHeight += 1
                                    }
                                    val boxHeight =
                                        50 * (x.end.hour - startHeight) + if (x.start.minute > x.end.minute) (x.end.minute - x.start.minute + 60) * 5 / 6 else (x.end.minute - x.start.minute) * 5 / 6
                                    val itemPading = (50 * startPad + x.start.minute * 5 / 6)+10
                                    println(itemPading)
                                    Box(
                                        modifier = Modifier.padding(top = itemPading.dp)
                                            .width(200.dp).height(boxHeight.dp)
                                            .background(x.color, shape = RoundedCornerShape(10))
                                    ) {
                                        Column {
                                            Text(x.name)
                                            Text(x.time)
                                        }
                                    }
                                }
                            }
                        }
                    }

                }




            }
        }

//        val newPading = (50*myTime.hour + myTime.minute*5/6)
//
//
    }


}


val testTaskList = listOf(
    TaskView(
        uid = 1,
        name = "Morning Meeting",
        start = DateAndTimeS(2025, 3, 27, 9, 0,0),
        end = DateAndTimeS(2025, 3, 27, 10, 0,0),
        color = Color.Red,
        time = "09:00 - 10:00"
    ),
    TaskView(
        uid = 2,
        name = "Project Planning",
        start = DateAndTimeS(2025, 3, 27, 10, 0,0),
        end = DateAndTimeS(2025, 3, 27, 12, 0,0),
        color = Color.Blue,
        time = "10:00 - 12:00"
    ),
    TaskView(
        uid = 3,
        name = "Lunch Break",
        start = DateAndTimeS(2025, 3, 27, 12, 0,0),
        end = DateAndTimeS(2025, 3, 27, 13, 0,0),
        color = Color.Green,
        time = "12:00 - 13:00"
    ),
    TaskView(
        uid = 4,
        name = "Team Workshop",
        start = DateAndTimeS(2025, 3, 27, 13, 0,0),
        end = DateAndTimeS(2025, 3, 27, 15, 0,0),
        color = Color.Yellow,
        time = "13:00 - 15:00"
    ),
    TaskView(
        uid = 5,
        name = "Client Call",
        start = DateAndTimeS(2025, 3, 27, 14, 30,0),
        end = DateAndTimeS(2025, 3, 27, 16, 0,0),
        color = Color.Cyan,
        time = "14:30 - 16:00"
    ),
    TaskView(
        uid = 6,
        name = "Code Review",
        start = DateAndTimeS(2025, 3, 27, 15, 0,0),
        end = DateAndTimeS(2025, 3, 27, 17, 0,0),
        color = Color.Blue,
        time = "15:00 - 17:00"
    ),
    TaskView(
        uid = 7,
        name = "Evening Meeting",
        start = DateAndTimeS(2025, 3, 27, 18, 0,0),
        end = DateAndTimeS(2025, 3, 27, 19, 0,0),
        color = Color.Cyan,
        time = "18:00 - 19:00"
    ),
    TaskView(
        uid = 8,
        name = "Project Deadline",
        start = DateAndTimeS(2025, 3, 27, 19, 0,0),
        end = DateAndTimeS(2025, 3, 28, 9, 0,0),
        color = Color.Magenta,
        time = "19:00 - 09:00 (next day)"
    )
)