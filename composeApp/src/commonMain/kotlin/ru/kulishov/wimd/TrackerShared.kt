package ru.kulishov.wimd

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonColors
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

//##################################################################################################
//##################################################################################################
//#####################                 WIMD - TrackerShared                 #######################
//##################################################################################################
//####  Author:Kulishov I.V.                         ###############################################
//####  Version:0.1.0                                ###############################################
//####  Date:24.01.2025                              ###############################################
//##################################################################################################
//##################################################################################################

//=====================================================================================
//chooseCreateBlock
//Input values:
//              onGroup:() -> Unit -
//              onTask:() -> Unit -
//              titleStyle:TextStyle -
//              primaryColor:Color -
//              backgroundColor:Color -
//=====================================================================================
@Composable
fun chooseCreateBlock(onGroup:() -> Unit,onTask:() -> Unit, titleStyle: TextStyle, primaryColor:Color,backgroundColor:Color){
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Создать", style = TextStyle(
                fontSize = 32.sp,
                fontWeight = titleStyle.fontWeight,
                color = primaryColor
            ))
            Button(onClick ={onTask()},
                shape = RoundedCornerShape(10),
                modifier = Modifier.padding(top=25.dp, start = 25.dp,end=25.dp).fillMaxWidth().height(100.dp)
                , colors = ButtonDefaults.buttonColors(primaryColor,backgroundColor,primaryColor,backgroundColor)
            ){
                Text("Задачу", style = TextStyle(
                    fontWeight = titleStyle.fontWeight,
                    fontSize = titleStyle.fontSize,
                    fontFamily = titleStyle.fontFamily,
                    color = backgroundColor
                ))
            }
            Button(onClick ={onGroup()},
                shape = RoundedCornerShape(10),
                modifier = Modifier.padding(top=25.dp, start = 25.dp,end=25.dp).fillMaxWidth().height(100.dp)
                , colors = ButtonDefaults.buttonColors(primaryColor,backgroundColor,primaryColor,backgroundColor)
            ){
                Text("Группу", style = TextStyle(
                    fontWeight = titleStyle.fontWeight,
                    fontSize = titleStyle.fontSize,
                    fontFamily = titleStyle.fontFamily,
                    color = backgroundColor
                ))
            }
        }
    }
}

//=====================================================================================
//taskGroupScreen
//Input values:
//              listTask:List<Task> -
//              listGroupTask: List<GroupTask> -
//              onTapGroup: () -> Unit -
//              onTapTask:() -> Unit -
//              primaryColor:Color -
//              backgroundColor:Color -
//              titleStyle:TextStyle -
//              bodyStyle:TextStyle -
//=====================================================================================
@Composable
fun taskGroupScreen(listTask:List<Task>, listGroupTask: List<GroupTask>,onTapGroup: () -> Unit, onTapTask:() -> Unit, primaryColor:Color,backgroundColor:Color,titleStyle: TextStyle,bodyStyle: TextStyle){
    //-----------------------------------------------------------------------------
    //Состояние отображения:
    //      false -> Группы
    //      true -> Задачи
    //-----------------------------------------------------------------------------
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val listState = rememberLazyListState()
            val currentPage by remember { derivedStateOf { listState.firstVisibleItemIndex } }
            val coroutineScope = rememberCoroutineScope()
            var buttonScroll by remember { mutableStateOf(-1) }
            NavDoubleButtom(currentPage==0,{
                rec -> if(rec){
              buttonScroll = 0
            }else{
              buttonScroll=1
            }
            },"Задачи","Группы",titleStyle,backgroundColor,primaryColor)

            //чекаем смещение
            val currentOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }
            var fixOffset by remember { mutableStateOf(0) }
            //println("Current: $currentOffset fix: $fixOffset")

            LaunchedEffect(buttonScroll){
                if(buttonScroll==0){
                    coroutineScope.launch {
                        buttonScroll=-1
                        listState.animateScrollToItem(0)
                        fixOffset=0
                    }
                }
                if(buttonScroll==1){
                    coroutineScope.launch {
                        buttonScroll=-1
                        listState.animateScrollToItem(1)
                        fixOffset=1000
                    }
                }

            }
            LaunchedEffect(currentOffset) {
                if (currentOffset > fixOffset&&buttonScroll<0) {
                    // Свайп вправо
                    coroutineScope.launch {
                        listState.animateScrollToItem(1)
                        fixOffset=1000
                    }
                } else if (currentOffset < fixOffset&&buttonScroll<0) {
                    // Свайп влево
                    coroutineScope.launch {
                        listState.animateScrollToItem(0)
                        fixOffset=0
                    }
                }
            }
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxSize()
            ) {
                item() {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(start = 15.dp,end=15.dp)

                    ) {
                       LazyColumn {
                           items(listTask){
                               task->
                               var dateTimeStart = DateAndTimeS(0,0,0,0,0,0)
                               dateTimeStart.convertUnixTimeToDate1(task.start*1000)
                               var dateTimeEnd = DateAndTimeS(0,0,0,0,0,0)
                               dateTimeEnd.convertUnixTimeToDate1(task.end*1000)
                               var timet = DateAndTimeS(0,0,0,0,0,0)
                               timet.calculateDifference(dateTimeStart,dateTimeEnd)
                               var tstring=""
                               if (timet.day>0) tstring+=timet.day.toString() + "д."
                               tstring+= timet.hour.toString() + "ч."
                               if(timet.day==0) tstring+= timet.minute.toString() + "м."
                               taskCardTracker(TaskView(
                                   task.uid!!, task.name!!, dateTimeStart, dateTimeEnd,
                                   parseColor(listGroup[task.groupID - 1].color), tstring
                               ),titleStyle,bodyStyle,primaryColor)
                           }
                       }
                    }
                }
                item() {
                    Box(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(start = 15.dp,end=15.dp)

                    ) {
                        LazyColumn {
                            items(listGroupTask){
                                    group->
                                groupCardTracker(group,titleStyle,primaryColor)
                            }
                        }
                    }
                }
            }

        }
    }


}

