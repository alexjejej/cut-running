
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.navigation.Navigation.findNavController
import com.cut.android.running.R
import com.cut.android.running.databinding.AdminRaceActionsDialogFragmentBinding
import com.cut.android.running.databinding.FragmentRacesManagementBinding
import com.cut.android.running.databinding.RaceAddDialogFragmentBinding
import com.cut.android.running.databinding.RaceInfoDialogFragmentBinding
import com.cut.android.running.models.Race
import com.cut.android.running.provider.DatosUsuario
import com.cut.android.running.provider.services.navigation.NavigationObj
import com.cut.android.running.usecases.gestioncarreras.AdminUserByRace
import com.cut.android.running.usecases.gestioncarreras.RaceDone
import com.cut.android.running.usecases.gestioncarreras.adapter.RaceAdapter
import com.cut.android.running.usecases.gestioncarreras.adapter.UserRaceAdapter
import com.cut.android.running.usecases.gestioncarreras.addracedialog.AddRaceDialog
import com.cut.android.running.usecases.gestioncarreras.adminraceactions.AdminRaceActionsDialog
import com.cut.android.running.usecases.gestionuc.UcManagementViewModel
import com.cut.android.running.usecases.home.HomeViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.ParseException
import java.text.SimpleDateFormat
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

        // Configuración del primer RecyclerView
        binding.rcvRaces.adapter = raceAdapter
        binding.rcvRaces.setHasFixedSize(true)

        homeViewModel.checkUserRole(DatosUsuario.getEmail(requireActivity()))

        raceManagementViewModel.getRaceModel.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                val currentDate = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.time

                val races = it.filter { race ->
                    val raceDate = parseDate(race.date) // Convertir el String a Date utilizando parseDate
                    raceDate?.let {
                        val raceCalendar = Calendar.getInstance().apply { time = it }

                        raceCalendar.time.after(currentDate) || isSameDay(raceCalendar.time, currentDate)
                    } ?: false
                }.map { race ->
                    val convertedDate = convertDateFormat(race.date, 0) // Supongo que esto convierte la fecha a otro formato
                    race.copy(date = convertedDate)
                }

                raceAdapter.races = races
            }



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

        // Configuración del segundo RecyclerView para carreras de usuario
        val userRaceAdapter = UserRaceAdapter { race -> navigateToRaceDone(race) }
        binding.rvCarrerasUser.adapter = userRaceAdapter

        raceManagementViewModel.getRaceByUserModel.observe(viewLifecycleOwner) { it ->
            if (!it.isNullOrEmpty()) {
                val races = it.map { race ->
                    val convertedDate = convertDateFormat(race.date, 0)
                    race.copy(date = convertedDate)
                }
                userRaceAdapter.races = races
            }
        }


        val userEmail = DatosUsuario.getEmail(requireActivity())
        raceManagementViewModel.getRaceByUser(userEmail)

        return binding.root

    }

    fun isSameDay(date1: Date, date2: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date1 }
        val cal2 = Calendar.getInstance().apply { time = date2 }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    fun parseDate(dateString: String): Date? {
        val dateFormat1 = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        dateFormat1.timeZone = TimeZone.getTimeZone("America/Mexico_City")

        val dateFormat2 = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        dateFormat2.timeZone = TimeZone.getTimeZone("America/Mexico_City")

        return try {
            dateFormat1.parse(dateString)
        } catch (e1: ParseException) {
            try {
                dateFormat2.parse(dateString)
            } catch (e2: ParseException) {
                null
            }
        }
    }
    private fun convertDateFormat(dateString: String, offsetHours: Int): String {
        val inputFormats = arrayOf(
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"),
            SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        )

        val timeZone = TimeZone.getTimeZone("America/Mexico_City")
        timeZone.rawOffset = offsetHours * 3600 * 1000 // Convert offset from hours to milliseconds

        for (format in inputFormats) {
            try {
                format.timeZone = timeZone
                val date = format.parse(dateString)
                if (date != null) {
                    val outputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                    outputFormat.timeZone = timeZone
                    return outputFormat.format(date)
                }
            } catch (e: ParseException) {
                // Intenta el siguiente formato si la conversión falla
            }
        }

        // Si no se pudo convertir con ninguno de los formatos, devuelve la cadena original
        return dateString
    }



    private fun navigateToRaceDone(race: Race) {
        val raceDoneFragment = RaceDone().apply {
            arguments = Bundle().apply {
                putSerializable("race", race)
            }
        }
        navigateToFragment(raceDoneFragment)
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.home_container_fragment, fragment)
            .addToBackStack(null)
            .commit()
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
            AdminRaceClick(race)
        else
            UserRaceClick(race)
    }

    private fun UserRaceClick(race: Race) {
        raceManagementViewModel.verifyRelationship.observeOnce(viewLifecycleOwner) {
            if (it != null && it) {
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
        raceManagementViewModel.verifyUserRaceRelationship(DatosUsuario.getEmail(requireActivity()), race.id)
    }

    private fun AdminRaceClick(race: Race) {
        val adminRaceActionDialogBinding = AdminRaceActionsDialogFragmentBinding.inflate(layoutInflater)
        val adminRaceActionsDialog = AdminRaceActionsDialog(requireContext(), adminRaceActionDialogBinding, parentFragmentManager, raceManagementViewModel, viewLifecycleOwner, DatosUsuario.getEmail(requireActivity()), race.id)
        adminRaceActionsDialog.showDialog()
    }

    /**
     * Permite desvilcular el observer del live data despues del primer callback
     */
    fun <T> LiveData<T>.observeOnce(owner: LifecycleOwner, observer: (T) -> Unit) {
        observe(owner, object : Observer<T> {
            override fun onChanged(t: T) {
                observer(t)
                removeObserver(this)
            }
        })
    }
}