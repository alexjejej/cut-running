package com.cut.android.running.usecases.logros.admin

import LogrosAdapter_delete
import LogrosViewModel_delete
import android.animation.ValueAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText
import com.cut.android.running.R
import com.cut.android.running.models.Achievement


class AdminLogrosFragment_update : Fragment() {

    private val viewModel: LogrosViewModel_delete by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var layoutSeleccionLogro: LinearLayout
    private lateinit var layoutInformacionEditar: LinearLayout


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logros_managment_update, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI(view)
        viewModel.mensajeError.observe(viewLifecycleOwner) { mensaje ->
            Toast.makeText(context, mensaje, Toast.LENGTH_LONG).show()
        }
    }

    private fun initializeUI(view: View) {
        layoutSeleccionLogro = view.findViewById(R.id.LayoutSeleccionLogro)
        layoutInformacionEditar = view.findViewById(R.id.LayoutInformacionEditar)

        recyclerView = view.findViewById(R.id.rvLogrosEliminar)
        recyclerView.layoutManager = LinearLayoutManager(context)

        val editNombreLogro = view.findViewById<TextInputEditText>(R.id.editNombreLogro)
        val editDescripcionLogro = view.findViewById<TextInputEditText>(R.id.editDescripcionLogro)
        val editPasosLogro = view.findViewById<TextInputEditText>(R.id.editPasosLogro)
        val editFotoLogro = view.findViewById<TextInputEditText>(R.id.editFotoLogro)
        val txtId = view.findViewById<TextView>(R.id.txtIdLogro)
        viewModel.obtenerLogros { logros ->
            val adapter = LogrosAdapter_delete(logros) { logro ->
                // Cargar datos del logro en los TextInputEditTexts
                txtId.text = "${logro.id}"
                editNombreLogro.setText(logro.name)
                editDescripcionLogro.setText(logro.description)
                editPasosLogro.setText(logro.steps.toString())
                editFotoLogro.setText(logro.photo)
                //Animación editar
                AnimacionLayoutEditar()
            }
            recyclerView.adapter = adapter
        }

        view.findViewById<MaterialButton>(R.id.btnAgregarLogro).setOnClickListener {
            val logroActualizado = Achievement(
                // Obtener id del logro actual y otros campos
                id = txtId.text.toString().toInt(),
                name = editNombreLogro.text.toString(),
                description = editDescripcionLogro.text.toString(),
                steps = editPasosLogro.text.toString().toInt(),
                photo = editFotoLogro.text.toString()
            )
            viewModel.actualizarLogro(logroActualizado) { exitoso ->
                if (exitoso) {
                    Toast.makeText(context, "Logro actualizado con éxito", Toast.LENGTH_SHORT).show()
                    // Actualiza la lista de logros y el adaptador del RecyclerView
                    viewModel.obtenerLogros { logros ->
                        (recyclerView.adapter as? LogrosAdapter_delete)?.updateLogros(logros)
                        AnimacionLayoutExito()
                    }
                } else {
                    Toast.makeText(context, "Error al actualizar el logro", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun AnimacionLayoutExito() {
        val anim = ValueAnimator.ofFloat(0f, 100f).apply {
            duration = 500 // Duración en milisegundos
            addUpdateListener {
                val valor = it.animatedValue as Float
                val params = layoutSeleccionLogro.layoutParams as LinearLayout.LayoutParams
                params.weight = valor
                layoutSeleccionLogro.layoutParams = params

                val paramsInfo = layoutInformacionEditar.layoutParams as LinearLayout.LayoutParams
                paramsInfo.weight = 100f - valor
                layoutInformacionEditar.layoutParams = paramsInfo
            }
        }
        anim.start()
    }

    private fun AnimacionLayoutEditar() {
        val anim = ValueAnimator.ofFloat(100f, 0f).apply {
            duration = 500 // Duración en milisegundos
            addUpdateListener {
                //Cambiar pesos de los layouts
                val valor = it.animatedValue as Float
                val params = layoutSeleccionLogro.layoutParams as LinearLayout.LayoutParams
                params.weight = valor
                layoutSeleccionLogro.layoutParams = params

                val paramsInfo = layoutInformacionEditar.layoutParams as LinearLayout.LayoutParams
                paramsInfo.weight = 100f - valor
                layoutInformacionEditar.layoutParams = paramsInfo
            }
        }
        anim.start()
    }

    fun onBackPressed() {
        val anim = ValueAnimator.ofFloat(100f, 0f).apply {
            duration = 500 // Duración en milisegundos
            addUpdateListener {
                //Cambiar pesos de los layouts
                val valor = it.animatedValue as Float
                val params = layoutSeleccionLogro.layoutParams as LinearLayout.LayoutParams
                params.weight = valor
                layoutSeleccionLogro.layoutParams = params

                val paramsInfo = layoutInformacionEditar.layoutParams as LinearLayout.LayoutParams
                paramsInfo.weight = 100f - valor
                layoutInformacionEditar.layoutParams = paramsInfo
            }
        }
        anim.start()
    }




}
