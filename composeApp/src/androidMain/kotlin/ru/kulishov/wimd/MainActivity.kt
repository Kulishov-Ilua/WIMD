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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import androidx.lifecycle.asLiveData
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

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
                //Инициализации базы данных
                val db = TrackerDatabase.getInstance(this)
                //------------------------------------------------------
                //Получаем данные с бд
                //------------------------------------------------------
                var groups=db.groupDao().getAllGroup().asLiveData().observe(this){
                    listGroup.value= emptyList()
                    it.forEach {
                        listGroup.value+=GroupTask(it.uid,it.name, it.color)
                    }
                }
                var tasks = db.trackerDao().getAllTask().asLiveData().observe(this){
                    listTask.value= emptyList()
                    it.forEach{
                        listTask.value+=Task(it.uid,it.name,it.start, it.end, it.groupID)
                    }
                }
                //------------------------------------------------------
                //Обновление данных бд
                //      0 - ожидание
                //      1 - обновление групп
                //      2 - удаление группы
                //      3 - обновление задачи
                //      4 - удаление задачи
                //------------------------------------------------------
                var updateDataStatus by remember { mutableStateOf(5) }
                //Группа для записи в бд
                var updateGroupValue by remember { mutableStateOf(GroupTask(null,"","")) }
                var updateTaskValue by remember { mutableStateOf(Task(null,"",0L,0L,-1)) }

                val coroutineScope = CoroutineScope(Dispatchers.IO)
                LaunchedEffect(updateDataStatus) {
                    if (updateDataStatus > 0) {
                        coroutineScope.launch {
                            when(updateDataStatus){
                                1->{
                                    if(updateGroupValue.uid!=null){
                                        db.groupDao().updateGroup(updateGroupValue)
                                    }else {db.groupDao().insertGroup(updateGroupValue)}
                                    updateGroupValue = GroupTask(null, "", "")
                                    updateDataStatus=0
                                }
                                2->{
                                    if(updateGroupValue.uid!=null){
                                        db.groupDao().deleteGroup(updateGroupValue)
                                    }
                                    updateGroupValue = GroupTask(null, "", "")
                                    updateDataStatus=0
                                }
                                3->{
                                    if(updateTaskValue.uid!=null){
                                        db.trackerDao().updateTask(updateTaskValue)
                                    }else {db.trackerDao().insertTask(updateTaskValue)}
                                    updateTaskValue = Task(null,"",0L,0L,-1)
                                    updateDataStatus=0
                                }
                                4->{
                                    if(updateTaskValue.uid!=null){
                                        db.trackerDao().deleteTask(updateTaskValue)

                                    }
                                    updateTaskValue = Task(null,"",0L,0L,-1)
                                    updateDataStatus=0
                                }
                            }
                        }
                    }
                }

                val navController = rememberNavController()
                Surface {
                    Scaffold (
                        bottomBar = {
                            BottomNavigationBar(navController)
                        },
                        content = {
                                padding ->
                        NavHostContainer(navController,padding, listTask.value, listGroup.value,
                            {
                                type,task ->
                                updateTaskValue=task
                                if(type) updateDataStatus = 3
                                else updateDataStatus=4
                            },{
                                type,group ->
                                updateGroupValue=group
                                if(type) updateDataStatus=1
                                else updateDataStatus=2
                            })
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
                fontSize = 20.sp,
            ),
            titleLarge = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 48.sp
            )
        )
    )
}


