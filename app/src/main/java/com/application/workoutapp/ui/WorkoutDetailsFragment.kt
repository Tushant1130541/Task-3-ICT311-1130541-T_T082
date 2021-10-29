package com.application.workoutapp.ui

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.application.workoutapp.R
import com.application.workoutapp.database.WorkoutDatabase
import com.application.workoutapp.database.WorkoutDetails
import java.text.SimpleDateFormat
import java.util.*

/*
* Fragment which holds Activity's Detail
*
* */
class WorkoutDetailsFragment : Fragment() {

    private lateinit var mAddImage: ImageView

    private lateinit var mSaveButton: Button
    private lateinit var mUpdateButton: Button
    private lateinit var mDeleteButton: Button
    private lateinit var mDateButton: Button

    private lateinit var mLLUpdate: LinearLayout
    private lateinit var mLLSave: LinearLayout

    private lateinit var mTitleEditText: EditText
    private lateinit var mPlaceEditText: EditText
    private lateinit var mStartTimeEditText: EditText
    private lateinit var mEndTimeEditText: EditText

    private lateinit var mRadioGroup: RadioGroup

    private var mItemPos = -1
    private var mFinalActivity = "Individual"
    private var mWorkoutDB: WorkoutDatabase? = null
    private var mWorkoutDetail: WorkoutDetails? = null
    private val mCalender = Calendar.getInstance()

    // Create View for this Fragment
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var view = inflater.inflate(R.layout.fragment_workout_details, container, false)
        mAddImage = view.findViewById(R.id.toolbar_add_button)
        mSaveButton = view.findViewById(R.id.btn_save)
        mUpdateButton = view.findViewById(R.id.btn_update)
        mDeleteButton = view.findViewById(R.id.btn_delete)
        mLLSave = view.findViewById(R.id.ll_save)
        mLLUpdate = view.findViewById(R.id.ll_update)
        mTitleEditText = view.findViewById(R.id.et_title)
        mPlaceEditText = view.findViewById(R.id.et_place)
        mDateButton = view.findViewById(R.id.btn_date)
        mStartTimeEditText = view.findViewById(R.id.et_start_time)
        mEndTimeEditText = view.findViewById(R.id.et_end_time)
        mRadioGroup = view.findViewById(R.id.rg_activity)

        // Position of Activity clicked in List Fragment
        if (arguments != null) {
            mItemPos = requireArguments().getInt("Item_Pos")
        }

        init(mItemPos)

