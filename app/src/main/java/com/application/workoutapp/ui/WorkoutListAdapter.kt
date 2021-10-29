package com.application.workoutapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.application.workoutapp.R
import com.application.workoutapp.database.WorkoutDetails

/*
* Adapter to Update Activities in List
*
* */
class WorkoutListAdapter(
    private var mList: List<WorkoutDetails>,
    private val itemCellClickListener: WorkoutListAdapter.ItemCellClickListener
) :
    RecyclerView.Adapter<WorkoutListAdapter.WorkoutViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        return WorkoutViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_workout, parent, false)
        )
    }

    // Update List Items
    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workoutDetails = mList[position]

        holder.title.text = workoutDetails.title
        holder.date.text = workoutDetails.date
        holder.place.text = workoutDetails.place

        holder.itemView.setOnClickListener {
            val details = mList[position]

            itemCellClickListener.onItemCellClickListener(details.id)
        }
    }

    // List Size
    override fun getItemCount(): Int {
        return mList.size
    }

    // New List
    fun refreshWorkoutList(list: List<WorkoutDetails>) {
        mList = list
    }

    // Class which holds UI Items
    class WorkoutViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tv_title)
        val date: TextView = itemView.findViewById(R.id.tv_date)
        val place: TextView = itemView.findViewById(R.id.tv_place)
    }

    // Click Listener for List
    interface ItemCellClickListener {
        fun onItemCellClickListener(pos: Int)
    }
}