package ru.kulishov.wimd

import androidx.room.ColumnInfo
import androidx.room.ConstructedBy
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

//##################################################################################################
//##################################################################################################
//#####################                    WIMD - Database                   #######################
//##################################################################################################
//####  Author:Kulishov I.V.                         ###############################################
//####  Version:0.1.0                                ###############################################
//####  Date:23.01.2025                              ###############################################
//##################################################################################################
//##################################################################################################


//------------------------------------------------------------------------------
//Класс для групп задач
//  Поля:
//      uid     - Идентификатор
//      name    - Название
//      color   - цвет
//------------------------------------------------------------------------------
@Entity
data class GroupTask(
    @PrimaryKey(autoGenerate = true) val uid: Int?=null,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "color") val color: String
)

//------------------------------------------------------------------------------
//Класс для задач
//  Поля:
//      uid     - Идентификатор
//      name    - Название
//      start   - Начало
//      end     - Конец
//      groupID - Идентификатор группы
//------------------------------------------------------------------------------
@Entity
data class Task(
    @PrimaryKey(autoGenerate = true) val uid: Long?=null,
    @ColumnInfo(name = "name") var name: String?,
    @ColumnInfo(name = "start") var start: Long,
    @ColumnInfo(name = "endTime") var endTime: Long,
    @ColumnInfo(name = "groupID") val groupID: Int,

    )

//------------------------------------------------------------------------------
//Класс для счётчика
//  Поля:
//      uid     - Идентификатор
//      name    - Название
//      count   - Количество
//      groupID - Идентификатор группы
//------------------------------------------------------------------------------
@Entity
data class Counter(
    @PrimaryKey(autoGenerate = true) val uid: Long?=null,
    @ColumnInfo(name = "name") val name: String?,
    @ColumnInfo(name = "count") val start: Long,
    @ColumnInfo(name = "groupID") val groupID: Int,

    )


//------------------------------------------------------------------------------
//Интерфейс Dao для трекера
//  Функции:
//      insertTask  - Добавить задачу
//      getAllTask  - Получить список задач
//      deleteTask  - Удалить задачу
//      updateTask  - Обновить задачу
//------------------------------------------------------------------------------
@Dao
interface DaoTracker{
    @Insert
    suspend fun insertTask(task: Task)
    @Query("SELECT * FROM Task")
     fun getAllTask(): Flow<List<Task>>
    @Query("SELECT * FROM Task WHERE (start < :dayEnd AND endTime >= :dayStart)")
     fun getDayTask(dayStart: Long, dayEnd: Long): Flow<List<Task>>
    @Delete
    suspend  fun deleteTask(task: Task)
    @Update
    suspend  fun updateTask(vararg task: Task)

}
//------------------------------------------------------------------------------
//Интерфейс Dao для счётчика
//  Функции:
//      insertCounter  - Добавить счётчик
//      getAllCounter  - Получить список счётчиков
//      deleteCounter  - Удалить счётчик
//      updateCounter  - Обновить счётчик
//------------------------------------------------------------------------------
@Dao
interface DaoCounter{

    @Insert
    suspend  fun insertCounter(counter: Counter)
    @Query("SELECT * FROM Counter")
    suspend fun getAllCounter():List<Counter>
    @Delete
    suspend fun deleteCounter(counter: Counter)
    @Update
    suspend fun updateCounter(vararg counter: Counter)
}

//------------------------------------------------------------------------------
//Интерфейс Dao для групп
//  Функции:
//      insertGroup  - Добавить группу
//      getAllGroup  - Получить список групп
//      deleteGroup  - Удалить группу
//      updateGroup  - Обновить группу
//------------------------------------------------------------------------------
@Dao
interface DaoGroup{
    @Update
    suspend fun updateGroup(vararg group: GroupTask)
    @Delete
    suspend fun deleteGroup(group: GroupTask)
    @Query("SELECT * FROM GroupTask ")
     fun getAllGroup(): Flow<List<GroupTask>>
    @Insert
    suspend fun insertGroup(group: GroupTask)
}

//#####################################################################################################################
//###############################                База данных приложения                 ###############################
//#####################################################################################################################

@Database(entities = [GroupTask::class, Task::class, Counter::class], version = 1)
@ConstructedBy(AppDatabaseConstructor::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun groupDao(): DaoGroup
    abstract fun trackerDao(): DaoTracker
    abstract fun counterDao(): DaoCounter


}

// The Room compiler generates the `actual` implementations.
@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object AppDatabaseConstructor : RoomDatabaseConstructor<AppDatabase> {
    override fun initialize(): AppDatabase
}