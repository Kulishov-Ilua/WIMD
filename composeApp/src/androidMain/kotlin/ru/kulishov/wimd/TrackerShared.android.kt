package ru.kulishov.wimd
//##################################################################################################
//##################################################################################################
//#####################             WIMD - TrackerShared Android             #######################
//##################################################################################################
//####  Author:Kulishov I.V.                         ###############################################
//####  Version:0.1.0                                ###############################################
//####  Date:28.01.2025                              ###############################################
//##################################################################################################
//##################################################################################################

import android.icu.util.Calendar
import android.icu.util.TimeZone
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimeInput
import androidx.compose.material3.TimePickerDefaults
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

//=====================================================================================
//DPicker
//Input values:
//              onDateSelected:(Long?) -> Unit - date transfer
//              onDismiss:() -> Unit - exit
//=====================================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DPicker(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                onDateSelected(datePickerState.selectedDateMillis)
                onDismiss()
            }) {
                Text("Ок", style = MaterialTheme.typography.titleMedium)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Отмена", style = MaterialTheme.typography.titleMedium)
            }
        }
        , modifier = Modifier.scale(0.8f)) {
        DatePicker(state = datePickerState,
            colors = DatePickerDefaults.colors(
                containerColor = MaterialTheme.colorScheme.background,
                titleContentColor = MaterialTheme.colorScheme.primary,
                headlineContentColor = MaterialTheme.colorScheme.primary,
                weekdayContentColor = MaterialTheme.colorScheme.primary,
                yearContentColor = MaterialTheme.colorScheme.primary,
                dayContentColor = MaterialTheme.colorScheme.primary,
                currentYearContentColor = MaterialTheme.colorScheme.primary,
                navigationContentColor = MaterialTheme.colorScheme.primary,

                )
        )
    }
}
//=====================================================================================
//Окно выбора времени
//=====================================================================================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun inputTime(
    onConfirm: (TimePickerState) -> Unit,
    onDismiss: () -> Unit,
) {

    val timeZone = TimeZone.getDefault()
    val currentTime = Calendar.getInstance(timeZone)


    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )
    //timePickerState.hour+=3
    Box(modifier = Modifier
        .padding(top = 25.dp)
        .width(300.dp)
        .height(210.dp)
        .clip(shape = RoundedCornerShape(4))
        .shadow(elevation = 5.dp)
        .background(MaterialTheme.colorScheme.background)
        .border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.background,
        ), contentAlignment = Alignment.Center){
        Column {
            TimeInput(
                state = timePickerState,
                colors = TimePickerDefaults.colors(
                    clockDialColor =  MaterialTheme.colorScheme.background,
                    selectorColor = MaterialTheme.colorScheme.background,
                    containerColor = MaterialTheme.colorScheme.background,
                    periodSelectorSelectedContentColor = MaterialTheme.colorScheme.background,
                    periodSelectorBorderColor =  MaterialTheme.colorScheme.background,
                    periodSelectorUnselectedContentColor = MaterialTheme.colorScheme.background,
                    timeSelectorSelectedContentColor = MaterialTheme.colorScheme.primary,
                    timeSelectorUnselectedContentColor = MaterialTheme.colorScheme.primary,
                    clockDialSelectedContentColor =  MaterialTheme.colorScheme.primary,
                    clockDialUnselectedContentColor =  MaterialTheme.colorScheme.primary,
                    periodSelectorSelectedContainerColor =  MaterialTheme.colorScheme.background,
                    periodSelectorUnselectedContainerColor =  MaterialTheme.colorScheme.background,
                    timeSelectorSelectedContainerColor =  MaterialTheme.colorScheme.background,
                    timeSelectorUnselectedContainerColor =  MaterialTheme.colorScheme.background,
                ),
                modifier = Modifier
                    .scale(0.8f)
            )
            Row() {
                Box(
                    Modifier
                        .padding(end = 10.dp)
                        .width(100.dp)
                        .height(50.dp)
                        .clip(shape = RoundedCornerShape(4))
                        .clickable {
                            onDismiss()
                        }
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(4)
                        ), contentAlignment = Alignment.Center){
                    Text(text = "Отмена", style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.background
                    )
                    )
                }
                Box(
                    Modifier
                        .padding(start = 10.dp)
                        .width(100.dp)
                        .height(50.dp)
                        .clip(shape = RoundedCornerShape(4))
                        .clickable {
                            onConfirm(timePickerState)
                        }
                        .background(
                            MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(4)
                        ), contentAlignment = Alignment.Center){
                    Text(text = "Ок", style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.background
                    ))
                }
            }

        }
    }

}


//=====================================================================================
//DatePickerShared (Realization)
//Input values:
//              onDateSelected:(Long?) -> Unit - date transfer
//              onDismiss:() -> Unit - exit
//=====================================================================================
@Composable
actual fun DatePickerShared(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit
) {
    DPicker({date->val pravka = date?.plus(60000)
        onDateSelected(pravka)},{onDismiss() })
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
actual fun TimePickerModal(onConfirm: (DateAndTimeS) -> Unit, onDismiss: () -> Unit) {
    inputTime({
        time->
        val transfer = DateAndTimeS(0,0,0,time.hour,time.minute,0)
        onConfirm(transfer)
    },{
      onDismiss()
    })
}

//=====================================================================================
//getDayStartAndEnd
//Input values:
//              date: Calendar -
//Output values:
//              ret:Pair<Long, Long> -
//=====================================================================================

fun getDayStartAndEnd(date: Calendar = Calendar.getInstance(TimeZone.GMT_ZONE)): Pair<Long, Long> {
    println(date.time)
    val calendar = date.clone() as Calendar
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    val dayStart = calendar.timeInMillis // 1000

    calendar.set(Calendar.HOUR_OF_DAY, 23)
    calendar.set(Calendar.MINUTE, 59)
    calendar.set(Calendar.SECOND, 59)
    calendar.set(Calendar.MILLISECOND, 999)
    val dayEnd = calendar.timeInMillis // 1000
    println(Pair(dayStart, dayEnd))

    return Pair(dayStart, dayEnd)
}
//=====================================================================================
//getSystemTime (Android)
//Output values:
//              time:Long - currentTime
//=====================================================================================
actual fun getSystemTime(): Long {
    return System.currentTimeMillis()
}

//=====================================================================================
//timeconverter (Android)
//Input values:
//              time:Long - current time
//              styleLabel:TextStyle - label style
//=====================================================================================
@Composable
actual fun timeconverter(time: Long, styleLabel: TextStyle) {

    val min = (time/1000%3600)/60
    val sec = time/1000%60
    val hour = time / 3600000
    var ret =""
    ret = "%02d:%02d:%02d".format(hour,min,sec)
    androidx.compose.material.Text(
        ret, style = styleLabel
    )
}

