package com.cut.android.running.usecases.profile

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cut.android.running.R
import com.cut.android.running.provider.BDsqlite
import com.cut.android.running.provider.DatosUsuario
import com.cut.android.running.provider.RetrofitInstance
import com.cut.android.running.provider.services.UserService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * A simple [Fragment] subclass.
 * Use the [UserInfoUpdateFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class UserInfoUpdateFragment : Fragment() {
    private lateinit var editTexts: List<EditText>
    private lateinit var saveButton: Button
    private lateinit var backButton: Button
    private lateinit var SpecialityIDSpinner: Spinner
    private val userService: UserService = RetrofitInstance.getRetrofit().create(
        UserService::class.java)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_user_info_update, container, false)
        initializeViews(view)
        setupTextWatchers()
        return view
    }

    private fun initializeViews(view: View) {
        editTexts = listOf(
            view.findViewById(R.id.edadEditTextUpdate),
            view.findViewById(R.id.estaturaEditTextUpdate),
            view.findViewById(R.id.pesoEditTextUpdate),
            view.findViewById(R.id.codigoEditTextUpdate)
        )

        SpecialityIDSpinner = view.findViewById(R.id.SpecialityIDSpinnerUpdate)
        setupCentroUniversitarioSpinner()

        saveButton = view.findViewById<Button>(R.id.botonGuardarUpdate).apply {
            setOnClickListener { GuardarDatosAPI() }
        }

        backButton = view.findViewById<Button>(R.id.botonVolverUpdate).apply {
            setOnClickListener { navigateToFragment(ProfileFragment()) }
        }
    }

    private fun setupCentroUniversitarioSpinner() {
        // Crea un ArrayAdapter usando un array de strings y un layout por defecto para el spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.centros_universitarios_array,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Especifica el layout a usar cuando aparece la lista de opciones
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Aplica el adaptador al spinner
            SpecialityIDSpinner.adapter = adapter
        }
    }


    private fun GuardarDatosAPI() {
        val email = DatosUsuario.getEmail(requireActivity()) ?: return

        // Realiza la lógica en una coroutina
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val responseActual = userService.getUserByEmail(email)
                val usuarioActual = responseActual.body()?.data

                // Construir objeto User con los nuevos datos
                val userActualizado = usuarioActual?.copy(
                    age = editTexts[0].text.toString().takeIf { it.isNotBlank() }?.toIntOrNull(),
                    height = editTexts[1].text.toString().takeIf { it.isNotBlank() }?.toIntOrNull(),
                    weight = editTexts[2].text.toString().takeIf { it.isNotBlank() }?.toIntOrNull(),
                    specialtyId = when (SpecialityIDSpinner.selectedItem.toString()) {
                        "Ingeniería en Ciencias Computacionales" -> 1
                        "Ingeniería en Nanotecnología" -> 2
                        else -> null
                    },
                    code = editTexts[3].text.toString().takeIf { it.isNotBlank() }?.toIntOrNull(),
                    email = email
                )

                // Verificar si hay cambios antes de llamar a la API
                if (usuarioActual != userActualizado) {
                    var updateResponse = userService.updateUser(userActualizado!!)

                    withContext(Dispatchers.Main) {
                        if (updateResponse.isSuccessful && updateResponse.body()?.isSuccess == true) {
                            Toast.makeText(requireContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                            GuardarDatosSQLite()
                        } else {
                            Log.e("API Error", "Respuesta fallida: ${updateResponse.errorBody()?.string()}")
                            Toast.makeText(requireContext(), "Error al actualizar datos en la API", Toast.LENGTH_SHORT).show()                        }
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(requireContext(), "No hay cambios para actualizar", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                    Log.e("API Error", "Error al actualizar datos: ", e)
                }
            }
        }
    }




    private fun GuardarDatosSQLite() {
        val email = DatosUsuario.getEmail(requireActivity())

        // Obtener referencias a los EditTexts (asumiendo que ya están inicializadas en 'initializeViews')
        val edadEditText = editTexts[0]
        val estaturaEditText = editTexts[1]
        val pesoEditText = editTexts[2]
        val codigoEditText = editTexts[3]


        val values = ContentValues()

        // Agregar solo si el campo no está vacío
        edadEditText.text.toString().takeIf { it.isNotBlank() }?.let { edad ->
            values.put(BDsqlite.COLUMN_EDAD, edad.toInt())
        }
        estaturaEditText.text.toString().takeIf { it.isNotBlank() }?.let { estatura ->
            values.put(BDsqlite.COLUMN_ESTATURA, estatura.toInt())
        }
        pesoEditText.text.toString().takeIf { it.isNotBlank() }?.let { peso ->
            values.put(BDsqlite.COLUMN_PESO, peso.toInt())
        }
        codigoEditText.text.toString().takeIf { it.isNotBlank() }?.let { edad ->
            values.put(BDsqlite.COLUMN_CODE, edad.toInt())
        }
        // Obtener specialtyId del Spinner
        val specialtyId = when (SpecialityIDSpinner.selectedItem.toString()) {
            "Ingeniería en Ciencias Computacionales" -> 1
            "Ingeniería en Nanotecnología" -> 2
            else -> null
        }

        specialtyId?.let {
            values.put(BDsqlite.COLUMN_SPECIALITYID, it)
        }

        // Guardar datos en SQLite
        val myDatabase = BDsqlite(requireContext())
        try{
            myDatabase.insertOrUpdate(email, values)
            navigateToFragment(ProfileFragment())
        }catch (e: Exception){
            Log.d("UF","No se guardó en la bd, $e")
        }


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
