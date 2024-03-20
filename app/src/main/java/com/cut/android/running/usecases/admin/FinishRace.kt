package com.cut.android.running.usecases.admin

import RaceService
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cut.android.running.R
import com.cut.android.running.models.Race
import com.cut.android.running.models.dto.RaceDto
import com.cut.android.running.provider.RetrofitInstance
import com.cut.android.running.usecases.home.HomeFragment
import kotlinx.coroutines.launch
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class FinishRace : Fragment() {

    private lateinit var txtStatusCarrera: TextView
    private lateinit var txtLabelStatusAdmin: TextView
    private lateinit var txtTituloCarrera: TextView
    private lateinit var switchAdmin: Switch
    private lateinit var linearLayoutInformacion: LinearLayout
    private lateinit var btnAdminHome: Button
    private lateinit var btnAdminCerrar: Button
    private lateinit var raceService: RaceService

    private var raceId: Int = -1  // Valor predeterminado indicando que no se ha establecido
    private var currentRace: Race? = null  // Almacena los datos actuales de la carrera

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        raceService = RetrofitInstance.getRetrofit().create(RaceService::class.java)
        arguments?.let {
            raceId = it.getInt("raceId", -1)  // Obtener el raceId pasado como argumento
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_finish_race, container, false)
        initializeUi(view)
        loadRaceData()
        return view
    }

    private fun initializeUi(view: View) {
        txtStatusCarrera = view.findViewById(R.id.txtstatusCarrera)
        txtLabelStatusAdmin = view.findViewById(R.id.txtlabelstatusAdmin)
        switchAdmin = view.findViewById(R.id.switchAdmin)
        linearLayoutInformacion = view.findViewById(R.id.LinearLayoutInformacion)
        txtTituloCarrera = view.findViewById(R.id.txtTituloCarrera)
        btnAdminHome = view.findViewById(R.id.btnAdminHome)
        btnAdminHome.setOnClickListener{navigateToFragment(HomeFragment())}

        btnAdminCerrar = view.findViewById(R.id.btnAdminCerrar)
        btnAdminCerrar.setOnClickListener{
            activity?.finish()
        }
    }

    private fun loadRaceData() {
        lifecycleScope.launch {
            val response = raceService.getRaceById(raceId)
            if (response.isSuccessful) {
                currentRace = response.body()?.data
                currentRace?.let {
                    val isEnabled = it.enabled == 1
                    switchAdmin.isChecked = isEnabled
                    txtTituloCarrera.text = "Administrar carrera ${it.name}"
                    updateUI(isEnabled)
                    setupSwitchListener(it.id)
                }
            }
        }
    }

    private fun setupSwitchListener(raceId: Int) {
        switchAdmin.setOnCheckedChangeListener { _, isChecked ->
            val enabledValue = if (isChecked) 1 else 0
            currentRace?.let { race ->
                updateRaceData(race, enabledValue)
            }
            updateUI(isChecked)
        }
    }

    private fun updateRaceData(race: Race, enabledValue: Int) {
        Log.d("FinishRace","$currentRace")

        lifecycleScope.launch {
            val ucId = race.UC?.id ?: 1 // Si UC es nulo, usa un valor predeterminado para ucId, por ejemplo, 0

            raceService.updateRace(
                RaceDto(
                    id = race.id,
                    name = race.name,
                    date = race.date, // Asegúrate de que 'date' se refiere al campo correcto de 'race'
                    description = race.description,
                    UC = ucId, // 'UC' se pasa directamente como Int
                    enabled = enabledValue,
                    updateDate = race.updateDate // 'updateDate' se refiere al campo correcto de 'race'
                )
            )
        }
    }


    private fun updateUI(isEnabled: Boolean) {
        if (isEnabled) {
            txtStatusCarrera.text = getString(R.string.race_can_finish)
            txtLabelStatusAdmin.text = "Activado"
            linearLayoutInformacion.setBackgroundResource(R.color.button_stopped)
        } else {
            txtStatusCarrera.text = getString(R.string.race_cannot_finish)
            txtLabelStatusAdmin.text = "Desactivado"
            linearLayoutInformacion.setBackgroundResource(R.color.button_started)
        }
    }

    companion object {
        fun newInstance(raceId: Int): FinishRace {
            val fragment = FinishRace()
            val args = Bundle()
            args.putInt("raceId", raceId)
            fragment.arguments = args
            return fragment
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.home_container_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }
}
