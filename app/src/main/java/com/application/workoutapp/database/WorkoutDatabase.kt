package com.application.workoutapp.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

/*
* Database Handler class which defines DB_Name, TABLE_Name, etc
*
* */
@Database(entities = [WorkoutDetails::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class WorkoutDatabase : RoomDatabase() {

    abstract fun daoAccess(): DaoAccess?

    companion object {

        private var INSTANCE: WorkoutDatabase? = null

        // Maintain a Singleton structure to return DB Object
        fun getInstance(context: Context): WorkoutDatabase? {
            if (INSTANCE == null) {
                synchronized(WorkoutDatabase::class) {
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        WorkoutDatabase::class.java,
                        DB_NAME
                    ).allowMainThreadQueries()
                        .build()
                }
            }

            return INSTANCE
        }

        fun destroyInstance() {
            INSTANCE = null
        }

        const val DB_NAME = "workout_db"
        const val TABLE_NAME_ACTIVITY = "activity"
    }
}