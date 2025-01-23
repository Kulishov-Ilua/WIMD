package ru.kulishov.wimd

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material.Text
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview

//##################################################################################################
//##################################################################################################
//#####################                     WIMD Tracker                     #######################
//##################################################################################################
//####  Author:Kulishov I.V.                         ###############################################
//####  Version:0.1.0                                ###############################################
//####  Date:21.01.2025                              ###############################################
//##################################################################################################
//##################################################################################################


//=====================================================================================
//TrackerBlock
//Input values:
//              backgroundColor:Color - Background color
//              primaryColor:Color - primary color
//              typographyLabel:TextStyle - typographyLabel
//              buttonLabel:TextStyle - buttonLabel
//              stateHeight:Boolean - tracker height state
//=====================================================================================
@Composable
fun TrackerBlock(backgroundColor: Color, primaryColor: Color, typographyLabel: TextStyle, buttonLabel: TextStyle, stateHeight:Boolean){

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
        targetValue = if(!isTrackerRunning|| isTrackerRunning&&stateHeight) 1f
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
            val startTime = System.currentTimeMillis()
            timerStartTime.value=startTime

            while (isTrackerRunning) {
                currentTime = System.currentTimeMillis() - startTime
                delay(10L)
            }
        }
    }

    //=====================================================================================
    //Функция остановки секундомера
    //=====================================================================================
    fun stop() {
        var timerEndTime = System.currentTimeMillis()
        timerStartTime.value+=10800000L
        timerEndTime+=10800000L
        isTrackerRunning=false
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
                            color = MaterialTheme.colorScheme.background
                        ))
                    }
                }
                if(stopButtonInRowAnimate.value>0f){
                    Button(onClick ={stop()},
                        shape = RoundedCornerShape(10),
                        modifier = Modifier.padding(start=20.dp).width(150.dp).height(50.dp).alpha(stopButtonInRowAnimate.value)
                        , colors = ButtonColors(primaryColor,backgroundColor,primaryColor,backgroundColor)
                    ) {
                        Text("Стоп", style = TextStyle(
                            fontSize = buttonLabel.fontSize,
                            fontWeight = buttonLabel.fontWeight,
                            color = MaterialTheme.colorScheme.primary))
                    }
                }

            }
            if(stateHeight||!stateHeight&&!isTrackerRunning){
                Row(modifier = Modifier.padding(top=if(stateHeight)30.dp else 0.dp), verticalAlignment = Alignment.CenterVertically) {
                    if(createButtonAlpha.value>0f){
                        Button(onClick ={},
                            shape = RoundedCornerShape(10),
                            modifier = Modifier.padding(end = 15.dp).width(150.dp).height(50.dp).alpha(createButtonAlpha.value)
                           , colors = ButtonColors(primaryColor,backgroundColor,primaryColor,backgroundColor)
                        ) {
                            Text("Создать", style = TextStyle(
                                fontSize = buttonLabel.fontSize,
                                fontWeight = buttonLabel.fontWeight,
                                color = MaterialTheme.colorScheme.primary)
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
                            , colors = ButtonColors(primaryColor,backgroundColor,primaryColor,backgroundColor)
                        ) {
                            Text(if(isTrackerRunning) "Стоп" else "Старт", style = TextStyle(
                                fontSize = buttonLabel.fontSize,
                                fontWeight = buttonLabel.fontWeight,
                                color = MaterialTheme.colorScheme.primary))
                        }
                    }

                }
            }
        }
    }

}

//=====================================================================================
//timeconverter
//Input values:
//              time:Long - time
//              styleLabel:TextStyle - label style
//=====================================================================================

@Composable
fun timeconverter(time:Long,styleLabel: TextStyle){

    val min = (time/1000%3600)/60
    val sec = time/1000%60
    val hour = time / 3600000
    var ret =""
    ret = "%02d:%02d:%02d".format(hour,min,sec)
    Text(ret, style = styleLabel
    )
}

@Preview
@Composable
fun TrackerTestScreen(){
    var stateTrackerApp by remember { mutableStateOf(0) }
    bottomIslandScreen(stateTrackerApp,{di-> stateTrackerApp=di},{
        Box(Modifier.fillMaxSize().background(color = androidx.compose.material3.MaterialTheme.colorScheme.background)
            , contentAlignment = Alignment.TopCenter){
            var dd by remember { mutableStateOf(true) }

            Row{
                Box(Modifier.padding(top=25.dp)){
                    NavDoubleButtom(dd,{d -> dd=d},"first","second")
                }

            }
        }
    },{
        TrackerBlock(androidx.compose.material3.MaterialTheme.colorScheme.primary,androidx.compose.material3.MaterialTheme.colorScheme.background,
            androidx.compose.material3.MaterialTheme.typography.titleLarge,androidx.compose.material3.MaterialTheme.typography.titleMedium,stateTrackerApp==0)

    }, androidx.compose.material3.MaterialTheme.colorScheme.primary, androidx.compose.material3.MaterialTheme.colorScheme.background)


}