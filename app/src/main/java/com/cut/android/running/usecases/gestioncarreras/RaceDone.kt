package com.cut.android.running.usecases.gestioncarreras

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cut.android.running.Carreras.Adapter.RankingAdapter
import com.cut.android.running.R
import com.cut.android.running.databinding.FragmentRaceDoneBinding
import com.cut.android.running.models.Race
import com.cut.android.running.models.RaceResult
import com.cut.android.running.models.RankingItem
import com.cut.android.running.provider.DatosUsuario
import com.cut.android.running.provider.RetrofitInstance
import com.cut.android.running.provider.services.RaceResultService
import com.cut.android.running.usecases.home.HomeFragment
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class RaceDone : Fragment() {
    private var race: Race? = null
    private lateinit var recyclerView: RecyclerView
    private lateinit var txtEventoNombre: TextView
    private lateinit var txtEventoFelicidades: TextView
    private lateinit var btnFinEventoHome: Button
    private lateinit var btnFinEventoCerrar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            race = it.getSerializable("race") as? Race
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentRaceDoneBinding.inflate(inflater, container, false)

        recyclerView = binding.rvRankingCarrera
        recyclerView.layoutManager = LinearLayoutManager(context)
        txtEventoNombre = binding.txtEventoNombre
        txtEventoFelicidades = binding.txtEventoFelicidades
        btnFinEventoCerrar = binding.btnFinEventoCerrar
        btnFinEventoHome = binding.btnFinEventoHome

        btnFinEventoCerrar.setOnClickListener {
            activity?.finish()
        }

        btnFinEventoHome.setOnClickListener {
            navigateToFragment(HomeFragment())
        }

        race?.let {
            txtEventoNombre.text = "Resultados de la carrera ${it.name}"
            cargarDatos(it.id)
        }

        return binding.root
    }

    private fun cargarDatos(idCarrera: Int) {
        val raceResultService = RetrofitInstance.getRetrofit().create(RaceResultService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = raceResultService.getResultsByRaceId(idCarrera)
                if (response.isSuccessful && response.body() != null) {
                    val raceResults = response.body()!!.data
                    val rankingItems = raceResults?.map { raceResult ->
                        // Suponiendo que RankingItem tiene un constructor que acepta los valores adecuados de RaceResult
                        RankingItem(raceResult.position!!, raceResult.userName, raceResult.time)
                    } ?: listOf()

                    withContext(Dispatchers.Main) {
                        recyclerView.adapter = RankingAdapter(rankingItems)
                        actualizarUIConDetallesDeCarrera(raceResults!!)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.e("RaceDone", "Error al cargar datos: ${response.errorBody()?.string()}")
                        // Manejar el error, por ejemplo, mostrando un mensaje al usuario
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.e("RaceDone", "Excepción al cargar datos", e)
                    // Manejar la excepción, por ejemplo, mostrando un mensaje al usuario
                }
            }
        }

    }
    private fun actualizarUIConDetallesDeCarrera(raceResults: List<RaceResult>) {
        val usuario = DatosUsuario.getUserName(requireContext())
        val email = DatosUsuario.getEmail(requireActivity())
        val raceResult = raceResults.find { it.userEmail == email }

        if (raceResult != null) {
            val position = raceResult.position ?: 0
            val time = raceResult.time ?: "N/A"

            val textoBase = "Felicidades $usuario, obtuviste el $position lugar en la carrera con un tiempo de $time minutos"
            val spannableString = SpannableString(textoBase)

            // Estilo para 'position'
            val positionStart = textoBase.indexOf("$position")
            val positionEnd = positionStart + "$position".length
            spannableString.setSpan(StyleSpan(Typeface.BOLD), positionStart, positionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(ForegroundColorSpan(Color.BLUE), positionStart, positionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            // Estilo para 'time'
            val timeStart = textoBase.indexOf(time)
            val timeEnd = timeStart + time.length
            spannableString.setSpan(StyleSpan(Typeface.ITALIC), timeStart, timeEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
            spannableString.setSpan(ForegroundColorSpan(Color.RED), timeStart, timeEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

            txtEventoFelicidades.text = spannableString
        } else {
            // Manejar caso donde no se encuentra el resultado de la carrera para el usuario
            txtEventoFelicidades.text = "No hay datos tuyos guardados en esta carrera"
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.home_container_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }

}
