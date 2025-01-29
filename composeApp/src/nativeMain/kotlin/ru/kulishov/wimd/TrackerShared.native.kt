package ru.kulishov.wimd
//##################################################################################################
//##################################################################################################
//#####################               WIMD - TrackerShared IOS               #######################
//##################################################################################################
//####  Author:Kulishov I.V.                         ###############################################
//####  Version:0.1.0                                ###############################################
//####  Date:28.01.2025                              ###############################################
//##################################################################################################
//##################################################################################################

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle

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

}

@Composable
actual fun TimePickerModal(onConfirm: (DateAndTimeS) -> Unit, onDismiss: () -> Unit) {
}
//=====================================================================================
//getSystemTime (IOS)
//Output values:
//              time:Long - currentTime
//=====================================================================================

actual fun getSystemTime(): Long {
    TODO("Not yet implemented")
}

//=====================================================================================
//timeconverter (IOS)
//Input values:
//              time:Long - current time
//              styleLabel:TextStyle - label style
//=====================================================================================
@Composable
actual fun timeconverter(time: Long, styleLabel: TextStyle) {
}