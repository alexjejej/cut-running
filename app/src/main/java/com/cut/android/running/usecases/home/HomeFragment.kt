package com.cut.android.running.usecases.home

import LogroConseguidoFragment
import MapsFragment
import RacesManagement
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cut.android.running.R
import com.cut.android.running.usecases.clasificacion.ClasificacionFragment
import com.cut.android.running.provider.DatosUsuario
import com.cut.android.running.usecases.logros.LogrosFragment
import com.cut.android.running.usecases.logros.admin.AdminLogrosFragment
import com.cut.android.running.usecases.profile.ProfileFragment


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel
    private lateinit var btnCarrera: Button
    private lateinit var btnLogros: Button
    private lateinit var btnClasificacion: Button
    private lateinit var btnAdminLogros: Button
    private lateinit var btnAdminCarreras: Button
    private lateinit var btnPerfil: Button

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI(view)
        setupViewModel()
    }

    private fun initializeUI(view: View) {
        btnCarrera = view.findViewById(R.id.btnCarrera)
        btnLogros = view.findViewById(R.id.btnLogros)
        btnClasificacion = view.findViewById(R.id.btnClasificacion)
        btnAdminLogros = view.findViewById(R.id.btnAdminLogros)
        btnAdminCarreras = view.findViewById(R.id.btnAdminCarreras)
        btnPerfil = view.findViewById(R.id.btnProfile)

        btnCarrera.setOnClickListener { navigateToFragment(MapsFragment()) }
        btnLogros.setOnClickListener { navigateToFragment(LogrosFragment()) }
        btnClasificacion.setOnClickListener { navigateToFragment(ClasificacionFragment()) }
        btnAdminLogros.setOnClickListener { navigateToFragment(AdminLogrosFragment()) }
        btnAdminCarreras.setOnClickListener{ navigateToFragment(RacesManagement()) }
        btnPerfil.setOnClickListener{ navigateToFragment(ProfileFragment()) }
    }

    private fun setupViewModel() {
        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)
        viewModel.usuario.observe(viewLifecycleOwner) { nombre ->
            updateWelcomeMessage(nombre)
        }
        viewModel.roleId.observe(viewLifecycleOwner) { roleId ->
            updateAdminOptionsVisibility(roleId)
        }
        viewModel.checkUserRole(DatosUsuario.getEmail(requireActivity()))
    }

    private fun updateWelcomeMessage(userName: String) {
        //var userName2 = DatosUsuario.getUserName(requireContext())
        Log.d("username","$userName")
        view?.findViewById<TextView>(R.id.bienvenidaTextView)?.text = "Bienvenido, $userName"
    }

    private fun updateAdminOptionsVisibility(roleId: Int?) {
        Log.d("HF","ROLEID: $roleId")
        val isAdmin = roleId == 1
        btnAdminCarreras.visibility = if (isAdmin) View.VISIBLE else View.GONE
        btnAdminLogros.visibility = if (isAdmin) View.VISIBLE else View.GONE
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.home_container_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }
}
