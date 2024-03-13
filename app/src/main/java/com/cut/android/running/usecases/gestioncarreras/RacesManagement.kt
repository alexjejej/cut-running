
import android.os.Bundle
import android.util.Log
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
import com.cut.android.running.provider.DatosUsuario
import com.cut.android.running.provider.services.navigation.NavigationObj
import com.cut.android.running.usecases.gestioncarreras.AdminUserByRace
import com.cut.android.running.usecases.gestioncarreras.adapter.RaceAdapter

import com.cut.android.running.usecases.gestioncarreras.addracedialog.AddRaceDialog
import com.cut.android.running.usecases.gestionuc.UcManagementViewModel
import com.cut.android.running.usecases.home.HomeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
    private val homeViewModel: HomeViewModel by viewModels()
    private var ISADMIN: Boolean = false;

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
        homeViewModel.checkUserRole(DatosUsuario.getEmail(requireActivity()))

        raceManagementViewModel.getRaceModel.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty())
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
        raceManagementViewModel.addUserRelationModel.observe(viewLifecycleOwner) {
            if (it) {
                raceManagementViewModel.getRaces()
                Toast.makeText(requireContext(), "Se ha registrado a la carrera corractamente", Toast.LENGTH_SHORT).show()
            }
            else
                Toast.makeText(requireContext(), "Ocurrio un error al registrarse a la carrera. Intente mas tarde", Toast.LENGTH_SHORT).show()
        }
        homeViewModel.roleId.observe(viewLifecycleOwner) { roleId ->
            ISADMIN = roleId ==1
            binding.btnShowCreateRace.visibility = if (ISADMIN) View.VISIBLE else View.GONE
        }

        raceManagementViewModel.getRaces()
        // tempGetRaces()

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
        if (ISADMIN)
            NavigationObj.navigateTo(parentFragmentManager, AdminUserByRace.newInstance(race.id), "AdminUserByRace")
        else {
            raceManagementViewModel.getRaceByUserModel.observe(viewLifecycleOwner) {
                if (!it.isNullOrEmpty()) {
                    val result = it.any {r -> r.id == race.id}
                    if (result) {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Registro existente")
                            .setMessage("Usted ya se encuetra registrado en la carrera seleccionada")
                            .setPositiveButton("Cerrar") { dialog, which ->
                                dialog.cancel()
                            }
                            .show()
                    }
                    else {
                        val dialogBinding = RaceInfoDialogFragmentBinding.inflate(layoutInflater)
                        val raceInforDialog = RaceInfoDialog(requireContext(), dialogBinding, raceManagementViewModel)
                        raceInforDialog.showDialog(DatosUsuario.getEmail(requireActivity()), race)
                    }
                }
                else {
                    val dialogBinding = RaceInfoDialogFragmentBinding.inflate(layoutInflater)
                    val raceInforDialog = RaceInfoDialog(requireContext(), dialogBinding, raceManagementViewModel)
                    raceInforDialog.showDialog(DatosUsuario.getEmail(requireActivity()), race)
                }
            }
            raceManagementViewModel.getRaceByUser(DatosUsuario.getEmail(requireActivity()))
        }
    }
}