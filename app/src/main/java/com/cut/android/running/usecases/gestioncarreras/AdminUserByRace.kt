package com.cut.android.running.usecases.gestioncarreras

import RaceManagementViewModel
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import com.cut.android.running.databinding.FragmentAdminUserByRaceBinding
import com.cut.android.running.usecases.gestioncarreras.adapter.UserAdapter
import com.cut.android.running.models.User

/**
 * A simple [Fragment] subclass.
 * Use the [AdminUserByRace.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminUserByRace () : Fragment() {
    private var raceId: Int? = null
    private var _binding: FragmentAdminUserByRaceBinding? = null
    private val binding get() = _binding!!
    private val TAG: String = this::class.java.simpleName
    private val raceManagementViewModel: RaceManagementViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            raceId = it.getInt(RACE_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminUserByRaceBinding.inflate(inflater, container, false)
        val userAdapter = UserAdapter()
        binding.rcvUsers.adapter = userAdapter
        binding.rcvUsers.setHasFixedSize(true)

        // Uso de user view model para la obtencion de los usuarios
        raceManagementViewModel.getUserByRaceModel.observe(viewLifecycleOwner, Observer {
            if (!it.isNullOrEmpty()) {
                userAdapter.users = it
            }
        })
        raceManagementViewModel.getUserByRace(raceId!!)

        return binding.root
    }

    companion object {
        const val RACE_ID = "raceId"
        @JvmStatic
        fun newInstance(raceId: Int) = AdminUserByRace().apply {
            arguments = Bundle().apply {
                putInt(RACE_ID, raceId)
            }
        }
    }
}