package ru.kulishov.wimd
//##################################################################################################
//##################################################################################################
//#####################                 WIMD - TrackerShared                 #######################
//##################################################################################################
//####  Author:Kulishov I.V.                         ###############################################
//####  Version:0.1.0                                ###############################################
//####  Date:27.01.2025                              ###############################################
//##################################################################################################
//##################################################################################################

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import wimd.composeapp.generated.resources.Res
import wimd.composeapp.generated.resources.ok
import wimd.composeapp.generated.resources.vector

//=====================================================================================
//taskGroupScreen
//Input values:
//              listTask:List<Task> -
//              listGroupTask: List<GroupTask> -
//              redactTask: (Boolean,Task) -> Unit
//              redactGroup: (Boolean, GroupTask) -> Unit
//              backgroundColor:Color - Background color
//              primaryColor:Color - primary color
//              titleMedium:TextStyle -
//              bodyMedium:TextStyle -
//              titleLarge:TextStyle -
//=====================================================================================
@Composable
fun TrackerScreen(listTask:List<Task>, listGroupTask: List<GroupTask>, redactTask: (Boolean,Task) -> Unit, redactGroup: (Boolean, GroupTask) -> Unit, backgroundColor: Color,primaryColor: Color,
                  titleMedium:TextStyle,bodyMedium:TextStyle,titleLarge:TextStyle){
    //--------------------------------------------------------------
    //Состояния экрана трекера:
    //      0,1 -> Блок трекера, списки задач и групп
    //      2 -> Блок выбора создания, списки задач и групп
    //      3 -> Блок создания
    //      4 -> Блок создания группы
    //--------------------------------------------------------------
    var stateTrackerApp by remember { mutableStateOf(0) }
    var taskTransfer by remember { mutableStateOf(Task(null,"",0L,0L, -1)) }
    var groupTransfer by remember { mutableStateOf(GroupTask(null,"","")) }

    bottomIslandScreen(stateTrackerApp,{di-> stateTrackerApp=di},{
        Box(Modifier.fillMaxSize().background(color = backgroundColor)
            , contentAlignment = Alignment.TopCenter){
            Box(Modifier.padding(top=25.dp)){
                taskGroupScreen(
                    listTask, listGroupTask,{ group ->
                        groupTransfer=group
                        stateTrackerApp=4
                    },{
                            task->
                        taskTransfer=task
                        stateTrackerApp=3
                    },primaryColor,backgroundColor,
                    titleMedium,bodyMedium)
            }


        }
    },{
        when(stateTrackerApp){
            0,1 ->  TrackerBlock(primaryColor,backgroundColor,
               titleLarge,titleMedium,stateTrackerApp==0,{
                    stateTrackerApp=2
                },{
                        start,stop-> taskTransfer.start=start
                    taskTransfer.endTime=stop
                    stateTrackerApp=3
                })
            2 -> chooseCreateBlock({
                groupTransfer=GroupTask(null,"","")
                stateTrackerApp=4},{
                taskTransfer=Task(null,"",0L,0L, -1)
                stateTrackerApp=3}, titleLarge,backgroundColor,primaryColor)
            3 -> createTaskBlock(taskTransfer,
                {task ->
                    redactTask(true,task)
                    stateTrackerApp=0
                },
                {
                        task -> redactTask(false,task)
                    stateTrackerApp=0
                },backgroundColor,primaryColor,titleMedium,
                titleMedium)
            4 -> createGroupBlock(groupTransfer,{group-> redactGroup(true,group)
                stateTrackerApp=0},{group-> redactGroup(false,group)
                stateTrackerApp=0},backgroundColor,primaryColor,titleMedium,
                titleMedium)
        }

    }, primaryColor, backgroundColor)


}


