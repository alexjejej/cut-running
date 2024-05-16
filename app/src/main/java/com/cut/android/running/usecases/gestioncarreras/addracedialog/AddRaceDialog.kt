package com.cut.android.running.usecases.gestioncarreras.addracedialog

import RaceManagementViewModel
import android.content.Context
import android.content.res.Resources
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.Observer
import com.cut.android.running.R
import com.cut.android.running.databinding.RaceAddDialogFragmentBinding
import com.cut.android.running.models.UniversityCenter
import com.cut.android.running.models.dto.RaceDto
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat

import com.cut.android.running.usecases.gestionuc.UcManagementViewModel
import java.util.Calendar
import java.util.Date
import java.util.TimeZone
import kotlin.properties.Delegates

class AddRaceDialog constructor(
    context: Context,
    binding: RaceAddDialogFragmentBinding,
    resources: Resources,
    childFragmentManager: FragmentManager,
    raceManagementViewModel: RaceManagementViewModel,
    ucManagementViewModel: UcManagementViewModel,
    viewLifecycleOwner: LifecycleOwner
) {
    private val _context = context
    private val _binding = binding
    private val _resources = resources
    private val _childFragmentManager = childFragmentManager
    private val _raceManagementViewModel = raceManagementViewModel
    private val _ucManagementViewModel = ucManagementViewModel
    private val _viewLifecycleOwner = viewLifecycleOwner

    private val TAG: String = this::class.java.simpleName
    private lateinit var raceDate: String
    private lateinit var raceHour: String
    private var ucIdSelected by Delegates.notNull<Int>()

    /**
     * Build Mat dialog
     */
    public fun showDialog() {
        getUc()
        _binding.btnRaceDate.setOnClickListener { buildDatePicker().show(_childFragmentManager, "tagDatePicker") }
        _binding.btnRaceHour.setOnClickListener { buildTimePicker().show(_childFragmentManager, "tagTimePicker") }

        MaterialAlertDialogBuilder(_context)
            .setView(_binding.root)
            .setPositiveButton(_resources.getString(R.string.accept)) { dialog, which ->
                 _raceManagementViewModel.addRace(
                    RaceDto(0,
                        _binding.txtRaceName.text.toString(),
                        "${raceDate}T${(raceHour)}.00Z",
                        _binding.txtRaceDescription.text.toString(),
                        ucIdSelected,
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
        materialDatePicker.addOnPositiveButtonClickListener { selectedDate ->
            val calendar: Calendar = Calendar.getInstance(TimeZone.getTimeZone("America/Mexico_City"))
            calendar.time = Date(selectedDate)
            val year = calendar.get(Calendar.YEAR).toString()
            val month = (calendar.get(Calendar.MONTH) + 1).toString() // Suma 1 al índice del mes
            val day = (calendar.get(Calendar.DAY_OF_MONTH) + 1).toString()
            raceDate = "$year-${month.padStart(2, '0')}-${day.padStart(2, '0')}"
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

            val modifiedHour = materialTimePicker.hour
            raceHour = "${modifiedHour}:${minute}:00"
            val raceHourUnmodified = "${hour}:${minute}:00"
            _binding.btnRaceHour.text = raceHourUnmodified
        }
        return materialTimePicker
    }

    /**
     * Get Univercity Centers
     */
    private fun getUc() {
        _ucManagementViewModel.getUcModel.observe(_viewLifecycleOwner, Observer {
            if (it?.isNotEmpty() == true) {
                val items = it.map { uc -> uc.acronym }
                val adapter = ArrayAdapter(_context, R.layout.uc_dropdown_item, items)
                _binding.dropdown.adapter = adapter
                _binding.dropdown.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                        val ucSelected = it.get(position)
                        ucIdSelected = ucSelected.id
                        Log.d(TAG, "$ucIdSelected")
                    }

                    override fun onNothingSelected(p0: AdapterView<*>?) {
                        TODO("Not yet implemented")
                    }
                }
            }
        })
        _ucManagementViewModel.getUc()
    }
}