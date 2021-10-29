package com.application.workoutapp.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/*
* POJO class which defines and holds an Activity's Detail in Local DB
*
* */
@Entity(tableName = WorkoutDatabase.TABLE_NAME_ACTIVITY)
data class WorkoutDetails(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id") var id: Int,
    @ColumnInfo(name = "title") var title: String,
    @ColumnInfo(name = "date") var date: String,
    @ColumnInfo(name = "place") var place: String,
    @ColumnInfo(name = "startTime") var startTime: String,
    @ColumnInfo(name = "endTime") var endTime: String,
    @ColumnInfo(name = "activityType") var activityType: String
)