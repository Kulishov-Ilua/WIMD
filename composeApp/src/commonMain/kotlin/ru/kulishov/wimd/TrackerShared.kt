package ru.kulishov.wimd

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults

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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import kotlinx.datetime.TimeZone
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import wimd.composeapp.generated.resources.Res
import wimd.composeapp.generated.resources.ok
import wimd.composeapp.generated.resources.vector

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
//createTaskBlock
//Input values:
//              task:Task - task
//              onSave: () -> Unit -
//              onBack: () -> Unit -
//              onDate:(Long) -> Long
//              onTime:(Long) ->Long
//              primaryColor:Color -
//              backgroundColor:Color -
//              titleStyle:TextStyle -
//              bodyStyle:TextStyle -
//=====================================================================================
@Preview
@Composable
fun createTaskBlock(task:Task,onSave: (Task) -> Unit, onBack:(Task) -> Unit, onDate:(Long) -> Long,onTime:(Long) ->Long,primaryColor:Color,backgroundColor:Color, titleStyle: TextStyle,bodyStyle: TextStyle){
    var newTask by remember { mutableStateOf(task) }
    var newName by remember { mutableStateOf(task.name!!) }
    var newStart by remember { mutableStateOf(task.start) }
    var newEnd by remember { mutableStateOf(task.end) }
    var newGroup by remember { mutableStateOf(task.groupID) }
    var newStartFormated by remember { mutableStateOf(DateAndTimeS(0,0,0,0,0,0)) }
    var newEndFormated by remember { mutableStateOf(DateAndTimeS(0,0,0,0,0,0)) }

    //Флаг открытия окна групп
    var groupFlag by remember { mutableStateOf(false) }

    val textfieldStyle = TextFieldDefaults.textFieldColors(
        backgroundColor = Color.Transparent,
        textColor = primaryColor,
        disabledTextColor = primaryColor,
        cursorColor = primaryColor,
        unfocusedLabelColor = primaryColor,
        focusedLabelColor = primaryColor,
        disabledLabelColor = primaryColor,
        )
    val titleTextStyle = TextStyle(
        fontWeight = titleStyle.fontWeight,
        fontSize = titleStyle.fontSize,
        color = primaryColor
    )
    val textfieldTextStyle = TextStyle(
        fontWeight = bodyStyle.fontWeight,
        fontSize = bodyStyle.fontSize,
        color = primaryColor
    )

    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter){
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Text(text = if(task.uid==null) "Создать задачу" else "Редактировать задачу", style = titleTextStyle, modifier = Modifier.padding(bottom = 25.dp)
            )
            LazyColumn( horizontalAlignment = Alignment.CenterHorizontally) {
                item{
                    TextField(value = newName,
                        onValueChange ={newName = it},
                        label = {
                            Text(text = "Название", style = textfieldTextStyle
                            )
                        },
                        textStyle = bodyStyle,
                        singleLine = true,
                        colors = textfieldStyle
                    )
                    Box(
                        Modifier
                            .width(300.dp)
                            .height(2.dp)
                            .background(primaryColor))
                    Text(
                        text = "C",
                        style = titleTextStyle,
                        modifier = Modifier.padding(top=25.dp)
                    )
                    Row(Modifier.padding(top=5.dp), verticalAlignment = Alignment.CenterVertically){
                        Box(
                            Modifier
                                .padding(end = 15.dp)
                                .width(135.dp)
                                .height(50.dp)
                                .clickable { newStart = onDate(newStart)
                                           newStartFormated.convertUnixTimeToDate1(newStart)},
                            contentAlignment = Alignment.Center){
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (newStart > 0L) "${newStartFormated.day}/${newStartFormated.month}/${newStartFormated.year}" else "Дата",
                                    style = textfieldTextStyle
                                )
                                Box(
                                    Modifier
                                        .width(135.dp)
                                        .height(2.dp)
                                        .background(primaryColor))
                            }
                        }
                        Box(
                            Modifier
                                .padding(start = 15.dp)
                                .width(135.dp)
                                .height(50.dp)
                                .clickable { newStart = onTime(newStart)
                                           newStartFormated.convertUnixTimeToDate1(newStart)},
                            contentAlignment = Alignment.Center){
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (newStart>0L) "${newStartFormated.hour}:${newStartFormated.minute}" else "Время",
                                    style = textfieldTextStyle
                                )
                                Box(
                                    Modifier
                                        .width(135.dp)
                                        .height(2.dp)
                                        .background(primaryColor))
                            }
                        }
                    }
                    Text(
                        text = "По",
                        style =titleTextStyle,
                        modifier = Modifier.padding(top=25.dp)
                    )
                    Row(Modifier.padding(top=5.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .padding(end = 15.dp)
                                .width(135.dp)
                                .height(50.dp)
                                .clickable {
                                    newEnd = onDate(newEnd)
                                    newEndFormated.convertUnixTimeToDate1(newEnd)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (newEnd > 0L) "${newEndFormated.day}/${newEndFormated.month}/${newEndFormated.year}" else "Дата",
                                    style = textfieldTextStyle
                                )
                                Box(
                                    Modifier
                                        .width(135.dp)
                                        .height(2.dp)
                                        .background(primaryColor)
                                )
                            }
                        }
                        Box(
                            Modifier
                                .padding(start = 15.dp)
                                .width(135.dp)
                                .height(50.dp)
                                .clickable {
                                    newEnd = onTime(newEnd)
                                    newEndFormated.convertUnixTimeToDate1(newEnd)
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (newEnd > 0L) "${newEndFormated.hour}:${newEndFormated.minute}" else "Время",
                                    style = textfieldTextStyle
                                )
                                Box(
                                    Modifier
                                        .width(135.dp)
                                        .height(2.dp)
                                        .background(primaryColor)
                                )
                            }
                        }
                    }
                        val animateRotateColorVectorGroup by animateFloatAsState(targetValue =
                        if(groupFlag==false) 0f else 180f,
                            animationSpec = tween(durationMillis = 400)
                        )
                        //анимация подчеркивания в палитре  групп
                        val animateBorderColorGroup by animateDpAsState(targetValue =
                        if(!groupFlag) 300.dp else 250.dp,
                            animationSpec = tween(durationMillis = 400)
                        )
                        //анимация изменения бокса групп
                        val animateGroupBox by animateDpAsState(targetValue = if (!groupFlag) 50.dp else 210.dp
                            , animationSpec = tween(durationMillis = 400)
                        )
                        Box(modifier = Modifier
                            .padding(top = 25.dp)
                            .width(300.dp)
                            .height(animateGroupBox)
                            .border(
                                width = if (groupFlag == true) 2.dp else 0.dp,
                                color = if (groupFlag == true) primaryColor else backgroundColor,
                                shape = RoundedCornerShape(4)
                            ), contentAlignment = Alignment.TopCenter){
                            Column {
                                Box(
                                    Modifier
                                        .height(50.dp)
                                        .clickable {
                                            if (groupFlag) groupFlag = false else groupFlag = true
                                        }, contentAlignment = Alignment.Center){
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Row(modifier = Modifier
                                            .padding(start = 25.dp, end = 25.dp)
                                            .height(48.dp)
                                            , verticalAlignment = Alignment.CenterVertically){
                                            (if(newGroup<0) "Группа" else listGroup[newGroup].name)?.let {
                                                Text(text = it, style = textfieldTextStyle)
                                            }

                                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd){
                                                Box(
                                                    modifier = Modifier
                                                        .padding(start = 15.dp)
                                                        .width(25.dp)
                                                        .height(25.dp)
                                                        .background(
                                                            if (newGroup >= 0) parseColor(
                                                                listGroup[newGroup ].color
                                                            ) else backgroundColor,
                                                            shape = RoundedCornerShape(4)
                                                        ), contentAlignment = Alignment.Center
                                                ){
                                                    Icon(painter = painterResource(Res.drawable.vector),
                                                        tint = primaryColor
                                                        , contentDescription ="",
                                                        modifier = Modifier
                                                            .scale(0.7f)
                                                            .rotate(animateRotateColorVectorGroup))
                                                }

                                            }
                                        }

                                        Box(
                                            Modifier
                                                .width(animateBorderColorGroup)
                                                .height(2.dp)
                                                .background(primaryColor))
                                    }

                                }
                                if(groupFlag){
                                    LazyColumn(Modifier.height(160.dp)) {
                                        items(listGroup){
                                                group-> Box(
                                            Modifier
                                                .height(50.dp)
                                                .clickable {
                                                    if (newGroup == group.uid) {
                                                        newGroup = -1
                                                    } else {
                                                        newGroup = group.uid!!
                                                    }
                                                    groupFlag = false
                                                }, contentAlignment = Alignment.Center){
                                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                                Row(modifier = Modifier
                                                    .padding(start = 25.dp, end = 25.dp)
                                                    .height(48.dp)
                                                    , verticalAlignment = Alignment.CenterVertically){
                                                    group.name?.let {
                                                        Text(text = it, style = TextStyle(
                                                            fontSize = 20.sp,
                                                            fontWeight = FontWeight.Bold,
                                                            color = primaryColor
                                                        ))
                                                    }
                                                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.CenterEnd){
                                                        Box(
                                                            Modifier
                                                                .width(25.dp)
                                                                .height(25.dp)
                                                                .background(
                                                                    parseColor(group.color),
                                                                    shape = RoundedCornerShape(4)
                                                                ),
                                                            contentAlignment = Alignment.Center){
                                                            if(newGroup==group.uid){
                                                                Icon(painter = painterResource(Res.drawable.ok),
                                                                    contentDescription = "",
                                                                    tint = primaryColor)
                                                            }
                                                        }
                                                    }
                                                }
                                                Box(
                                                    Modifier
                                                        .width(animateBorderColorGroup)
                                                        .height(2.dp)
                                                        .background(primaryColor))
                                            }

                                        }

                                        }
                                    }
                                }



                            }

                        }
                    Row(Modifier.padding(top=25.dp), verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            Modifier
                                .padding(end = 10.dp)
                                .width(140.dp)
                                .height(50.dp)
                                .background(
                                    primaryColor,
                                    shape = RoundedCornerShape(10)
                                )
                                .clickable {
                                    newTask= Task(task.uid,newName,newStart,newEnd,newGroup)
                                    onBack(newTask)
                                },
                            contentAlignment = Alignment.Center){
                            Text(text = "Удалить", style = titleStyle)

                        }
                        Box(
                            Modifier
                                .padding(start = 10.dp)
                                .width(140.dp)
                                .height(50.dp)
                                .background(
                                    primaryColor,
                                    shape = RoundedCornerShape(10)
                                )
                                .clickable {
                                    if (newName != "" && newStart != 0L && newEnd != 0L && newGroup > 0) {
                                        newTask= Task(task.uid,newName,newStart,newEnd,newGroup)
                                        onSave(newTask)
                                    }
                                },
                            contentAlignment = Alignment.Center){
                            Text(text = "Сохранить", style = titleStyle)

                        }
                    }


                }

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

