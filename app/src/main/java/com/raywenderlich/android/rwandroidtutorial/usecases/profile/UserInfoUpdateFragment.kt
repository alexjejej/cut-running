package com.raywenderlich.android.rwandroidtutorial.usecases.profile

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.provider.BDsqlite
import com.raywenderlich.android.rwandroidtutorial.provider.DatosUsuario

/**
 * A simple [Fragment] subclass.
 * Use the [UserInfoUpdateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserInfoUpdateFragment : Fragment() {
    private lateinit var editTexts: List<EditText>
    private lateinit var saveButton: Button
    private lateinit var backButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user_info_update, container, false)
        //Iniciar viewers para optimizar app
        initializeViews(view)
        //Funcionas para activar el boton de guardar
        setupTextWatchers()
        checkFieldsForEmptyValues()
        //Acciones de los botones
        view.findViewById<Button>(R.id.botonVolverUpdate).setOnClickListener {
            navigateToFragment(ProfileFragment())
        }
        view.findViewById<Button>(R.id.botonGuardarUpdate).setOnClickListener {
            GuardarDatos(view)
        }

        return view
    }


    private fun GuardarDatos(view: View) {
        // Obtener el nombre del usuario
        val nombreUsuario = DatosUsuario.getUserName(requireContext()) ?: "Error"

        // Obtener referencias a los EditTexts
        val edadEditText = view.findViewById<EditText>(R.id.edadEditTextUpdate)
        val estaturaEditText = view.findViewById<EditText>(R.id.estaturaEditTextUpdate)
        val pesoEditText = view.findViewById<EditText>(R.id.pesoEditTextUpdate)
        val centroUniversitarioEditText = view.findViewById<EditText>(R.id.centroUniversitarioEditTextUpdate)
        val carreraEditText = view.findViewById<EditText>(R.id.carreraEditTextUpdate)

        val botonGuardar = view.findViewById<Button>(R.id.botonGuardarUpdate)
        botonGuardar.setOnClickListener {
            val values = ContentValues()

            // Agregar solo si el campo no está vacío
            edadEditText.text.toString().takeIf { it.isNotBlank() }?.let { edad ->
                values.put(BDsqlite.COLUMN_EDAD, edad)
            }
            estaturaEditText.text.toString().takeIf { it.isNotBlank() }?.let { estatura ->
                values.put(BDsqlite.COLUMN_ESTATURA, estatura)
            }
            pesoEditText.text.toString().takeIf { it.isNotBlank() }?.let { peso ->
                values.put(BDsqlite.COLUMN_PESO, peso)
            }
            centroUniversitarioEditText.text.toString().takeIf { it.isNotBlank() }?.let { centroUniversitario ->
                values.put(BDsqlite.COLUMN_CENTRO_UNIVERSITARIO, centroUniversitario)
            }
            carreraEditText.text.toString().takeIf { it.isNotBlank() }?.let { carrera ->
                values.put(BDsqlite.COLUMN_CARRERA, carrera)
            }

            // Guardar datos en la base de datos
            val myDatabase = BDsqlite(context)
            myDatabase.insertOrUpdate(nombreUsuario, values)

            // Mensaje de éxito
            Toast.makeText(context, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()

            // Volver al perfil
            navigateToFragment(ProfileFragment())
        }
    }




    private fun initializeViews(view: View) {
        editTexts = listOf(
            view.findViewById(R.id.edadEditTextUpdate),
            view.findViewById(R.id.estaturaEditTextUpdate),
            view.findViewById(R.id.pesoEditTextUpdate),
            view.findViewById(R.id.centroUniversitarioEditTextUpdate),
            view.findViewById(R.id.carreraEditTextUpdate)
        )

        saveButton = view.findViewById(R.id.botonGuardarUpdate)

    }

    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                checkFieldsForEmptyValues()
            }
        }

        editTexts.forEach { it.addTextChangedListener(textWatcher) }
    }

    private fun checkFieldsForEmptyValues() {
        saveButton.isEnabled = editTexts.any { it.text.isNotEmpty() }
    }

    /** Navega al fragmento dado como parametro **/
    private fun navigateToFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.home_container_fragment, fragment)
        transaction.addToBackStack(null)  // Permite volver al fragmento anterior con el botón de retroceso
        transaction.commit()
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) = UserInfoUpdateFragment()
    }
}
