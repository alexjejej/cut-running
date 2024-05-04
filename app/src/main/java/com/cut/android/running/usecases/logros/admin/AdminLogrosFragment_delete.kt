package com.cut.android.running.usecases.logros.admin

import LogrosAdapter_delete
import LogrosViewModel_delete
import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cut.android.running.R
import com.cut.android.running.models.Achievement


class AdminLogrosFragment_delete : Fragment() {

    private val viewModel: LogrosViewModel_delete by viewModels()
    private lateinit var recyclerView: RecyclerView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logros_managment_delete, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI(view)
        viewModel.mensajeError.observe(viewLifecycleOwner) { mensaje ->
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeUI(view: View) {
        recyclerView = view.findViewById(R.id.rvLogrosEliminar)
        recyclerView.layoutManager = LinearLayoutManager(context)

        viewModel.obtenerLogros { logros ->
            val adapter = LogrosAdapter_delete(logros) { logroSeleccionado ->
                mostrarDialogoConfirmacion(logroSeleccionado)
            }
            recyclerView.adapter = adapter
        }

    }


    private fun mostrarDialogoConfirmacion(logro: Achievement) {
        AlertDialog.Builder(context, R.style.AlertDialogCustom)
            .setTitle("Confirmación")
            .setMessage("¿Estás seguro de que quieres desactivar el logro '${logro.name}'?")
            .setPositiveButton("Desactivar") { dialog, which ->
                // Ajusta el logro para desactivarlo
                logro.enabled = 0
                viewModel.desactivarLogro(logro) { exitoso ->
                    if (exitoso) {
                        Toast.makeText(context, "Logro desactivado con éxito", Toast.LENGTH_SHORT).show()
                        // Actualiza la lista de logros y el adaptador del RecyclerView
                        viewModel.obtenerLogros { logros ->
                            (recyclerView.adapter as? LogrosAdapter_delete)?.updateLogros(logros)
                        }
                    } else {
                        Toast.makeText(context, "Error al desactivar el logro", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }


    private fun navigateToFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.home_container_fragment, fragment)
        transaction.addToBackStack(null)  // Permite volver al fragmento anterior con el botón de retroceso
        transaction.commit()
    }
}
