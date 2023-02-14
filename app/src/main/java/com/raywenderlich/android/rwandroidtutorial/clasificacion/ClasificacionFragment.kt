package com.raywenderlich.android.rwandroidtutorial.clasificacion

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.runtracking.databinding.FragmentClasificacionBinding
import com.raywenderlich.android.rwandroidtutorial.clasificacion.adapter.ListaClasificacionAdapter

class ClasificacionFragment : Fragment() {
    private val clasificacionViewModel = ClasificacionViewModel()
    private lateinit var binding: FragmentClasificacionBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_clasificacion, container, false)
        binding = FragmentClasificacionBinding.bind(rootView)

        this.setup()
        return rootView
    }

    private fun setup() {
        this.initRecycleView()
    }

    private fun initRecycleView() {
        val manager = LinearLayoutManager(context)
        val decorartion = DividerItemDecoration(context, manager.orientation)
        binding.rvClassification.layoutManager = manager
        binding.rvClassification.adapter = ListaClasificacionAdapter(clasificacionViewModel.listaClasificacion)
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