        // Auto-Save when User Pressed Back Button
        activity?.onBackPressedDispatcher?.addCallback(
            viewLifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (mItemPos == -1 && validate(true)) {
                        val workoutDetails = WorkoutDetails(
                            0, mTitleEditText.text.toString(),
                            mDateButton.text.toString(),
                            mPlaceEditText.text.toString(),
                            mStartTimeEditText.text.toString(),
                            mEndTimeEditText.text.toString(),
                            mFinalActivity
                        )

                        Thread {
                            mWorkoutDB?.daoAccess()?.insertWorkout(workoutDetails)
                        }.start()
                    }
                    closeFragment()
                }
            })

        return view
    }

    @SuppressLint("SetTextI18n")
    private fun init(mItemPos: Int) {
        mWorkoutDB = activity?.let { WorkoutDatabase.getInstance(it) }

        mAddImage.visibility = View.GONE

        mDateButton.setOnClickListener {
            var year: String
            var month: String
            var day: String
            var list: List<String>

            if (!mDateButton.text.toString().isEmpty()) {
                list = mDateButton.text.toString().split(".")

                day = list[0]
                month = list[1]
                year = list[2]
            } else {
                year = mCalender.get(Calendar.YEAR).toString()
                month = mCalender.get(Calendar.MONTH).toString()
                day = mCalender.get(Calendar.DAY_OF_MONTH).toString()
            }

            val dpd =
                activity?.let { it1 ->
                    DatePickerDialog(it1, { _, year, monthOfYear, dayOfMonth ->
                        // Display Selected date in textbox
                        val selMonth = monthOfYear + 1
                        mDateButton.setText("""$dayOfMonth.$selMonth.$year""")
                    }, Integer.parseInt(year), Integer.parseInt(month) - 1, Integer.parseInt(day))
                }
            dpd?.show()
        }

        mStartTimeEditText.setOnClickListener {
            getStartTime()
        }

        mEndTimeEditText.setOnClickListener {
            getEndTime()
        }

        mRadioGroup.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rb_in -> {
                    mFinalActivity = "Individual"
                }
                R.id.rb_group -> {
                    mFinalActivity = "Group"
                }
            }
        }

        // When a New Item is to be inserted
        if (mItemPos == -1) {
            mSaveButton.visibility = View.VISIBLE
            mDeleteButton.visibility = View.GONE
            mUpdateButton.visibility = View.GONE

            mRadioGroup.check(R.id.rb_in)

            val currentTime: String =
                SimpleDateFormat(
                    "dd.MM.yyyy",
                    Locale.getDefault()
                ).format(System.currentTimeMillis())
            mDateButton.setText(currentTime)

            mSaveButton.setOnClickListener {

                if (!validate(false)) {
                    Toast.makeText(context, "Please Fill Required Data", Toast.LENGTH_SHORT).show()
                } else {
                    val workoutDetails = WorkoutDetails(
                        0, mTitleEditText.text.toString(),
                        mDateButton.text.toString(),
                        mPlaceEditText.text.toString(),
                        mStartTimeEditText.text.toString(),
                        mEndTimeEditText.text.toString(),
                        mFinalActivity
                    )

                    Thread {
                        mWorkoutDB?.daoAccess()?.insertWorkout(workoutDetails)

                        closeFragment()
                    }.start()
                }
            }
        } else {
            // When an existing Activity is Clicked
            getDataFromLocalDatabase()

            mSaveButton.visibility = View.GONE
            mDeleteButton.visibility = View.VISIBLE
            mUpdateButton.visibility = View.VISIBLE

            mDeleteButton.setOnClickListener {
                onWarningAlertDialog(
                    "Do You want to Delete this Activity ?",
                    2
                )
            }

            mUpdateButton.setOnClickListener {
                if (!validate(false)) {
                    Toast.makeText(context, "Please Fill Required Data", Toast.LENGTH_SHORT).show()
                } else {
                    onWarningAlertDialog(
                        "Do You want to Update this Activity ?",
                        1
                    )
                }
            }
        }
    }

    // Update an Activity after User Confirmation
    private fun proceedWithUpdate() {
        mWorkoutDetail?.title = mTitleEditText.text.toString()
        mWorkoutDetail?.date = mDateButton.text.toString()
        mWorkoutDetail?.place = mPlaceEditText.text.toString()
        mWorkoutDetail?.startTime = mStartTimeEditText.text.toString()
        mWorkoutDetail?.endTime = mEndTimeEditText.text.toString()
        mWorkoutDetail?.activityType = mFinalActivity

        Thread {
            mWorkoutDB?.daoAccess()?.updateWorkout(mWorkoutDetail)

            closeFragment()
        }.start()
    }

    // Delete an Activity after User Confirmation
    private fun proceedWithDelete() {
        Thread {
            mWorkoutDB?.daoAccess()?.deleteWorkout(mWorkoutDetail)

            closeFragment()
        }.start()
    }

    // Fetch StartTime for this Activity
    private fun getStartTime() {
        var hour: String
        var minute: String
        var list: List<String>

        if (!mStartTimeEditText.text.toString().isEmpty()) {
            list = mStartTimeEditText.text.toString().split(":")

            hour = list[0]

            list = list[1].split(" ")

            minute = list[0]
        } else {
            hour = mCalender.get(Calendar.HOUR).toString()
            minute = mCalender.get(Calendar.MINUTE).toString()
        }

        val tpd = TimePickerDialog(activity, { _, h, m ->
            mStartTimeEditText.setText(String.format("%02d:%02d", h, m))
        }, Integer.parseInt(hour), Integer.parseInt(minute), false)
        tpd.show()
    }

    // Fetch EndTime for this Activity
    private fun getEndTime() {
        var hour: String
        var minute: String
        var list: List<String>

        if (!mEndTimeEditText.text.toString().isEmpty()) {
            list = mEndTimeEditText.text.toString().split(":")

            hour = list[0]

            list = list[1].split(" ")

            minute = list[0]
        } else {
            hour = mCalender.get(Calendar.HOUR).toString()
            minute = mCalender.get(Calendar.MINUTE).toString()
        }

        val tpd = TimePickerDialog(activity, { _, h, m ->
            mEndTimeEditText.setText(String.format("%02d:%02d", h, m))
        }, Integer.parseInt(hour), Integer.parseInt(minute), false)
        tpd.show()
    }

    // Validate Title, Place, Date fields before saving into DB
    private fun validate(skipError: Boolean): Boolean {
        var inputData = mTitleEditText.text.toString()
        if (inputData.isEmpty()) {
            if (!skipError)
                mTitleEditText.setError("Title Required")
            return false
        }

        inputData = mDateButton.text.toString()
        if (inputData.isEmpty()) {
            if (!skipError)
                mDateButton.setError("Date Required")
            return false
        }

        inputData = mPlaceEditText.text.toString()
        if (inputData.isEmpty()) {
            if (!skipError)
                mPlaceEditText.setError("Place Required")
            return false
        }

        return true
    }

    // Alert Popup for User confirmation
    private fun onWarningAlertDialog(msg: String, type: Int) {
        val dialogBuilder = activity?.let { AlertDialog.Builder(it) }
        dialogBuilder?.setMessage(msg)
            ?.setCancelable(false)
            ?.setPositiveButton("Yes") { _, _ ->
                if (type == 1)
                    proceedWithUpdate()
                else
                    proceedWithDelete()
            }
            ?.setNegativeButton("No") { dialog, _ ->
                dialog.cancel()
            }

        val alert = dialogBuilder?.create()
        alert?.setTitle("Alert!!!")
        alert?.show()
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            WorkoutDetailsFragment().apply {
            }
    }

    // Close this Fragment
    private fun closeFragment() {
        this.activity?.supportFragmentManager?.popBackStack()
    }

    // Fetch existing Activity's Data from DB
    private fun getDataFromLocalDatabase() {
        Thread {
            Looper.prepare()

            mWorkoutDetail = mWorkoutDB?.daoAccess()?.fetchWorkout(mItemPos)

            if (mWorkoutDetail != null) {
                mTitleEditText.setText(mWorkoutDetail?.title)
                mPlaceEditText.setText(mWorkoutDetail?.place)
                mDateButton.setText(mWorkoutDetail?.date)
                mStartTimeEditText.setText(mWorkoutDetail?.startTime)
                mEndTimeEditText.setText(mWorkoutDetail?.endTime)

                if (mWorkoutDetail?.activityType == "Individual")
                    mRadioGroup.check(R.id.rb_in)
                else
                    mRadioGroup.check(R.id.rb_group)
            }
        }.start()
    }
}