//=====================================================================================
//Task card
//Input values:
//              task:TaskView - task
//              titleStyle:TextStyle -
//              bodyStyle:TextStyle -
//              primaryColor:Color -
//=====================================================================================
@Composable
fun taskCardTracker(task: TaskView,titleStyle: TextStyle,bodyStyle: TextStyle, primaryColor:Color){
    Box(
        Modifier
            .fillMaxWidth()
            .height(70.dp)
        , contentAlignment = Alignment.CenterStart){
        Row(Modifier.padding(start = 15.dp, end=15.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                .width(5.dp)
                .height(50.dp)
                .background(color = task.color, shape = RoundedCornerShape(4)))
            Column(Modifier.padding(start=10.dp)) {
                Text(text = task.name, style = TextStyle(
                    fontSize = titleStyle.fontSize,
                    fontWeight = titleStyle.fontWeight,
                    color = primaryColor
                ))
                Text(text = "${task.start.hour}:${task.start.minute} -> ${task.end.hour}:${task.end.minute}", style = TextStyle(
                    fontSize = bodyStyle.fontSize,
                    fontWeight = bodyStyle.fontWeight,
                    color = primaryColor
                ))
            }
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd){
                Text(text = task.time, style = TextStyle(
                    fontSize = titleStyle.fontSize,
                    fontWeight = titleStyle.fontWeight,
                    color = primaryColor
                ))
            }
        }
    }
}

//=====================================================================================
//Group card
//Input values:
//              groupTask: GroupTask - group
//              titleStyle:TextStyle -
//              primaryColor:Color -
//=====================================================================================
@Composable
fun groupCardTracker(groupTask: GroupTask,titleStyle: TextStyle, primaryColor:Color){
    Box(
        Modifier
            .fillMaxWidth()
            .height(70.dp)
        , contentAlignment = Alignment.CenterStart){
        Row(Modifier.padding(start = 15.dp, end=15.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Box(modifier = Modifier
                .width(5.dp)
                .height(50.dp)
                .background(color = parseColor(groupTask.color), shape = RoundedCornerShape(4)))
            Column(Modifier.padding(start=10.dp)) {
                Text(text = groupTask.name!!, style = TextStyle(
                    fontSize = titleStyle.fontSize,
                    fontWeight = titleStyle.fontWeight,
                    color = primaryColor
                ))
            }

        }
    }
}

//=====================================================================================
//Функция парсинга строки в цвет
//Input values:
//              colorString: String - color
//Output values:
//              color:Color - color
//=====================================================================================
fun parseColor(colorString: String): Color {
    val rgb = colorString.split(",").map { it.trim().toInt() }
    return Color(rgb[0], rgb[1], rgb[2])
}

