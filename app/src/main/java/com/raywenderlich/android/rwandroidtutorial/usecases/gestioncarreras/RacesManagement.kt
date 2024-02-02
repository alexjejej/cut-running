package com.raywenderlich.android.rwandroidtutorial.usecases.gestioncarreras

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.MaterialTimePicker.INPUT_MODE_CLOCK
import com.google.android.material.timepicker.TimeFormat
import com.google.api.Context
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.runtracking.databinding.FragmentRacesManagementBinding
import com.raywenderlich.android.runtracking.databinding.RaceAddDialogFragmentBinding
import com.raywenderlich.android.rwandroidtutorial.models.Race
import com.raywenderlich.android.rwandroidtutorial.models.UniversityCenter
import com.raywenderlich.android.rwandroidtutorial.models.dto.RaceDto
import com.raywenderlich.android.rwandroidtutorial.provider.RetrofitInstance
import com.raywenderlich.android.rwandroidtutorial.provider.services.RaceService
import com.raywenderlich.android.rwandroidtutorial.usecases.gestioncarreras.adapter.RaceAdapter
import com.raywenderlich.android.rwandroidtutorial.usecases.gestioncarreras.addracedialog.AddRaceDialog
import com.raywenderlich.android.rwandroidtutorial.usecases.gestioncarreras.viewmodel.RaceManagementViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date
import java.util.TimeZone

/**
 * A simple [Fragment] subclass.
 * Use the [RacesManagement.newInstance] factory method to
 * create an instance of this fragment.
 */
class RacesManagement : Fragment() {
    private var _binding: FragmentRacesManagementBinding? = null
    private val binding get() = _binding!!
    private val raceManagementViewModel: RaceManagementViewModel by viewModels()
    private val TAG: String = this::class.java.simpleName
    val raceAdapter: RaceAdapter = RaceAdapter { race -> onItemClick(race) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRacesManagementBinding.inflate(inflater, container, false)
        binding.rcvRaces.adapter = raceAdapter
        binding.rcvRaces.setHasFixedSize(true)

        raceManagementViewModel.getRaceModel.observe(viewLifecycleOwner, Observer {
            raceAdapter.races = it!!
        })
        raceManagementViewModel.raceActionsModel.observe(viewLifecycleOwner, Observer {
            if (it) {
                raceManagementViewModel.getRaces()
                Log.d(TAG, "Registro y obtencion")
                Toast.makeText(
                    requireContext(),
                    "Carrera agregada correctamente",
                    Toast.LENGTH_SHORT
                ).show()
            }
            else
                Toast.makeText(requireContext(), "Ocurrio un error al agregar la carrera", Toast.LENGTH_SHORT).show()
        })
        raceManagementViewModel.getRaces()

        binding.btnShowCreateRace.setOnClickListener {
            /**
             * Shows a dialog to add race
             */
            val dialogBinding = RaceAddDialogFragmentBinding.inflate(layoutInflater)
            val addRaceDialog: AddRaceDialog = AddRaceDialog(requireContext(), dialogBinding, resources, childFragmentManager, raceManagementViewModel)
            addRaceDialog.showDialog()
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = RacesManagement()
    }

    /**
     * Action when click on list item
     */
    private fun onItemClick(race: Race) {
        Toast.makeText(requireContext(), "${race.id} - ${race.name}", Toast.LENGTH_LONG).show()
    }

    /**
     * Get temporal races list to test
     * @suppress
     */
    private fun tempGetRaces() {
        val tempRaces: List<Race> = listOf(
            Race(
            1,
            "Race 1",
            "2024-01-31",
            "Description 1",
            UniversityCenter(1, "CUT", "CUT", 1, "2020-02-22"),
            1,
            "2023-12-22"),
            Race(
                2,
                "Race 2",
                "2024-01-31",
                "Description 2",
                UniversityCenter(1, "CUT", "CUT", 1, "2020-02-22"),
                1,
                "2023-12-22")
            )
        raceAdapter.races = tempRaces

    }

    /**
     * Add new race to Database
     */
    private fun addRace(race: RaceDto) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val call = RetrofitInstance.getRetrofit().create(RaceService::class.java).addRace(race)
                val result = call.body()
                requireActivity().runOnUiThread {
                    if (result != null && result.isSuccess) {
                        Toast.makeText(requireContext(), "Carrera agregada exitosamente", Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(requireContext(), "${result?.message ?: "Error desconocido"}", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error al agregar carrera", e)
                requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Error con la conexión", Toast.LENGTH_LONG).show()
                }
            }
        }
    }
}