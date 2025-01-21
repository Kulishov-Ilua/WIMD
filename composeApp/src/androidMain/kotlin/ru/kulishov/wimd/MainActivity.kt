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
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AppTheme {
                //------------------------------------------------------
                //Состояние навигации:
                //      0 -> Трекер, экран записей. Состояние острова:
                //              0 -> Трекер расширенный
                //              1 -> Трекер компактный
                //              2 -> Трекер, выбор создания
                //              3 -> Трекер, создание/редактирование задачи
                //          Состояние содержимого острова:
                //              0 -> Трекер расширенный (Выключенный)
                //              1 -> Трекер компактный (Выключенный)
                //              2 -> Трекер расширенный (Запущенный)
                //              3 -> Трекер компактный  (Запущенный)
                //              4 -> Выбор создания
                //              5 -> Создание/редактирование задачи
                //      1 -> Трекер, группы. Состояние острова:
                //              4 -> Редактирование группы
                //------------------------------------------------------
                var state by remember { mutableStateOf(0) }
                val navController = rememberNavController()
                Surface {
                    Scaffold (
                        bottomBar = {
                            BottomNavigationBar(navController)
                        },
                        content = {
                                padding -> NavHostContainer(navController,padding)
                        }
                    )
                }
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
        content=content,
        typography = Typography(
            titleMedium = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            )
        )
    )
}


