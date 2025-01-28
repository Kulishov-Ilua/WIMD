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
import androidx.compose.ui.draw.scale
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
    val currentTime = Calendar.getInstance()

    val timePickerState = rememberTimePickerState(
        initialHour = currentTime.get(Calendar.HOUR_OF_DAY),
        initialMinute = currentTime.get(Calendar.MINUTE),
        is24Hour = true,
    )
    Box(modifier = Modifier
        .padding(top = 25.dp)
        .width(300.dp)
        .height(210.dp)
        .border(
            width = 2.dp,
            color = MaterialTheme.colorScheme.background,
            shape = RoundedCornerShape(4)
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
                        .clickable {
                            onDismiss
                        }
                        .background(
                            MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(4)
                        ), contentAlignment = Alignment.Center){
                    Text(text = "Отмена", style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    )
                }
                Box(
                    Modifier
                        .padding(start = 10.dp)
                        .width(100.dp)
                        .height(50.dp)
                        .clickable {
                            onConfirm(timePickerState)
                        }
                        .background(
                            MaterialTheme.colorScheme.background,
                            shape = RoundedCornerShape(4)
                        ), contentAlignment = Alignment.Center){
                    Text(text = "Ок", style = TextStyle(
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
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
    DPicker({date->val pravka = date?.plus(600000)
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