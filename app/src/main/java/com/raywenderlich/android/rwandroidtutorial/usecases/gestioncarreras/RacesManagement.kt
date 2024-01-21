package com.raywenderlich.android.rwandroidtutorial.usecases.gestioncarreras

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.runtracking.databinding.FragmentRacesManagementBinding
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

    val raceAdapter: RaceAdapter = RaceAdapter()

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
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = RacesManagement()
    }

    private fun getRaces() {
        CoroutineScope(Dispatchers.IO).launch {
            val call = RetrofitInstance.getRetrofit().create(RaceService::class.java).getRaces()
            val races = call.body()
            requireActivity().runOnUiThread {
                if (races!!.isSuccess) {
                    Log.d(TAG, races.toString())
                    raceAdapter.races = races.data!!
                }
                else {
                    Log.e(TAG, "Errores")
                    Log.e(TAG, races.message!!)
                }
            }
        }
    }
}