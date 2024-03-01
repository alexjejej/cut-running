package com.cut.android.running.usecases.clasificacion

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.cut.android.running.R
import com.cut.android.running.databinding.FragmentClasificacionBinding
import com.cut.android.running.provider.resources.Presets
import com.cut.android.running.usecases.clasificacion.adapter.ListaClasificacionAdapter
import nl.dionsegijn.konfetti.xml.KonfettiView

class ClasificacionFragment : Fragment() {
    private val clasificacionViewModel = ClasificacionViewModel()
    private lateinit var binding: FragmentClasificacionBinding
    private lateinit var viewKonfetti: KonfettiView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_clasificacion, container, false)
        viewKonfetti = rootView.findViewById(R.id.konfettiViewPodio)
        binding = FragmentClasificacionBinding.bind(rootView)

        this.setup()
        return rootView
    }

    /** Funcion de inicio de configuracion **/
    private fun setup() {
        this.initRecycleView()

        // Observer de LiveData del ViewModel
        clasificacionViewModel.listaClasificacion.observe(viewLifecycleOwner, Observer { clasificaciones ->
            val listaOrdenada = clasificaciones.sortedByDescending { it.pasos }

            // Asegúrate de que hay al menos 3 elementos para el podio
            if (listaOrdenada.size >= 3) {
                // Actualiza los TextViews del podio aquí
                val primerLugar = listaOrdenada[0]
                val segundoLugar = listaOrdenada[1]
                val tercerLugar = listaOrdenada[2]

                binding.txt1NombrePodio.text = primerLugar.nombre
                binding.txt2NombrePodio.text = segundoLugar.nombre
                binding.txt3NombrePodio.text = tercerLugar.nombre

                // Asegúrate de hacer visible el layout del podio si estaba oculto
                binding.podioLayout.visibility = View.VISIBLE
            }

            // Configura el RecyclerView para el resto de la lista
            if (listaOrdenada.size > 3) {
                val restoDeClasificaciones = listaOrdenada.drop(0)
                binding.rvClassification.adapter = ListaClasificacionAdapter(restoDeClasificaciones, requireContext())
            } else {
                // Manejar el caso donde hay menos de 3 elementos para evitar errores
                binding.rvClassification.adapter = ListaClasificacionAdapter(emptyList(), requireContext())
            }
        })
        startRandomConfettiAnimation()

    }
    private fun startRandomConfettiAnimation() {
        val randomParty = when ((1..4).random()) {
            1 -> Presets.festive()
            3 -> Presets.parade()
            4 -> Presets.rain()
            else -> emptyList()
        }

        viewKonfetti.start(randomParty)
    }
    /** Inicializacion de Recycleview **/
    private fun initRecycleView() {
        val manager = LinearLayoutManager(context)
        val decorartion = DividerItemDecoration(context, manager.orientation)
        binding.rvClassification.layoutManager = manager
        binding.rvClassification.setHasFixedSize(true)
        binding.rvClassification.addItemDecoration(decorartion)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = ClasificacionFragment()
    }
}