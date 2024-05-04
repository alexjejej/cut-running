package com.cut.android.running.usecases.logros.admin

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.textfield.TextInputEditText
import android.widget.Button
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.cut.android.running.R
import com.cut.android.running.common.response.IResponse
import com.cut.android.running.models.Achievement
import com.cut.android.running.provider.RetrofitInstance
import com.cut.android.running.provider.services.AchievementService
import com.cut.android.running.usecases.home.HomeFragment
import kotlinx.coroutines.launch
import retrofit2.Response
import android.text.Editable
import android.text.TextWatcher
import com.cut.android.running.usecases.logros.LogrosFragment


class AdminLogrosFragment_add : Fragment() {

    private lateinit var nombreLogroEditText: TextInputEditText
    private lateinit var descripcionLogroEditText: TextInputEditText
    private lateinit var pasosLogroEditText: TextInputEditText
    private lateinit var fotoLogroEditText: TextInputEditText

    private val achievementService =
        RetrofitInstance.getRetrofit().create(AchievementService::class.java)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_logros_managment_add, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI(view)
    }

    private fun initializeUI(view: View) {
        nombreLogroEditText = view.findViewById(R.id.editNombreLogro)
        descripcionLogroEditText = view.findViewById(R.id.editDescripcionLogro)
        pasosLogroEditText = view.findViewById(R.id.editPasosLogro)
        fotoLogroEditText = view.findViewById(R.id.editFotoLogro)

        view.findViewById<Button>(R.id.btnAgregarLogro).setOnClickListener {
            agregarLogro()
        }
        setupTextWatchers()
        checkFieldsForEmptyValues()
    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // No es necesario para este caso
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                // Verifica si todos los campos están llenos y activa/desactiva el botón
                checkFieldsForEmptyValues()
            }

            override fun afterTextChanged(s: Editable?) {
                // No es necesario para este caso
            }
        }

        // Aplica el TextWatcher a todos los TextInputEditTexts
        nombreLogroEditText.addTextChangedListener(textWatcher)
        descripcionLogroEditText.addTextChangedListener(textWatcher)
        pasosLogroEditText.addTextChangedListener(textWatcher)
        fotoLogroEditText.addTextChangedListener(textWatcher)
    }

    private fun checkFieldsForEmptyValues() {
        val button = view?.findViewById<Button>(R.id.btnAgregarLogro)
        button?.isEnabled = nombreLogroEditText.text.toString().trim().isNotEmpty() &&
                descripcionLogroEditText.text.toString().trim().isNotEmpty() &&
                pasosLogroEditText.text.toString().trim().isNotEmpty() &&
                fotoLogroEditText.text.toString().trim().isNotEmpty()
    }


    private fun agregarLogro() {
        val nombreLogro = nombreLogroEditText.text.toString()
        val descripcionLogro = descripcionLogroEditText.text.toString()
        val pasosLogro = pasosLogroEditText.text.toString()
        val fotoLogro = fotoLogroEditText.text.toString()

        // Crea un objeto Achievement con los datos obtenidos
        val achievement = Achievement(
            name = nombreLogro,
            description = descripcionLogro,
            steps = pasosLogro.toInt(),
            photo = fotoLogro
        )

        // Llamada a la API en una corutina
        lifecycleScope.launch {
            val response: Response<IResponse<Boolean>> =
                achievementService.addAchievement(achievement)

            if (response.isSuccessful && response.body()?.data == true) {
                // Éxito al agregar el logro
                activity?.runOnUiThread {
                    AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom)
                        .setTitle("Éxito")
                        .setMessage("El logro \"$nombreLogro\" se agregó con éxito.")
                        .setPositiveButton("Ver logros") { dialog, which ->
                            navigateToFragment(LogrosFragment())
                        }
                        .setNegativeButton("Volver al inicio") { dialog, which ->
                            navigateToFragment(HomeFragment())
                        }
                        .show()
                }
            } else {
                // Manejo de errores
                activity?.runOnUiThread {
                    val errorMessage = response.errorBody()?.string() ?: "Error desconocido"
                    Toast.makeText(
                        requireContext(),
                        "Error al agregar el logro, error encontrado: $errorMessage",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.home_container_fragment, fragment)
        transaction.addToBackStack(null)  // Permite volver al fragmento anterior con el botón de retroceso
        transaction.commit()
    }
}
