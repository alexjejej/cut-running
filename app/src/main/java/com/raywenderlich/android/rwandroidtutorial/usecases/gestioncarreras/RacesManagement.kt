package com.raywenderlich.android.rwandroidtutorial.usecases.gestioncarreras

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.runtracking.databinding.FragmentRacesManagementBinding
import com.raywenderlich.android.rwandroidtutorial.models.Race
import com.raywenderlich.android.rwandroidtutorial.models.UniversityCenter
import com.raywenderlich.android.rwandroidtutorial.models.dto.RaceDto
import com.raywenderlich.android.rwandroidtutorial.provider.RetrofitInstance
import com.raywenderlich.android.rwandroidtutorial.provider.services.RaceService
import com.raywenderlich.android.rwandroidtutorial.usecases.gestioncarreras.adapter.RaceAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [RacesManagement.newInstance] factory method to
 * create an instance of this fragment.
 */
class RacesManagement : Fragment() {
    private var _binding: FragmentRacesManagementBinding? = null
    private val binding get() = _binding!!
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
        getRaces()
//        tempGetRaces()
        binding.btnShowCreateRace.setOnClickListener {
            val dialogView: View = inflater.inflate(R.layout.race_add_dialog_fragment, null)
            MaterialAlertDialogBuilder(requireContext())
                .setView(dialogView)
                .setPositiveButton(resources.getString(R.string.accept)) { dialog, which ->
                    addRace(dialogView)
                }
                .setNegativeButton(resources.getString(R.string.cancel)) { dialog, which ->
                    dialog.cancel()
                }
                .show()
        }

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = RacesManagement()
    }

    private fun onItemClick(race: Race) {
        Toast.makeText(requireContext(), "${race.id} - ${race.name}", Toast.LENGTH_LONG).show()
    }

    private fun getRaces() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitInstance.getRetrofit().create(RaceService::class.java).getRaces()
            val result = call.body()
            requireActivity().runOnUiThread {
                if (result!!.isSuccess) {
                    raceAdapter.races = result.data!!
                }
                else {
                    Toast.makeText(requireContext(), "${result.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

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

    private fun addRace(view: View) {
        val race = RaceDto(null,
            view.findViewById<TextInputLayout>(R.id.txtRaceName).editText?.text.toString(),
            view.findViewById<TextInputLayout>(R.id.txtRaceDate).editText?.text.toString(),
            view.findViewById<TextInputLayout>(R.id.txtRaceDescription).editText?.text.toString(),
            1,
            1,
            null
        )

        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitInstance.getRetrofit().create(RaceService::class.java).addRace(race)
            val result = call.body()
            requireActivity().runOnUiThread {
                if (result!!.isSuccess) {
                    Toast.makeText(requireContext(), "Carrera agregada exitosamente", Toast.LENGTH_LONG).show()
                    getRaces()
                }
                else
                    Toast.makeText(requireContext(), "${result.message}", Toast.LENGTH_LONG).show()
            }
        }
    }
}