package ru.kulishov.wimd

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase

//#####################################################################################################################
//###############################                База данных приложения                 ###############################
//#####################################################################################################################
@Database(
    entities = [
        GroupTask::class,
        Task::class,
        Counter::class
    ],
    version = 1,
)
abstract class TrackerDatabase: RoomDatabase(){
    abstract fun trackerDao():DaoTracker
    abstract fun counterDao():DaoCounter
    abstract fun groupDao(): DaoGroup

    companion object {
        @Volatile
        private var INSTANCE: TrackerDatabase? = null

        fun getInstance(context: Context): TrackerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TrackerDatabase::class.java,
                    "WIMD"
                )
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Выполняем заполнение данных в фоновом потоке
                            Thread {
                                getInstance(context).groupDao().insertGroup(
                                    GroupTask(0,"Личное", "169,82,170")
                                )
                            }.start()
                        }
                    })
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }

}