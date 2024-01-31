package com.raywenderlich.android.rwandroidtutorial.usecases.home

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.Carrera.MapsActivity
import com.raywenderlich.android.rwandroidtutorial.usecases.logros.LogrosFragment
import com.raywenderlich.android.rwandroidtutorial.usecases.clasificacion.ClasificacionFragment
import com.raywenderlich.android.rwandroidtutorial.provider.DatosUsuario
import com.raywenderlich.android.rwandroidtutorial.usecases.gestioncarreras.RacesManagement
import com.raywenderlich.android.rwandroidtutorial.usecases.logros.admin.AdminLogrosFragment


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel
    private lateinit var btnCarrera: Button
    private lateinit var btnLogros: Button
    private lateinit var btnClasificacion: Button
    private lateinit var btnAdminLogros: Button
    private lateinit var btnAdminCarreras: Button

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

        btnCarrera.setOnClickListener { openMapsActivity() }
        btnLogros.setOnClickListener { navigateToFragment(LogrosFragment()) }
        btnClasificacion.setOnClickListener { navigateToFragment(ClasificacionFragment()) }
        btnAdminLogros.setOnClickListener { navigateToFragment(AdminLogrosFragment()) }
        btnAdminCarreras.setOnClickListener{ navigateToFragment(RacesManagement()) }
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

    private fun openMapsActivity() {
        val intent = Intent(activity, MapsActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.home_container_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }
}
