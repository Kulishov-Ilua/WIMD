package ru.kulishov.wimd

//##################################################################################################
//##################################################################################################
//#####################                WIMD - ComponentShared                #######################
//##################################################################################################
//####  Author:Kulishov I.V.                         ###############################################
//####  Version:0.1.0                                ###############################################
//####  Date:24.01.2025                              ###############################################
//##################################################################################################
//##################################################################################################


import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp

//=====================================================================================
//NavDoubleButtom
//Input values:
//              startState:Boolean
//              onButState:() -> Unit
//              first:String - first element
//              second:String - second element
//               titleStyle:TextStyle -
//               backgroundColor:Color -
//               primaryColor:Color -
//=====================================================================================
@Composable
fun NavDoubleButtom(startState:Boolean, onButState:(Boolean) -> Unit, first:String, second:String, titleStyle: TextStyle, backgroundColor: Color,primaryColor:Color){
    var animateBackground = animateDpAsState(
        targetValue = if(startState) 0.dp else 120.dp
    )
    Box(
        Modifier.width(240.dp).pointerInput(Unit){
        detectHorizontalDragGestures{
                change,dragAmount ->
            change.consume()
            if(dragAmount>0){
                if(startState==true){
                    onButState(false)
                }
            }else{
                if(startState==false) {
                    onButState(true)
                }
            }
        }
    }, contentAlignment = Alignment.CenterStart
    ){
        // Background box that moves
        Box(
            Modifier
                .padding(start = animateBackground.value)
                .height(40.dp)
                .width(120.dp)
                .background(primaryColor,
                    RoundedCornerShape(10.dp)
                )

        )
        Row(verticalAlignment = Alignment.CenterVertically){
            Box(Modifier.width(120.dp).height(40.dp), contentAlignment = Alignment.Center) {
                Text(first, style = TextStyle(
                    fontWeight = titleStyle.fontWeight,
                    fontSize = titleStyle.fontSize,
                    color = if (startState)backgroundColor else primaryColor
                ), modifier = Modifier.padding(10.dp).clickable {
                    if (startState == false) {
                        onButState(true)
                    }

                })
            }
            Box(Modifier.width(120.dp).height(40.dp), contentAlignment = Alignment.Center) {
                Text(second, style = TextStyle(
                    fontWeight = titleStyle.fontWeight,
                    fontSize = titleStyle.fontSize,
                    color = if (!startState) backgroundColor else primaryColor
                ), modifier = Modifier.padding(10.dp).clickable {
                    if (startState == true) {
                        onButState(false)
                    }

                })
            }
        }

    }
}

//=====================================================================================
//intDataFormatTransformation
//Input values:
//              value:Int - input
//Output values:
//              output:String - output
//=====================================================================================
fun intDataFormatTransformation(value:Int):String{
    var ret =value.toString()
    if(ret.length<2){
        ret = "0"
        ret+=value
    }
    return ret
}