//=====================================================================================
//TrackerBlock
//Input values:
//              backgroundColor:Color - Background color
//              primaryColor:Color - primary color
//              typographyLabel:TextStyle - typographyLabel
//              buttonLabel:TextStyle - buttonLabel
//              stateHeight:Boolean - tracker height state
//              onCreate:()->Unit - button create unit
//              onStop:(Long,Long)->Unit -
//=====================================================================================
@Composable
fun TrackerBlock(backgroundColor: Color, primaryColor: Color, typographyLabel: TextStyle, buttonLabel: TextStyle, stateHeight:Boolean,onCreate:()->Unit,onStop:(Long,Long)->Unit){

    var currentTime by remember { mutableStateOf(0L) }
    var timerStartTime = remember { mutableStateOf(0L) }
    //флаг запуска трекера
    var isTrackerRunning by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    //--------------------------------------------------------------------------
    // Анимация секундомера:
    //      Уменьшить размер - если изменился размер острова и трекер запущен
    //      Скрыть - если уменьшился размер острова и трекер не запущен
    //-------------------------------------------------------------------------
    val delayTimeLabel = 300 //задержка при появлении сбоку кнопки стоп
    var timeLabelSize = animateIntAsState(
        targetValue = if(!stateHeight&&isTrackerRunning) typographyLabel.fontSize.value.toInt()-2
        else typographyLabel.fontSize.value.toInt()
        , animationSpec = tween(durationMillis = delayTimeLabel, delayMillis =
        if(!stateHeight&&isTrackerRunning) 0 else delayTimeLabel)
    )
    var timeLabelAlpha = animateFloatAsState(
        targetValue = if(!stateHeight&&!isTrackerRunning) 0f
        else 1f,
        animationSpec = tween(delayMillis = if(stateHeight||isTrackerRunning) delayTimeLabel else 0)
    )
    //--------------------------------------------------------------------------
    // Анимация кнопки стоп если остров узкий:
    //      Показать - если изменился размер острова и трекер запущен
    //-------------------------------------------------------------------------
    var stopButtonInRowAnimate = animateFloatAsState(
        targetValue = if(!stateHeight&&isTrackerRunning) 1f
        else 0f
        , animationSpec = tween(durationMillis = delayTimeLabel, delayMillis =
        if(!stateHeight&&isTrackerRunning) delayTimeLabel else 0)

    )
    //--------------------------------------------------------------------------
    // Анимация кнопки стоп если остров высокий:
    //      Показать - когда трекер не запущен или запущен и большой остров
    //      Увеличить - если запустился трекер и остров широкий
    //-------------------------------------------------------------------------
    val delayButtonDown = 300 //задержка на ожидание исчезновение 2 кнопки в нижнем ряду
    var stopButtonAlpha = animateFloatAsState(
        targetValue = if(!isTrackerRunning || isTrackerRunning&&stateHeight) 1f
        else 0f
    )
    var stopButtonWidth = animateDpAsState(
        targetValue = if(isTrackerRunning&&stateHeight) 280.dp
        else 150.dp
        , animationSpec = tween(delayMillis = if(isTrackerRunning&&stateHeight) 0/*delayButtonDown*/ else 0, durationMillis = delayButtonDown)
    )
    //--------------------------------------------------------------------------
    // Анимация кнопки создать если трекер запустился:
    //      Скрыть - когда трекер запущен
    //-------------------------------------------------------------------------
    var createButtonAlpha = animateFloatAsState(
        targetValue = if(!isTrackerRunning) 1f
        else 0f
        , animationSpec = tween(delayMillis = if(!isTrackerRunning )delayButtonDown else 0, durationMillis = delayButtonDown)
    )
    //=====================================================================================
    //Функция запуска секундомера
    //=====================================================================================
    fun start() {
        if (isTrackerRunning) return
        isTrackerRunning = true
        coroutineScope.launch {
            val startTime = getSystemTime()
            timerStartTime.value=startTime

            while (isTrackerRunning) {
                currentTime = getSystemTime() - startTime
                delay(10L)
            }
        }
    }

    //=====================================================================================
    //Функция остановки секундомера
    //=====================================================================================
    fun stop() {
        var timerEndTime = getSystemTime()
        timerStartTime.value+=10800000L
        timerEndTime+=10800000L
        isTrackerRunning=false
        onStop(timerStartTime.value,timerEndTime)
        timerStartTime.value=0L
        currentTime=0L
    }
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(verticalAlignment = Alignment.CenterVertically){
                if(timeLabelAlpha.value>0f){
                    Box(Modifier.alpha(timeLabelAlpha.value)){
                        timeconverter(currentTime,TextStyle(
                            fontWeight = typographyLabel.fontWeight,
                            fontSize = timeLabelSize.value.sp,
                            color = primaryColor
                        ))
                    }
                }
                if(stopButtonInRowAnimate.value>0f){
                    Button(onClick ={stop()},
                        shape = RoundedCornerShape(10),
                        modifier = Modifier.padding(start=20.dp).width(150.dp).height(50.dp).alpha(stopButtonInRowAnimate.value)
                        , colors = ButtonDefaults.buttonColors(primaryColor,backgroundColor,primaryColor,backgroundColor)
                    ) {
                        Text("Стоп", style = TextStyle(
                            fontSize = buttonLabel.fontSize,
                            fontWeight = buttonLabel.fontWeight,
                            color = backgroundColor))
                    }
                }

            }
            if(!isTrackerRunning||isTrackerRunning&&stateHeight){
                Row(modifier = Modifier.padding(top=if(stateHeight)30.dp else 0.dp), verticalAlignment = Alignment.CenterVertically) {
                    if(createButtonAlpha.value>0f){
                        Button(onClick ={
                            onCreate()
                        },
                            shape = RoundedCornerShape(10),
                            modifier = Modifier.padding(end = 15.dp).width(150.dp).height(50.dp).alpha(createButtonAlpha.value)
                            , colors = ButtonDefaults.buttonColors(primaryColor,backgroundColor,primaryColor,backgroundColor)
                        ) {
                            Text("Создать", style = TextStyle(
                                fontSize = buttonLabel.fontSize,
                                fontWeight = buttonLabel.fontWeight,
                                color = backgroundColor)
                            )
                        }
                    }

                    if(stopButtonAlpha.value>0f){
                        Button(onClick ={
                            if(isTrackerRunning) stop()
                            else start()
                        },
                            shape = RoundedCornerShape(10),
                            modifier = Modifier.padding(start = 15.dp).width(stopButtonWidth.value).height(50.dp).alpha(stopButtonAlpha.value)
                            , colors = ButtonDefaults.buttonColors(primaryColor,backgroundColor,primaryColor,backgroundColor)
                        ) {
                            Text(if(isTrackerRunning) "Стоп" else "Старт", style = TextStyle(
                                fontSize = buttonLabel.fontSize,
                                fontWeight = buttonLabel.fontWeight,
                                color = backgroundColor))
                        }
                    }

                }
            }
        }
    }

}


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
//              primaryColor:Color -
//              backgroundColor:Color -
//              titleStyle:TextStyle -
//              bodyStyle:TextStyle -
//=====================================================================================
@Preview
@Composable
fun createTaskBlock(task:Task,onSave: (Task) -> Unit, onBack:(Task) -> Unit,primaryColor:Color,backgroundColor:Color, titleStyle: TextStyle,bodyStyle: TextStyle){
    var newTask by remember { mutableStateOf(task) }
    var newName by remember { mutableStateOf(task.name!!) }
    var newStart by remember { mutableStateOf(task.start) }
    var newEnd by remember { mutableStateOf(task.endTime) }
    var newGroup by remember { mutableStateOf(task.groupID) }
    var newStartFormated by remember { mutableStateOf(DateAndTimeS(0,0,0,0,0,0)) }
    var newEndFormated by remember { mutableStateOf(DateAndTimeS(0,0,0,0,0,0)) }
    newStartFormated.convertUnixTimeToDate1(newStart)
    newEndFormated.convertUnixTimeToDate1(newEnd)

    //--------------------------------------------------------------
    //Состояния окон даты и времени:
    //      0 -> закрыты
    //      1 -> Окно выбора даты (Старт)
    //      2 -> Окно выбора времени (Старт)
    //--------------------------------------------------------------
    var pickersState by remember { mutableStateOf(0) }
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
        focusedIndicatorColor = Color.Transparent,
        disabledIndicatorColor = Color.Transparent
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
                                .clickable {
                                    pickersState=1
                                    },
                            contentAlignment = Alignment.Center){
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (newStart > 0L) "${intDataFormatTransformation( newStartFormated.day)}/${intDataFormatTransformation( newStartFormated.month)}/${intDataFormatTransformation( newStartFormated.year)}" else "Дата",
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
                                .clickable {
                                    pickersState=2
                                },
                            contentAlignment = Alignment.Center){
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (newStart>0L) "${intDataFormatTransformation( newStartFormated.hour)}:${intDataFormatTransformation(newStartFormated.minute)}" else "Время",
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
                                    pickersState=3
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (newEnd > 0L) "${intDataFormatTransformation(newEndFormated.day)}/${intDataFormatTransformation(newEndFormated.month)}/${intDataFormatTransformation(newEndFormated.year)}" else "Дата",
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
                                    pickersState=4
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = if (newEnd > 0L) "${intDataFormatTransformation(newEndFormated.hour)}:${intDataFormatTransformation(newEndFormated.minute)}" else "Время",
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
                                            (if(newGroup<0) "Группа" else listGroup.value[newGroup].name)?.let {
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
                                                                listGroup.value[newGroup].color
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
                                        items(listGroup.value){
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
                            Text(text = "Удалить", style = TextStyle(
                                fontSize = titleTextStyle.fontSize,
                                fontWeight = titleTextStyle.fontWeight,
                                fontFamily = titleTextStyle.fontFamily,
                                color = backgroundColor
                            ))

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
                                    if (newName != "" && newStart != 0L && newEnd != 0L && newGroup > -1) {
                                        newTask= Task(task.uid,newName,newStart,newEnd,newGroup)
                                        onSave(newTask)
                                    }
                                },
                            contentAlignment = Alignment.Center){
                            Text(text = "Сохранить", style = TextStyle(
                                fontSize = titleTextStyle.fontSize,
                                fontWeight = titleTextStyle.fontWeight,
                                fontFamily = titleTextStyle.fontFamily,
                                color = backgroundColor
                            ))

                        }
                    }


                }

            }
        }
    }
    if(pickersState>0){
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter){
            when(pickersState){
                1-> DatePickerShared({ date->newStart=date!! },{pickersState=0})
                2->TimePickerModal({
                        time->
                    var timeF = DateAndTimeS(0,0,0,0,0,0)
                    timeF.convertUnixTimeToDate1(newStart)
                    timeF.hour=time.hour
                    timeF.minute=time.minute
                    timeF.second=0
                    newStart=timeF.convertToSeconds(timeF)*1000
                    pickersState=0
                },{
                    pickersState=0
                })

                3-> DatePickerShared({ date->newEnd=date!! },{pickersState=0})
                4->TimePickerModal({
                        time->
                    var timeF = DateAndTimeS(0,0,0,0,0,0)
                    timeF.convertUnixTimeToDate1(newEnd)
                    timeF.hour=time.hour
                    timeF.minute=time.minute
                    timeF.second=0
                    newEnd=timeF.convertToSeconds(timeF)*1000
                    pickersState=0
                },{
                    pickersState=0
                })
            }

        }
    }
}


