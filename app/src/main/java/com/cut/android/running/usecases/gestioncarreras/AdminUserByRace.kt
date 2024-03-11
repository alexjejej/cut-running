package com.cut.android.running.usecases.gestioncarreras

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.cut.android.running.R
import com.cut.android.running.databinding.FragmentAdminUserByRaceBinding
import com.cut.android.running.usecases.gestioncarreras.adapter.UserAdapter

/**
 * A simple [Fragment] subclass.
 * Use the [AdminUserByRace.newInstance] factory method to
 * create an instance of this fragment.
 */
class AdminUserByRace : Fragment() {
    private var _binding: FragmentAdminUserByRaceBinding? = null
    private val binding get() = _binding!!
    private val TAG: String = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminUserByRaceBinding.inflate(inflater, container, false)
        binding.rcvUsers.adapter = UserAdapter()
        binding.rcvUsers.setHasFixedSize(true)

        // Uso de user view model para la obtencion de los usuarios

        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() = AdminUserByRace()
    }
}