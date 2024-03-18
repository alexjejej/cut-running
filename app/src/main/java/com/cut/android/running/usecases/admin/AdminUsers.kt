package com.cut.android.running.usecases.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Switch
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.cut.android.running.R
import com.cut.android.running.models.Role
import com.cut.android.running.models.User
import com.cut.android.running.provider.RetrofitInstance
import com.cut.android.running.provider.services.UserService
import com.cut.android.running.usecases.home.HomeFragment
import kotlinx.coroutines.launch

class AdminUsers : Fragment() {

    private lateinit var editTextCodigo: EditText
    private lateinit var btnBuscar: Button
    private lateinit var layoutInformacion: LinearLayout
    private lateinit var txtNombreUsuarioAdmin: TextView
    private lateinit var txtEdadUsuarioAdmin: TextView
    private lateinit var txtCorreoUsuarioAdmin: TextView
    private lateinit var btnAdminHome: Button
    private lateinit var btnAdminCerrar: Button
    private lateinit var switchAdmin: Switch
    private var currentUser: User? = null
    private var isUpdatingSwitch = false

    private val userService by lazy {
        RetrofitInstance.getRetrofit().create(UserService::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_admin_users, container, false)
        initializeUI(view)
        return view
    }

    private fun initializeUI(view: View) {
        editTextCodigo = view.findViewById(R.id.codigoEditTextUpdate)
        btnBuscar = view.findViewById(R.id.btnBuscar)
        layoutInformacion = view.findViewById(R.id.LinearLayoutInformacion)
        txtNombreUsuarioAdmin = view.findViewById(R.id.txtNombreUsuarioAdmin)
        txtEdadUsuarioAdmin = view.findViewById(R.id.txtEdadUsuarioAdmin)
        txtCorreoUsuarioAdmin = view.findViewById(R.id.txtCorreoUsuarioAdmin)
        btnAdminHome = view.findViewById(R.id.btnAdminHome)
        btnAdminCerrar = view.findViewById(R.id.btnAdminCerrar)
        switchAdmin = view.findViewById(R.id.switchAdmin)

        btnAdminHome.setOnClickListener{
            navigateToFragment(HomeFragment())
        }

        btnAdminCerrar.setOnClickListener{
            activity?.finish()
        }
        btnBuscar.setOnClickListener {
            val email = editTextCodigo.text.toString()
            if (email.isNotEmpty()) {
                getUserByEmail(email)
            } else {
                Toast.makeText(context, "Ingrese un correo electrónico", Toast.LENGTH_SHORT).show()
            }
        }

        switchAdmin.setOnCheckedChangeListener { _, isChecked ->
            if (isUpdatingSwitch) {
                return@setOnCheckedChangeListener
            }
            var user = currentUser
            if (user != null) {
                // Cambiar el rol del usuario según el estado del switch
                user.Role = if (isChecked) Role(1, "Admin",1,null) else Role(2, "User",1,null)
                updateUser(user)
            }
        }


    }

    private fun getUserByEmail(email: String) {
        lifecycleScope.launch {
            try {
                val response = userService.getUserByEmail(email)
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!.data
                    updateUI(user!!)
                } else {
                    Toast.makeText(context, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error al obtener datos", Toast.LENGTH_SHORT).show()
            }
        }
    }


    private fun updateUI(user: User) {
        currentUser = user
        layoutInformacion.visibility = View.VISIBLE
        txtNombreUsuarioAdmin.text = "Nombre: ${user.firstname} ${user.lastname}"
        txtEdadUsuarioAdmin.text = "Edad: ${user.age ?: "Desconocida"}"
        txtCorreoUsuarioAdmin.text = "Correo: ${user.email}"

        isUpdatingSwitch = true // Ignorar eventos programáticos
        switchAdmin.isChecked = user.Role?.id == 1 // 1 para admin
        isUpdatingSwitch = false // Restablecer después de la actualización programática
    }


    private fun updateUser(user: User) {
        lifecycleScope.launch {
            try {
                val response = userService.updateUser(user)
                if (response.isSuccessful && response.body() != null) {
                    val isSuccess = response.body()!!.data
                    if (isSuccess == true) {
                        Toast.makeText(context, "Rol actualizado correctamente", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Error al actualizar el rol", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(context, "Error en la respuesta de la API", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error al actualizar el usuario", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.home_container_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }



}