//=====================================================================================
//createGroupBlock
//Input values:
//              group:GroupTask - group
//              onSave: (GroupTask) -> Unit -
//              onBack: (GroupTask) -> Unit -
//              primaryColor:Color -
//              backgroundColor:Color -
//              titleStyle:TextStyle -
//              bodyStyle:TextStyle -
//=====================================================================================

@Composable
fun createGroupBlock(group:GroupTask,onSave: (GroupTask) -> Unit, onBack:(GroupTask) -> Unit, primaryColor:Color,backgroundColor:Color, titleStyle: TextStyle,bodyStyle: TextStyle){
    var newGroup by remember { mutableStateOf(GroupTask(group.uid, group.name,group.color)) }
    var newNawe by remember { mutableStateOf(group.name!!)  }
    var newColor by remember{ mutableStateOf(group.color) }

    var colorFlag by remember { mutableStateOf(false) }

    val titleTextStyle = TextStyle(
        fontSize = titleStyle.fontSize
        , fontWeight = titleStyle.fontWeight,
        fontFamily = titleStyle.fontFamily,
        color = primaryColor
    )
    val bodyTextStyle = TextStyle(
        fontSize = bodyStyle.fontSize,
        fontFamily = bodyStyle.fontFamily,
        fontWeight = bodyStyle.fontWeight,
        color = primaryColor
    )

    val textfieldStyle = TextFieldDefaults.textFieldColors(
        backgroundColor = Color.Transparent,
        textColor = primaryColor,
        disabledTextColor = primaryColor,
        cursorColor = primaryColor,
        unfocusedLabelColor = primaryColor,
        focusedLabelColor = primaryColor,
        disabledLabelColor = primaryColor,
    )

    //анимация открытия палитры(поворота стрелки)
    val animateRotateColorVector by animateFloatAsState(targetValue =
    if(colorFlag) 180f else 0f,
        animationSpec = tween(durationMillis = 300), label = ""
    )
    //анимация подчеркивания в палитре цвета групп
    val animateBorderColor by animateDpAsState(targetValue =
    if(!colorFlag) 300.dp else 250.dp,
        animationSpec = tween(durationMillis = 400)
    )
    LazyColumn {
        item {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = if (group.uid == null) "Создать группу" else "Редактировать группу",
                    style = titleTextStyle
                )
                TextField(
                    value = newNawe,
                    onValueChange = { newNawe = it },
                    label = {
                        Text(
                            text = "Название", style = bodyTextStyle
                        )
                    },
                    singleLine = true,
                    colors = textfieldStyle,
                    modifier = Modifier.padding(top = 25.dp)
                )
                Box(
                    Modifier
                        .width(300.dp)
                        .height(2.dp)
                        .background(primaryColor)
                )
                //анимация изменения бокса палитры
                val animateColorBox by animateDpAsState(
                    targetValue = if (!colorFlag) 50.dp else 210.dp,
                    animationSpec = tween(durationMillis = 400)
                )
                Box(
                    modifier = Modifier
                        .padding(top = 25.dp)
                        .width(300.dp)
                        .height(animateColorBox)
                        .border(
                            width = if (colorFlag) 2.dp else 0.dp,
                            color = if (colorFlag) primaryColor else backgroundColor,
                            shape = RoundedCornerShape(4)
                        ), contentAlignment = Alignment.TopCenter
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            Modifier
                                .height(50.dp)
                                .clickable {
                                    colorFlag = !colorFlag
                                }, contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Row(
                                    modifier = Modifier
                                        .padding(start = 25.dp, end = 25.dp)
                                        .height(48.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(text = "Цвет", style = bodyTextStyle)

                                    Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.CenterEnd
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .padding(start = 15.dp)
                                                .width(25.dp)
                                                .height(25.dp)
                                                .background(
                                                    if (newColor != "" && !colorFlag) parseColor(
                                                        newColor
                                                    ) else backgroundColor,
                                                    shape = RoundedCornerShape(4)
                                                ), contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                painter = painterResource(Res.drawable.vector),
                                                tint = primaryColor, contentDescription = "",
                                                modifier = Modifier
                                                    .scale(0.7f)
                                                    .rotate(animateRotateColorVector)
                                            )
                                        }

                                    }
                                }

                                Box(
                                    Modifier
                                        .width(animateBorderColor)
                                        .height(2.dp)
                                        .background(primaryColor)
                                )
                            }

                        }
                        if (colorFlag) {
                            LazyColumn(Modifier.height(160.dp)) {
                                items(colorList) { color ->
                                    Box(
                                        Modifier
                                            .height(50.dp)
                                            .clickable {
                                                if (color.colorS == newColor) {
                                                    newColor = ""
                                                } else {
                                                    newColor = color.colorS
                                                }
                                                colorFlag = !colorFlag
                                            }, contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Row(
                                                modifier = Modifier
                                                    .padding(start = 25.dp, end = 25.dp)
                                                    .height(48.dp),
                                                verticalAlignment = Alignment.CenterVertically
                                            ) {
                                                Text(text = color.name, style = bodyTextStyle)
                                                Box(
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentAlignment = Alignment.CenterEnd
                                                ) {
                                                    Box(
                                                        Modifier
                                                            .width(25.dp)
                                                            .height(25.dp)
                                                            .background(
                                                                color.color,
                                                                shape = RoundedCornerShape(4)
                                                            ),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        if (newColor == color.colorS) {
                                                            Icon(
                                                                painter = painterResource(Res.drawable.ok),
                                                                contentDescription = "",
                                                                tint = primaryColor
                                                            )
                                                        }
                                                    }
                                                }
                                            }
                                            Box(
                                                Modifier
                                                    .width(animateBorderColor)
                                                    .height(2.dp)
                                                    .background(primaryColor)
                                            )
                                        }

                                    }

                                }
                            }
                        }

                    }
                }
                Row(Modifier.padding(top = 25.dp), verticalAlignment = Alignment.CenterVertically) {
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
                                newGroup = GroupTask(group.uid, newNawe, newColor)
                                onBack(newGroup)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Удалить", style = TextStyle(
                                fontSize = titleTextStyle.fontSize,
                                fontWeight = titleTextStyle.fontWeight,
                                fontFamily = titleTextStyle.fontFamily,
                                color = backgroundColor
                            )
                        )

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
                                newGroup = GroupTask(group.uid, newNawe, newColor)
                                onSave(newGroup)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Сохранить", style = TextStyle(
                                fontSize = titleTextStyle.fontSize,
                                fontWeight = titleTextStyle.fontWeight,
                                fontFamily = titleTextStyle.fontFamily,
                                color = backgroundColor
                            )
                        )

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
fun taskGroupScreen(listTask:List<Task>, listGroupTask: List<GroupTask>,onTapGroup: (GroupTask) -> Unit, onTapTask:(Task) -> Unit, primaryColor:Color,backgroundColor:Color,titleStyle: TextStyle,bodyStyle: TextStyle){
    //-----------------------------------------------------------------------------
    //Состояние отображения:
    //      false -> Группы
    //      true -> Задачи
    //-----------------------------------------------------------------------------
    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter){
        val listState = rememberLazyListState()
        val currentPage by remember { derivedStateOf { listState.firstVisibleItemIndex } }
        var buttonScroll by remember { mutableStateOf(-1) }
        val coroutineScope = rememberCoroutineScope()
        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier =
        Modifier.pointerInput(Unit) {
            detectHorizontalDragGestures { change, dragAmount ->
                change.consume()
                if (dragAmount > 0) {
                    buttonScroll=0
                } else {
                    buttonScroll=1
                }
            }
        }) {


            NavDoubleButtom(currentPage==0&&buttonScroll!=1,{
                rec -> if(rec){
              buttonScroll = 0
            }else{
              buttonScroll=1
            }
            },"Задачи","Группы",titleStyle,backgroundColor,primaryColor)

            //чекаем смещение
            //val currentOffset by remember { derivedStateOf { listState.firstVisibleItemScrollOffset } }
            //var fixOffset by remember { mutableStateOf(0) }
            //println("Current: $currentOffset fix: $fixOffset")

            LaunchedEffect(buttonScroll){
                if(buttonScroll==0){
                    coroutineScope.launch {
                        buttonScroll=-1
                        listState.animateScrollToItem(0)
                        //fixOffset=0
                    }
                }
                if(buttonScroll==1){
                    coroutineScope.launch {
                        buttonScroll=-1
                        listState.animateScrollToItem(1)
                        //fixOffset=1000
                    }
                }

            }
            /*
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
             */
            LazyRow(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                userScrollEnabled = false
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
                               dateTimeStart.convertUnixTimeToDate1(task.start)
                               var dateTimeEnd = DateAndTimeS(0,0,0,0,0,0)
                               dateTimeEnd.convertUnixTimeToDate1(task.endTime)
                               var timet = DateAndTimeS(0,0,0,0,0,0)
                               timet.calculateDifference(dateTimeStart,dateTimeEnd)
                               var tstring=""
                               if (timet.day>0) tstring+=timet.day.toString() + "д."
                               tstring+= timet.hour.toString() + "ч."
                               if(timet.day==0) tstring+= timet.minute.toString() + "м."
                               Box(Modifier.clickable { onTapTask(task) }) {
                                   taskCardTracker(
                                       TaskView(
                                           task.uid!!, task.name!!, dateTimeStart, dateTimeEnd,
                                           parseColor(listGroup.value[task.groupID].color), tstring
                                       ), titleStyle, bodyStyle, primaryColor
                                   )
                               }
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
                                Box(Modifier.clickable { onTapGroup(group) }) {
                                    groupCardTracker(group, titleStyle, primaryColor)
                                }
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

//=====================================================================================
//DatePickerShared
//Input values:
//              onDateSelected:(Long?) -> Unit - date transfer
//              onDismiss:() -> Unit - exit
//=====================================================================================
@Composable
expect fun DatePickerShared(onDateSelected: (Long?) -> Unit,
                            onDismiss: () -> Unit)
//=====================================================================================
//TimePickerModal
//Input values:
//              onConfirm: (DateAndTimeS) -> Unit - time transfer
//              onDismiss:() -> Unit - exit
//=====================================================================================
@Composable
expect fun TimePickerModal(onConfirm: (DateAndTimeS) -> Unit,
                           onDismiss: () -> Unit,)
//=====================================================================================
//getSystemTime
//Output values:
//              time:Long - currentTime
//=====================================================================================
expect fun getSystemTime():Long

@Composable
expect fun timeconverter(time:Long,styleLabel: TextStyle)