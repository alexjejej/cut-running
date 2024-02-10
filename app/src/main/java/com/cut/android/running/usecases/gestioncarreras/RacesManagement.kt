
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.cut.android.running.databinding.FragmentRacesManagementBinding
import com.cut.android.running.databinding.RaceAddDialogFragmentBinding
import com.cut.android.running.databinding.RaceInfoDialogFragmentBinding
import com.cut.android.running.models.Race
import com.cut.android.running.models.UniversityCenter
import com.cut.android.running.usecases.gestioncarreras.adapter.RaceAdapter

import com.cut.android.running.usecases.gestioncarreras.addracedialog.AddRaceDialog
import com.cut.android.running.usecases.gestionuc.UcManagementViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [RacesManagement.newInstance] factory method to
 * create an instance of this fragment.
 */
class RacesManagement : Fragment() {
    private var _binding: FragmentRacesManagementBinding? = null
    private val binding get() = _binding!!
    private val raceManagementViewModel: RaceManagementViewModel by viewModels()
    private val ucManagementViewModel: UcManagementViewModel by viewModels()

    val raceAdapter: RaceAdapter = RaceAdapter { race -> onItemClick(race) }
    private val TAG: String = this::class.java.simpleName

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
        raceManagementViewModel.addRaceActionsModel.observe(viewLifecycleOwner, Observer {
            if (it) {
                raceManagementViewModel.getRaces()
                Toast.makeText(requireContext(), "Carrera agregada correctamente", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(requireContext(), "Ocurrio un error al agregar la carrera", Toast.LENGTH_SHORT).show()
        })
        // raceManagementViewModel.getRaces()
        tempGetRaces()

        binding.btnShowCreateRace.setOnClickListener {
            /**
             * Shows a dialog to add race
             */
            val dialogBinding = RaceAddDialogFragmentBinding.inflate(layoutInflater)
            val addRaceDialog: AddRaceDialog = AddRaceDialog(
                requireContext(), dialogBinding, resources, childFragmentManager, raceManagementViewModel, ucManagementViewModel, viewLifecycleOwner)
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
        // Toast.makeText(requireContext(), "${race.id} - ${race.name}", Toast.LENGTH_LONG).show()

        val dialogBinding = RaceInfoDialogFragmentBinding.inflate(layoutInflater)
        val raceInforDialog = RaceInfoDialog(requireContext(), dialogBinding, raceManagementViewModel)
        raceInforDialog.showDialog(race)
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
}