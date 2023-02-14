package com.raywenderlich.android.rwandroidtutorial.Logros

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.runtracking.databinding.FragmentLogrosBinding
import com.raywenderlich.android.rwandroidtutorial.Logros.adapter.ListaLogrosAdapter

class LogrosFragment : Fragment() {
    private val logrosViewModel = LogrosViewModel()
    private lateinit var binding: FragmentLogrosBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_logros, container, false)
        binding = FragmentLogrosBinding.bind(rootView)

        this.setup()
        return rootView
    }

    private fun setup() {
        this.initRecycleView()
    }

    private fun initRecycleView() {
        val manager = LinearLayoutManager(context)
        val decoration = DividerItemDecoration(context, manager.orientation)
        binding.rvLogrosF.layoutManager = manager
        binding.rvLogrosF.adapter = ListaLogrosAdapter(logrosViewModel.listaLogros)
        binding.rvLogrosF.addItemDecoration(decoration)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance() = LogrosFragment()
    }
}