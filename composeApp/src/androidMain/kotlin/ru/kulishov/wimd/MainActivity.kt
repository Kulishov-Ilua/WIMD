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
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                bottomIslandScreen({},{}, MaterialTheme.colorScheme.primary, MaterialTheme.colorScheme.background)
            }

        }
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
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
            background = Color(255,245,225),
            primary = Color(63,89,156),
            surface = Color(255,245,225),
            )
         },
        content=content
    )
}
