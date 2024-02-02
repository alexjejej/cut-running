package com.raywenderlich.android.rwandroidtutorial.usecases.gestioncarreras.addracedialog

import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.runtracking.databinding.RaceAddDialogFragmentBinding
import com.raywenderlich.android.rwandroidtutorial.models.dto.RaceDto
import com.raywenderlich.android.rwandroidtutorial.provider.RetrofitInstance
import com.raywenderlich.android.rwandroidtutorial.provider.services.RaceService
import com.raywenderlich.android.rwandroidtutorial.usecases.gestioncarreras.viewmodel.RaceManagementViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

class AddRaceDialog constructor(
    context: Context,
    binding: RaceAddDialogFragmentBinding,
    resources: Resources,
    childFragmentManager: FragmentManager,
    raceManagementViewModel: RaceManagementViewModel
) {
    private val _context = context
    private val _binding = binding
    private val _resources = resources
    private val _childFragmentManager = childFragmentManager
    private val _raceManagementViewModel = raceManagementViewModel

    private val TAG: String = this::class.java.simpleName
    private lateinit var raceDate: String
    private lateinit var raceHour: String

    /**
     * Build Mat dialog
     */
    public fun showDialog() {
        _binding.btnRaceDate.setOnClickListener { buildDatePicker().show(_childFragmentManager, "tagDatePicker") }
        _binding.btnRaceHour.setOnClickListener { buildTimePicker().show(_childFragmentManager, "tagTimePicker") }

        MaterialAlertDialogBuilder(_context)
            .setView(_binding.root)
            .setPositiveButton(_resources.getString(R.string.accept)) { dialog, which ->
                 _raceManagementViewModel.addRace(
                    RaceDto(0,
                        _binding.txtRaceName.text.toString(),
                        "${raceDate}T${raceHour}.00Z",
                        _binding.txtRaceDescription.text.toString(),
                        1,
                        1,
                        null
                    )
                 )
            }
            .setNegativeButton(_resources.getString(R.string.cancel)) { dialog, which ->
                dialog.cancel()
            }
            .show()
    }

    /**
     * Build Date Picker
     */
    private fun buildDatePicker(): MaterialDatePicker<Long> {
        val materialDatePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Elegir fecha")
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .setInputMode(MaterialDatePicker.INPUT_MODE_CALENDAR)
            .build()
        /**
         * Add button behavior
         */
        materialDatePicker.addOnPositiveButtonClickListener {
            val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            calendar.time = Date(it)
            val year = calendar.get(Calendar.YEAR).toString()
            val mont = if (calendar.get(Calendar.MONTH).toString().length > 1) (calendar.get(Calendar.MONTH)+1).toString() else "0${(calendar.get(Calendar.MONTH)+1).toString()}"
            val day = if (calendar.get(Calendar.DAY_OF_MONTH).toString().length > 1) calendar.get(Calendar.DAY_OF_MONTH).toString() else "0${calendar.get(Calendar.DAY_OF_MONTH).toString()}"
            raceDate = "${year}-${mont}-${day}"
            _binding.btnRaceDate.text = materialDatePicker.headerText
        }
        return materialDatePicker
    }

    /**
     * Build Time Picker
     */
    private fun buildTimePicker(): MaterialTimePicker {
        val materialTimePicker = MaterialTimePicker.Builder()
            .setTitleText("Elegir hora")
            .setInputMode(MaterialTimePicker.INPUT_MODE_CLOCK)
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(12)
            .setMinute(0)
            .build()
        /**
         * Add button behavior
         */
        materialTimePicker.addOnPositiveButtonClickListener {
            val hour = if (materialTimePicker.hour in 0..9) "0${materialTimePicker.hour}" else materialTimePicker.hour
            val minute = if (materialTimePicker.minute in 0..9) "0${materialTimePicker.minute}" else materialTimePicker.minute
            raceHour = "${hour}:${minute}:00"
            _binding.btnRaceHour.text = raceHour
        }
        return materialTimePicker
    }
}