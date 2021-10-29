package com.application.workoutapp.ui

import android.os.Bundle
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.application.workoutapp.R
import com.application.workoutapp.ui.WorkoutListFragment.AddButtonListener

/*
* MainActivity: Primary activity to hold all Fragments
*
* */
class MainActivity : AppCompatActivity(), AddButtonListener {

    private var mItemPos = -2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Maintain ItemPos in case Details Fragment was open
        if (savedInstanceState != null) {
            with(savedInstanceState) {
                mItemPos = getInt("Item_Pos")
            }
        } else {
            goToFragment(WorkoutListFragment.newInstance(), Navigation.WORKOUTLIST, false)
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState?.run { putInt("Item_Pos", mItemPos) }

        super.onSaveInstanceState(outState)
    }

    // Switch to Fragment
    private fun goToFragment(
        @NonNull fragment: Fragment, @MainActivity.Navigation navigationID: String,
        addToNavigationStack: Boolean
    ) {
        try {
            if (navigationID == Navigation.WORKOUTLIST) {
                mItemPos = -2;
            }

            val fragmentManager: FragmentManager = supportFragmentManager
            val fragmentTransaction: FragmentTransaction = fragmentManager.beginTransaction()
            if (addToNavigationStack) {
                fragmentTransaction.addToBackStack(null)
            }
            fragmentTransaction.replace(R.id.fragment_container, fragment)
            fragmentTransaction.commit()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @kotlin.annotation.Retention(AnnotationRetention.SOURCE)
    internal annotation class Navigation {
        companion object {
            var WORKOUTLIST = "LIST"
            var WORKOUTDETAIL = "DETAIL"
        }
    }

    // Function to change to Details Fragment
    override fun switchDetailsFragment(itemPos: Int) {
        val detailFragment: WorkoutDetailsFragment = WorkoutDetailsFragment.newInstance()
        val dataBundle = Bundle()

        mItemPos = itemPos

        // Position of Activity Clicked in List Fragment
        dataBundle.putInt("Item_Pos", itemPos)

        detailFragment.arguments = dataBundle

        goToFragment(detailFragment, Navigation.WORKOUTDETAIL, true)
    }
}