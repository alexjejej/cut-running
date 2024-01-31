package com.raywenderlich.android.rwandroidtutorial.usecases.logros.admin

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import android.widget.Button
import com.raywenderlich.android.runtracking.R


class AdminLogrosFragment : Fragment() {


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logros_managment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI(view)
    }

    private fun initializeUI(view: View) {


        view.findViewById<Button>(R.id.btnAgregarLogro).setOnClickListener {
            navigateToFragment(AdminLogrosFragment_add())
        }
        view.findViewById<Button>(R.id.btnEliminarLogro).setOnClickListener {
            navigateToFragment(AdminLogrosFragment_delete())
        }
        view.findViewById<Button>(R.id.btnEditarLogro).setOnClickListener {
            navigateToFragment(AdminLogrosFragment_update())
        }

    }



    private fun navigateToFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.home_container_fragment, fragment)
        transaction.addToBackStack(null)  // Permite volver al fragmento anterior con el botón de retroceso
        transaction.commit()
    }
}
