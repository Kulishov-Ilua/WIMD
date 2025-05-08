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

import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.datetime.Clock
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atStartOfDayIn
import kotlinx.datetime.plus
import kotlinx.datetime.toLocalDateTime
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
@Preview
fun App(db:AppDatabase,
        primaryColor: Color,
        backgroundColor:Color,
        titleMedium: TextStyle,
        bodyMedium: TextStyle,
        titleLarge: TextStyle
) {

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
    //------------------------------------------------------
    //Получаем данные с бд
    //------------------------------------------------------
    //var selectedDate by remember { mutableStateOf(Calen.getInstance(TimeZone.GMT_ZONE)) }
    val coroutineScope = CoroutineScope(Dispatchers.IO)

    coroutineScope.launch {
        db.groupDao().getAllGroup()
            .collect { groups ->
                listGroup.value = groups.map {
                    GroupTask(it.uid, it.name, it.color)
                }
            }
    }

// Для задач
    val currentDate = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    coroutineScope.launch {
        db.trackerDao().getDayTask(
            getDayStartAndEnd(currentDate).first,
            getDayStartAndEnd(currentDate).second
        ).collect { tasks ->
            listTask.value = tasks.map {
                Task(it.uid, it.name, it.start, it.endTime, it.groupID)
            }
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
//            bottomBar = {
//                BottomNavigationBar(navController,backgroundColor)
//            },
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
                    },primaryColor,backgroundColor,titleMedium,bodyMedium,titleLarge
                )
            }
        )
    }
}

fun getDayStartAndEnd(date: LocalDate): Pair<Long, Long> {
    val timeZone = TimeZone.currentSystemDefault()
    val startOfDay = date.atStartOfDayIn(timeZone).toEpochMilliseconds()
    val endOfDay = date.plus(1, DateTimeUnit.DAY).atStartOfDayIn(timeZone).toEpochMilliseconds()
    return Pair(startOfDay, endOfDay)
}