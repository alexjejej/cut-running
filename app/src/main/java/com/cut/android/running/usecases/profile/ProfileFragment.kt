package com.cut.android.running.usecases.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cut.android.running.R
import com.cut.android.running.models.Achievement
import com.cut.android.running.models.Session
import com.cut.android.running.provider.BDsqlite
import com.cut.android.running.provider.DatosUsuario
import com.cut.android.running.provider.RetrofitInstance
import com.cut.android.running.provider.services.AchievementService
import kotlinx.coroutines.launch

/**
 * A simple [Fragment] subclass.
 * Use the [ProfileFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ProfileFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        // Llama aquí a setupProfile y pasa la vista inflada
        setupProfile(view)

        view.findViewById<Button>(R.id.ButtonProfile).setOnClickListener {
            navigateToFragment(UserInfoUpdateFragment())
        }

        return view
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction = parentFragmentManager.beginTransaction()
        transaction.replace(R.id.home_container_fragment, fragment)
        transaction.addToBackStack(null)  // Permite volver al fragmento anterior con el botón de retroceso
        transaction.commit()
    }

    private fun setupProfile(view: View) {
        // Obtener datos de las preferencias compartidas
        val activity = requireActivity()
        Session.readPrefs(activity)

        // Crear una instancia de BDsqlite
        val bd = BDsqlite(context)

        // Obtener el email y nombre
        val nombreUsuario = DatosUsuario.getUserName(requireContext()) ?: "Error"
        val email = DatosUsuario.getEmail(requireActivity())

        val txtNombre = view.findViewById<TextView>(R.id.txtNombreUsuarioProfile)
        txtNombre.text = "Nombre: $nombreUsuario"

        //configurar logros obtenidos
        loadAchievements(email)

        // Configurar la foto del perfil
        val imgPerfilProfile = view.findViewById<ImageView>(R.id.imgPerfilProfile)
        Glide.with(this)
            .load(Session.userPhoto)
            .circleCrop() // Esto hace que la imagen sea redonda
            .into(imgPerfilProfile)

        // Pasos obtenidos
        val pasosTotales = bd.getIntData(email,BDsqlite.COLUMN_PASOS_TOTALES)
        val txtpasosTotales = view.findViewById<TextView>(R.id.txtPasosProfile)
        txtpasosTotales.text = pasosTotales.toString()

        //email
        val txtemail = view.findViewById<TextView>(R.id.txtCorreoUsuarioProfile)
        txtemail.text = "Email: $email"

        // Codigo
        val txtcodigo = view.findViewById<TextView>(R.id.txtCodigoProfile)
        val codigo = bd.getIntData(email, BDsqlite.COLUMN_CODE)
        txtcodigo.text = if (codigo == 0) "Código: Sin datos" else "Código: $codigo"

        // Edad
        val txtedad = view.findViewById<TextView>(R.id.txtEdadUsuarioProfile)
        val edad = bd.getIntData(email, BDsqlite.COLUMN_EDAD)
        txtedad.text = if (edad == 0) "Edad: Sin datos" else "Edad: $edad años"

        // Estatura
        val txtestatura = view.findViewById<TextView>(R.id.txtEstaturaUsuarioProfile)
        val estatura = bd.getIntData(email, BDsqlite.COLUMN_ESTATURA)
        txtestatura.text = if (estatura == 0) "Estatura: Sin datos" else "Estatura: $estatura cm"

        //peso
        val txtpeso = view.findViewById<TextView>(R.id.txtPesoUsuarioProfile)
        val peso = bd.getIntData(email, BDsqlite.COLUMN_PESO)
        txtpeso.text = if (peso == 0) "Peso: Sin datos" else "Peso: $peso kg"

        //CU
        val txtcarrera = view.findViewById<TextView>(R.id.txtCarreraUsuarioProfile)
        val specialityID = bd.getIntData(email, BDsqlite.COLUMN_SPECIALITYID)
        txtcarrera.text = when (specialityID) {
            1 -> "Carrera: Ingeniería en Ciencias Computacionales"
            2 -> "Carrera: Ingenieria en Nanotecnogogía"
            else -> "Carrera: Sin datos"
        }

        //DistanciaPorPaso
        val txtdistancia = view.findViewById<TextView>(R.id.txtDistanciaxPasoUsuarioProfile)
        val distancia = bd.getIntData(email, BDsqlite.COLUMN_DISTANCEPERSTEP)
        txtdistancia.text = if (distancia == 0) "Distancia por paso: Predeterminado" else "Distancia por paso: $distancia cm"

    }


    private fun loadAchievements(email: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.getRetrofit().create(AchievementService::class.java).getAchievementsByUser(email)
                if (response.isSuccessful && response.body() != null) {
                    val achievements = response.body()?.data ?: emptyList()
                    updateAchievementsView(achievements)
                } else {
                    // Manejar respuesta fallida
                    Log.e("ProfileFragment", "Error cargando logros: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                // Manejar excepción
                Log.e("ProfileFragment", "Excepción al cargar logros", e)
            }
        }
    }

    private fun updateAchievementsView(achievements: List<Achievement>) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerViewLogros)
        // Configura el LayoutManager para horizontal
        recyclerView?.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerView?.adapter = AdaptadorLogros(achievements)
    }



    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ProfileFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) = ProfileFragment()
    }

    private fun updateProfile() {
        // Crear una nueva instancia de UserInfoUpdateFragment
        val newFragment = UserInfoUpdateFragment()

        // Obtener el FragmentManager
        val fragmentManager = parentFragmentManager

        // Iniciar la transacción
        val transaction = fragmentManager.beginTransaction()

        // Reemplazar el fragmento actual con el nuevo (asumiendo que tienes un contenedor de fragmentos en tu layout)
        transaction.replace(R.id.home_container_fragment, newFragment)

        // Agregar la transacción a la pila de retroceso (opcional)
        transaction.addToBackStack(null)

        // Confirmar la transacción
        transaction.commit()
    }

}