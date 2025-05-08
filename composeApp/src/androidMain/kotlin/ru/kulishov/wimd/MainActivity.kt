package ru.kulishov.wimd
//##################################################################################################
//##################################################################################################
//#####################                    WIMD - Android                    #######################
//##################################################################################################
//####  Author:Kulishov I.V.                         ###############################################
//####  Version:0.1.0                                ###############################################
//####  Date:18.01.2025                              ###############################################
//##################################################################################################
//##################################################################################################
import android.annotation.SuppressLint
import android.app.Activity
import android.icu.util.Calendar
import android.icu.util.TimeZone
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.view.WindowCompat
import androidx.lifecycle.asLiveData

import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

import kotlin.coroutines.EmptyCoroutineContext



class MainActivity : ComponentActivity() {
    @SuppressLint("CoroutineCreationDuringComposition")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                val systemUiController = rememberSystemUiController()
                val useDarkIcons = !isSystemInDarkTheme()
                systemUiController.setStatusBarColor(
                    color = MaterialTheme.colorScheme.surface,
                    darkIcons = useDarkIcons
                )
                systemUiController.setNavigationBarColor(
                    color = MaterialTheme.colorScheme.primary,
                    darkIcons = useDarkIcons
                )
                val db = getRoomDatabase(getDatabaseBuilder(this))
                App(db,MaterialTheme.colorScheme.primary,MaterialTheme.colorScheme.background,MaterialTheme.typography.titleMedium,MaterialTheme.typography.bodyMedium,MaterialTheme.typography.titleLarge)
            }
        }
    }
}
@Preview
@Composable
fun AppAndroidPreview() {
    //App()
}

//#####################################################################################################################
//###############################                    Тема приложения                    ###############################
//#####################################################################################################################

@Composable
fun AppTheme(content:@Composable () -> Unit){
    MaterialTheme (        colorScheme = if(isSystemInDarkTheme()){
        darkColorScheme(
            background = Color(22,22,22),
            primary = Color(63,89,156),
            surface = Color(22,22,22),

        )
    }else{
        lightColorScheme(
            background = Color.White,
            primary = Color(63,89,156),
            surface = Color.White,
        )
    },
        content=content,
        typography = Typography(
            titleMedium = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
            ),
            titleLarge = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp
            )
        )
    )
}


