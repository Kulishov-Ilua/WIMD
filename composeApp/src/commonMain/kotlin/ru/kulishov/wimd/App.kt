package ru.kulishov.wimd
//#####################################################################################################################
//#####################################################################################################################
//###############################                         WIMD                          ###############################
//#####################################################################################################################
//####   Автор: Кулишов Илья Вячеславович     #########################################################################
//####   Версия: v.0.0.1                      #########################################################################
//####   Дата: 18.01.2025                     #########################################################################
//#####################################################################################################################
//#####################################################################################################################

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview

import wimd.composeapp.generated.resources.Res
import wimd.composeapp.generated.resources.compose_multiplatform

@Composable
@Preview
fun App() {
    MaterialTheme {
        var showContent by remember { mutableStateOf(false) }
        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Button(onClick = { showContent = !showContent }) {
                Text("Click me!")
            }
            AnimatedVisibility(showContent) {
                val greeting = remember { Greeting().greet() }
                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Image(painterResource(Res.drawable.compose_multiplatform), null)
                    Text("Compose: $greeting")
                }
            }
        }
    }
}

//=====================================================================================
//bottomIslandScreen
//Input values:
//              screen:@Composable () -> Unit - screen
//              islandContent:@Composable () -> Unit - island content
//              islandColor:Color - islandColor
//              primaryColor:Color - primaryColor
//=====================================================================================
@Composable
fun bottomIslandScreen(screen:@Composable () -> Unit, islandContent:@Composable () -> Unit, islandColor: Color, primaryColor:Color){
    var state by remember { mutableStateOf(0) }
    //анимация размера острова
    val islandHeight by animateDpAsState(
        targetValue = if(state==0) 200.dp     //
        else if(state==1) 100.dp              //
        else if(state==2) 350.dp              //
        else if (state==3)465.dp              //
        else if (state==4)310.dp              //
        else if (state==5)480.dp              //
        else 625.dp,                                //
        animationSpec = tween(durationMillis= 500, delayMillis = 200), label = ""
    )
    //анимация стрелки на острове
    val rotateAnimate by animateFloatAsState(targetValue = if (state == 1) 180F else 0F
        , animationSpec = tween(delayMillis = 300), label = ""
    )

    Box(Modifier.fillMaxWidth()){
        Column(Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(Modifier.padding(bottom = islandHeight)){
                screen()
            }
            Box(Modifier.height(islandHeight), contentAlignment = Alignment.TopCenter){
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(painter = painterResource(Res.drawable.vector), contentDescription = "",
                        tint = primaryColor,
                        modifier = Modifier.padding(top=15.dp, bottom = 15.dp).rotate(rotateAnimate)
                            .clickable {
                                if (state == 0) state = 1
                                else
                                    state = 0
                            })
                }
            }
        }    }}