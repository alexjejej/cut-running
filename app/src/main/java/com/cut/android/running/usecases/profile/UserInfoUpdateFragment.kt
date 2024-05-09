package com.cut.android.running.usecases.profile

import android.app.AlertDialog
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
import com.cut.android.running.models.User
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
    private lateinit var txtEstatusUpdate: TextView

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

        txtEstatusUpdate = view.findViewById(R.id.txtEstatusUpdate)

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


    }


    private fun setupCentroUniversitarioSpinner() {
        val centros = resources.getStringArray(R.array.centros_universitarios_array)
        val adapter = MyCustomAdapter(requireContext(), R.layout.spinner_item, centros.toList())

        // Aplica el adaptador al spinner
        SpecialityIDSpinner.adapter = adapter

        // Añadir el listener
        SpecialityIDSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                // Aquí, 'view' puede ser null, pero no lo usamos directamente, así que está bien.
                checkFieldsForEmptyValues()
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // Manejo opcional del caso de "nada seleccionado"
            }
        }
    }




    private fun GuardarDatosAPI() {
        val email = DatosUsuario.getEmail(requireActivity()) ?: return

        configurarUIAntesDeGuardar()

        lifecycleScope.launch(Dispatchers.IO) {
            val isConnected = verificarConexion(email)
            if (!isConnected) {
                mostrarErrorYActualizarUI()
                return@launch
            }

            val responseActual = userService.getUserByEmail(email)
            val usuarioActual = responseActual.body()?.data ?: return@launch

            val userActualizado = construirUsuarioActualizado(usuarioActual)
            if (userActualizado != null && usuarioActual != userActualizado) {
                actualizarUsuarioEnAPI(userActualizado)
            } else {
                mostrarMensajeSinCambios()
            }
        }
    }
    private fun configurarUIAntesDeGuardar() {
        saveButton.isEnabled = false
        txtEstatusUpdate.visibility = View.VISIBLE
    }

    private fun mostrarErrorYActualizarUI() {
        lifecycleScope.launch(Dispatchers.Main) {
            mostrarErrorDeConexion()
            txtEstatusUpdate.visibility = View.INVISIBLE
            saveButton.isEnabled = true
        }
    }

    private fun construirUsuarioActualizado(usuarioActual: User): User? {
        // Extracción de los valores actuales de los campos de texto
        val edadActualizada = editTexts[0].text.toString().toIntOrNull()
        val estaturaActualizada = editTexts[1].text.toString().toIntOrNull()
        val pesoActualizado = editTexts[2].text.toString().toIntOrNull()
        val codigoActualizado = editTexts[3].text.toString().toIntOrNull()
        val distanciaPorPasoActualizada = editTexts[4].text.toString().toIntOrNull()

        // Determinar si el SpecialtyID ha cambiado
        val specialtyIdActualizado = when (SpecialityIDSpinner.selectedItem.toString()) {
            "Administración de Negocios" -> 1
            "Arquitectura" -> 2
            "Ciencias Computacionales" -> 3
            "Ciencias Forenses" -> 4
            "Contaduría Pública" -> 5
            "Derecho / Abogado" -> 6
            "Diseño de Artesanía" -> 7
            "Egresado" -> 8
            "Energía" -> 9
            "Enfermería" -> 10
            "Estudios Liberales" -> 11
            "Gerontología" -> 12
            "Historia del Arte" -> 13
            "Médico Cirujano y Partero" -> 14
            "Nanotecnología" -> 15
            "Nutrición" -> 16
            "Salud Pública" -> 17
            "Académico o Administrativo de CUT" -> 18
            "Estudiante de Posgrado" -> 19
            "Otros" -> 20
            else -> usuarioActual.specialtyId
        }



        // Verificar si hay cambios comparando los valores actuales con los nuevos
        val hayCambios = edadActualizada != usuarioActual.age ||
                estaturaActualizada != usuarioActual.height ||
                pesoActualizado != usuarioActual.weight ||
                codigoActualizado != usuarioActual.code ||
                distanciaPorPasoActualizada != usuarioActual.distanceperstep ||
                specialtyIdActualizado != usuarioActual.specialtyId

        // Si hay cambios, construir y retornar un nuevo objeto User con los valores actualizados
        if (hayCambios) {
            return usuarioActual.copy(
                age = edadActualizada ?: usuarioActual.age,
                height = estaturaActualizada ?: usuarioActual.height,
                weight = pesoActualizado ?: usuarioActual.weight,
                code = codigoActualizado ?: usuarioActual.code,
                distanceperstep = distanciaPorPasoActualizada ?: usuarioActual.distanceperstep,
                specialtyId = specialtyIdActualizado
            )
        }

        // Si no hay cambios, podrías optar por retornar null o el mismo objeto User
        return null
    }


    private suspend fun actualizarUsuarioEnAPI(userActualizado: User) {
        val updateResponse = userService.updateUser(userActualizado)
        withContext(Dispatchers.Main) {
            if (updateResponse.isSuccessful && updateResponse.body()?.isSuccess == true) {
                Toast.makeText(requireContext(), "Datos actualizados correctamente", Toast.LENGTH_SHORT).show()
                GuardarDatosSQLite()
            } else {
                Log.e("API Error", "Respuesta fallida: ${updateResponse.errorBody()?.string()}")
                Toast.makeText(requireContext(), "Error al actualizar datos en la API", Toast.LENGTH_SHORT).show()
            }
            // Asegúrate de volver a activar el botón y ocultar el txtEstatusUpdate aquí también
            txtEstatusUpdate.visibility = View.INVISIBLE
            saveButton.isEnabled = true
        }
    }

    private fun mostrarMensajeSinCambios() {
        lifecycleScope.launch(Dispatchers.Main) {
            Toast.makeText(requireContext(), "No hay cambios para actualizar", Toast.LENGTH_SHORT).show()
            // Asegúrate de volver a activar el botón y ocultar el txtEstatusUpdate aquí también
            txtEstatusUpdate.visibility = View.INVISIBLE
            saveButton.isEnabled = true
        }
    }


    private fun mostrarErrorDeConexion() {
        lifecycleScope.launch(Dispatchers.Main) {
            AlertDialog.Builder(requireContext(), R.style.AlertDialogCustom)
                .setTitle("Error de Conexión")
                .setMessage("No se conectó con la base de datos del CUT y no se pudo guardar los cambios.")
                .setPositiveButton("Aceptar", null)
                .show()
            // Reactivar el botón y ocultar el TextView de estado
            saveButton.isEnabled = true
            requireView().findViewById<TextView>(R.id.txtEstatusUpdate).visibility = View.INVISIBLE
        }
    }

    private suspend fun verificarConexion(email: String): Boolean {
        return try {
            val response = userService.getUserByEmail(email)
            response.isSuccessful // Si la petición es exitosa, asumimos que hay conexión
        } catch (e: Exception) {
            false // Si hay una excepción, asumimos que no hay conexión
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
            "Administración de Negocios" -> 1
            "Arquitectura" -> 2
            "Ciencias Computacionales" -> 3
            "Ciencias Forenses" -> 4
            "Contaduría Pública" -> 5
            "Derecho / Abogado" -> 6
            "Diseño de Artesanía" -> 7
            "Egresado" -> 8
            "Energía" -> 9
            "Enfermería" -> 10
            "Estudios Liberales" -> 11
            "Gerontología" -> 12
            "Historia del Arte" -> 13
            "Médico Cirujano y Partero" -> 14
            "Nanotecnología" -> 15
            "Nutrición" -> 16
            "Salud Pública" -> 17
            "Académico o Administrativo de CUT" -> 18
            "Estudiante de Posgrado" -> 19
            "Otros" -> 20
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
        val camposLlenos = editTexts.any { it.text.isNotEmpty() } // Comprueba que todos los campos estén llenos
        val spinnerSeleccionado = SpecialityIDSpinner.selectedItemPosition > 0 // Comprueba que se ha seleccionado un ítem del Spinner

        // El botón se activa si todos los campos están llenos o un ítem del Spinner está seleccionado
        saveButton.isEnabled = camposLlenos || spinnerSeleccionado
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
