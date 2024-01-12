package com.raywenderlich.android.rwandroidtutorial.Home

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.Carrera.MapsActivity
import com.raywenderlich.android.rwandroidtutorial.Logros.LogrosFragment
import com.raywenderlich.android.rwandroidtutorial.clasificacion.ClasificacionFragment
import com.raywenderlich.android.rwandroidtutorial.provider.DatosUsuario


class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Obtener el nombre del usuario de SharedPreferences
        val userName = DatosUsuario.getUserName(requireContext()) ?: "Error"

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        // Observar cambios en el ViewModel
        viewModel.usuario.observe(viewLifecycleOwner, Observer { nombre ->
            // Actualizar el mensaje de bienvenida
            view.findViewById<TextView>(R.id.bienvenidaTextView).text = "Bienvenido, $userName"
        })

        view.findViewById<Button>(R.id.btnCarrera).setOnClickListener {
            val intent = Intent(activity, MapsActivity::class.java)
            startActivity(intent)
        }

        view.findViewById<Button>(R.id.btnLogros).setOnClickListener {
            navigateToFragment(LogrosFragment())
        }

        view.findViewById<Button>(R.id.btnGestionCarreras).setOnClickListener {
            //navigateToFragment(GestionCarrerasFragment())
        }

        view.findViewById<Button>(R.id.btnClasificacion).setOnClickListener {
            navigateToFragment(ClasificacionFragment())
        }
    }
    /** Navega al fragmento dado como parametro **/
    private fun navigateToFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.home_container_fragment, fragment)
        transaction.addToBackStack(null)  // Permite volver al fragmento anterior con el botón de retroceso
        transaction.commit()
    }



}