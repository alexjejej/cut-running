package com.cut.android.running.usecases.profile

import android.content.ContentValues
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.cut.android.running.R
import com.cut.android.running.provider.BDsqlite
import com.cut.android.running.provider.DatosUsuario
import com.cut.android.running.provider.RetrofitInstance
import com.cut.android.running.provider.services.UserService
import com.google.android.material.textfield.TextInputLayout
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
            view.findViewById(R.id.codigoEditTextUpdate),
            view.findViewById(R.id.distanciaPromedioPorPasoEditText)
        )

        SpecialityIDSpinner = view.findViewById(R.id.SpecialityIDSpinnerUpdate)
        setupCentroUniversitarioSpinner()

        saveButton = view.findViewById<Button>(R.id.botonGuardarUpdate).apply {
            setOnClickListener {
                if (validarCampos()) {
                    GuardarDatosAPI()
                }
            }
        }

        backButton = view.findViewById<Button>(R.id.botonVolverUpdate).apply {
            setOnClickListener { navigateToFragment(ProfileFragment()) }
        }

        // Definir el filtro
        val filter = InputFilter { source, start, end, dest, dstart, dend ->
            for (index in start until end) {
                // Caracteres a filtrar
                if (source[index] == '.' || source[index] == ' ' || source[index] == ',' || source[index] == '-') {
                    return@InputFilter ""
                }
            }
            null // Retorna null para aceptar el original
        }

        // Aplicar el filtro a cada EditText en la lista
        editTexts.forEach { editText ->
            val currentFilters = editText.filters.toMutableList()
            currentFilters.add(filter)
            editText.filters = currentFilters.toTypedArray()
        }

        //Menú configuración avanzada
        val tvConfigAvanzadas = view.findViewById<TextView>(R.id.tvConfigAvanzadas)
        val llConfigAvanzadas = view.findViewById<LinearLayout>(R.id.llConfigAvanzadas)

        tvConfigAvanzadas.setOnClickListener {
            if (llConfigAvanzadas.visibility == View.GONE) {
                llConfigAvanzadas.visibility = View.VISIBLE
            } else {
                llConfigAvanzadas.visibility = View.GONE
            }
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

        // Añadir el listener
        SpecialityIDSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                checkFieldsForEmptyValues() // Verifica los campos cada vez que cambia la selección
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Opcionalmente puedes manejar el caso de "nada seleccionado" si es necesario
            }
        }
    }


    private fun GuardarDatosAPI() {
        Log.d("UserInfoUpdateFragment","entramos a api")

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
                    email = email,
                    distanceperstep = editTexts[4].text.toString().takeIf { it.isNotBlank() }?.toIntOrNull()
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


    private fun validarCampos(): Boolean {
        // Inicializar la bandera de validez como true
        var esValido = true


        // Validación para el campo código
        val codigo = view?.findViewById<EditText>(R.id.codigoEditTextUpdate)?.text.toString()
        if (codigo.isNotEmpty() && codigo.length != 9) {
            view?.findViewById<TextInputLayout>(R.id.codigoTextInputLayout)?.error = "El código debe tener 9 números"
            esValido = false
        } else {
            view?.findViewById<TextInputLayout>(R.id.codigoTextInputLayout)?.error = null
        }

        // Validación para el campo edad
        val edad = view?.findViewById<EditText>(R.id.edadEditTextUpdate)?.text.toString().toIntOrNull()
        when {
            edad != null && edad <= 10 -> {
                view?.findViewById<TextInputLayout>(R.id.edadTextInputLayout)?.error = "No se permiten peques"
                esValido = false
            }
            edad != null && edad >= 100 -> {
                view?.findViewById<TextInputLayout>(R.id.edadTextInputLayout)?.error = "Mmmmm no creo que tengas esa edad"
                esValido = false
            }
            else -> view?.findViewById<TextInputLayout>(R.id.edadTextInputLayout)?.error = null
        }

        // Validación para el campo estatura
        val estatura = view?.findViewById<EditText>(R.id.estaturaEditTextUpdate)?.text.toString().toIntOrNull()
        when {
            estatura != null && estatura <= 100 -> {
                view?.findViewById<TextInputLayout>(R.id.estaturaTextInputLayout)?.error = "No se aceptan minions"
                esValido = false
            }
            estatura != null && estatura >= 250 -> {
                view?.findViewById<TextInputLayout>(R.id.estaturaTextInputLayout)?.error = "No se aceptan jirafas"
                esValido = false
            }
            else -> view?.findViewById<TextInputLayout>(R.id.estaturaTextInputLayout)?.error = null
        }

        // Validación para el campo peso
        val peso = view?.findViewById<EditText>(R.id.pesoEditTextUpdate)?.text.toString().toIntOrNull()
        when {
            peso != null && peso <= 30 -> {
                view?.findViewById<TextInputLayout>(R.id.pesoTextInputLayout)?.error = "Ya quisieras"
                esValido = false
            }
            peso != null && peso >= 597 -> {
                view?.findViewById<TextInputLayout>(R.id.pesoTextInputLayout)?.error = "Lo dudamos mucho"
                esValido = false
            }
            else -> view?.findViewById<TextInputLayout>(R.id.pesoTextInputLayout)?.error = null
        }

        // Validación para el campo distancia por paso
        val distanciaPorPaso = view?.findViewById<EditText>(R.id.distanciaPromedioPorPasoEditText)?.text.toString().toIntOrNull()
        when {
            distanciaPorPaso != null && distanciaPorPaso <= 50 -> {
                view?.findViewById<TextInputLayout>(R.id.distanciaPromedioPorPasoTextInputLayout)?.error = "Ingresa un valor más alto"
                esValido = false
            }
            distanciaPorPaso != null && distanciaPorPaso >= 120 -> {
                view?.findViewById<TextInputLayout>(R.id.distanciaPromedioPorPasoTextInputLayout)?.error = "Ingresa un valor más bajo"
                esValido = false
            }
            else -> view?.findViewById<TextInputLayout>(R.id.distanciaPromedioPorPasoTextInputLayout)?.error = null
        }

        // Retorna el resultado de la validación
        return esValido
    }



    private fun GuardarDatosSQLite() {
        val email = DatosUsuario.getEmail(requireActivity())

        // Obtener referencias a los EditTexts (asumiendo que ya están inicializadas en 'initializeViews')
        val edadEditText = editTexts[0]
        val estaturaEditText = editTexts[1]
        val pesoEditText = editTexts[2]
        val codigoEditText = editTexts[3]
        val distanciapasoEditText = editTexts[4]


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
        codigoEditText.text.toString().takeIf { it.isNotBlank() }?.let { codigo ->
            values.put(BDsqlite.COLUMN_CODE, codigo.toInt())
        }
        distanciapasoEditText.text.toString().takeIf { it.isNotBlank() }?.let { distanciapaso ->
            values.put(BDsqlite.COLUMN_DISTANCEPERSTEP, distanciapaso.toInt())
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
        saveButton.isEnabled = SpecialityIDSpinner.selectedItemPosition > 0
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
