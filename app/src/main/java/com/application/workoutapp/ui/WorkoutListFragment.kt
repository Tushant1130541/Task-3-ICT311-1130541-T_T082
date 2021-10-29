package com.application.workoutapp.ui

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.application.workoutapp.R
import com.application.workoutapp.database.WorkoutDatabase
import com.application.workoutapp.database.WorkoutDetails

/*
* Fragment to display Activity List
*
* */
class WorkoutListFragment : Fragment(), WorkoutListAdapter.ItemCellClickListener {

    private lateinit var mAddImage: ImageView
    private lateinit var mAddListener: AddButtonListener

    private lateinit var mRecyclerViewWorkout: RecyclerView
    private lateinit var mWorkoutAdapter: WorkoutListAdapter
    private var mActivityList = ArrayList<WorkoutDetails>()
    private var mWorkoutDB: WorkoutDatabase? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is AddButtonListener) {
            mAddListener = context
        } else {
            throw RuntimeException(requireContext().toString())
        }
    }

    // Create View for this Fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_workout_list, container, false)
        mAddImage = view.findViewById(R.id.toolbar_add_button)
        mRecyclerViewWorkout = view.findViewById(R.id.rv_workout)

        init()

        return view;
    }

    // Refresh Data from DB
    override fun onResume() {
        super.onResume()
        getDataFromLocalDatabase()
    }

    // Initialize Views
    private fun init() {
        mWorkoutDB = activity?.let { WorkoutDatabase.getInstance(it) }
        mAddImage.setOnClickListener { mAddListener.switchDetailsFragment(-1) }

        mWorkoutAdapter = WorkoutListAdapter(mActivityList, this)

        mRecyclerViewWorkout.layoutManager = LinearLayoutManager(activity)
        mRecyclerViewWorkout.itemAnimator = DefaultItemAnimator()
        mRecyclerViewWorkout.adapter = mWorkoutAdapter
    }

    // Fetch All the Activities from Local DB
    @SuppressLint("NotifyDataSetChanged")
    private fun getDataFromLocalDatabase() {
        Thread {
            val workoutList: List<WorkoutDetails> =
                mWorkoutDB?.daoAccess()?.fetchAllWorkouts() as List<WorkoutDetails>

            if (workoutList != null) {
                // Show a Toast message when new Activity is inserted
                if (mActivityList.size >= 0 && workoutList.size > mActivityList.size) {
                    activity?.runOnUiThread(Runnable {
                        Toast.makeText(context, "Activity Inserted", Toast.LENGTH_SHORT).show()
                    })
                }
                mActivityList = workoutList as ArrayList<WorkoutDetails>

                // Refresh List after an Activity inserted
                activity?.runOnUiThread(Runnable {
                    mWorkoutAdapter.refreshWorkoutList(mActivityList)
                    mWorkoutAdapter.notifyDataSetChanged()
                })
            }
        }.start()
    }

    companion object {
        @JvmStatic
        fun newInstance() = WorkoutListFragment().apply {
        }
    }

    // Add Button click listener
    interface AddButtonListener {
        fun switchDetailsFragment(itemPos: Int)
    }

    // List Item Click listener
    override fun onItemCellClickListener(pos: Int) {
        mAddListener.switchDetailsFragment(pos)
    }
}