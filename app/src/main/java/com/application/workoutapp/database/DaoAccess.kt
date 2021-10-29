package com.application.workoutapp.database

import androidx.room.*

/*
* Interface to interact with Data in local DB
*
* */
@Dao
interface DaoAccess {

    @Insert
    fun insertWorkout(workoutDetails: WorkoutDetails?): Long

    @Query("SELECT * FROM " + WorkoutDatabase.TABLE_NAME_ACTIVITY)
    fun fetchAllWorkouts(): List<WorkoutDetails?>?

    @Query("SELECT * FROM " + WorkoutDatabase.TABLE_NAME_ACTIVITY + " WHERE id=:itemPos")
    fun fetchWorkout(itemPos: Int): WorkoutDetails

    @Update()
    fun updateWorkout(workoutDetails: WorkoutDetails?): Int

    @Delete
    fun deleteWorkout(workoutDetails: WorkoutDetails?): Int
}