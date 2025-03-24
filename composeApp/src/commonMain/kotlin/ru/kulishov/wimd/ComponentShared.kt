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
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectVerticalDragGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import wimd.composeapp.generated.resources.Res
import wimd.composeapp.generated.resources.vector

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


//=====================================================================================
//bottomIslandScreen
//Input values:
//              islandState:Int - islandState
//              islandStateEdit: (Int)-> Unit
//              screen:@Composable () -> Unit - screen
//              islandContent:@Composable () -> Unit - island content
//              islandColor:Color - islandColor
//              primaryColor:Color - primaryColor
//=====================================================================================
@Composable
fun bottomIslandScreen(islandState:Int,stateTable:List<Dp>,islandStateEdit: (Int)-> Unit, screen:@Composable () -> Unit, islandContent:@Composable () -> Unit, islandColor: Color, primaryColor:Color){
    //анимация размера острова
    val islandHeight by animateDpAsState(
        targetValue =if((stateTable.size-1)>islandState&&islandState>=0) stateTable[islandState]
        else if(stateTable.size<1) 100.dp
        else stateTable[stateTable.size-1]
        /*
        if(islandState==0) 200.dp     //
        else if(islandState==1) 125.dp              //
        else if(islandState==2) 350.dp              //
        else if (islandState==3)500.dp              //
        else if (islandState==4)310.dp              //
        else if (islandState==5)480.dp              //
        else 625.dp*/,                                //
        animationSpec = tween(durationMillis= 500, delayMillis = 200), label = ""
    )
    //анимация стрелки на острове
    val rotateAnimate by animateFloatAsState(targetValue = if (islandState == 1) 180F else 0F
        , animationSpec = tween(delayMillis = 300), label = ""
    )

    Box(Modifier.fillMaxSize().background(primaryColor), contentAlignment = Alignment.BottomCenter){
        //Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
        Box(Modifier.padding(bottom = islandHeight).fillMaxSize()){
            screen()
        }
        Box(Modifier
            .clip(RoundedCornerShape(10,10,0,0))
            .fillMaxWidth()
            .height(islandHeight)

            .background(islandColor)
            .pointerInput(Unit){
                detectVerticalDragGestures { change, dragAmount ->
                    change.consume()
                    if(dragAmount>0){
                        if(islandState==0) islandStateEdit(1)
                    }else{
                        islandStateEdit(0)
                    }
                }
            }, contentAlignment = Alignment.TopCenter){
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Icon(painter = painterResource(Res.drawable.vector), contentDescription = "",
                    tint = primaryColor,
                    modifier = Modifier.padding(top=15.dp).rotate(rotateAnimate)
                        .clickable {
                            if (islandState == 0)  islandStateEdit(1)
                            else
                                islandStateEdit(0)
                        })
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                    islandContent()
                }

            }
        }
        //}
    }
}