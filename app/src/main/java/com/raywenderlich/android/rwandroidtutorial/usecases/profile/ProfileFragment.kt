package com.raywenderlich.android.rwandroidtutorial.usecases.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.models.Session
import com.raywenderlich.android.rwandroidtutorial.provider.BDsqlite
import com.raywenderlich.android.rwandroidtutorial.provider.DatosUsuario
import com.raywenderlich.android.rwandroidtutorial.usecases.logros.LogrosFragment
import com.squareup.picasso.Picasso

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

        view.findViewById<ImageButton>(R.id.imageButtonProfile).setOnClickListener {
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
        // Obtener el nombre de usuario de las preferencias compartidas
        val activity = requireActivity() // Obtener la Activity
        Session.readPrefs(activity)


        // Obtener el nombre del usuario
        val nombreUsuario = DatosUsuario.getUserName(requireContext()) ?: "Error"

        val txtNombre = view.findViewById<TextView>(R.id.txtNombreUsuarioProfile)
        txtNombre.text = "Nombre: $nombreUsuario"

        // Configurar la foto del perfil
        val imgPerfilProfile = view.findViewById<ImageView>(R.id.imgPerfilProfile)
        Glide.with(this)
            .load(Session.userPhoto)
            .circleCrop() // Esto hace que la imagen sea redonda
            .into(imgPerfilProfile)

        // Pasos obtenidos
        val pasosTotales = DatosUsuario.getPasosTotales(requireContext())
        val txtpasosTotales = view.findViewById<TextView>(R.id.txtPasosProfile)
        txtpasosTotales.text = pasosTotales.toString()

        //email
        val txtemail = view.findViewById<TextView>(R.id.txtCorreoUsuarioProfile)
        txtemail.text = Session.userEmail

        // Crear una instancia de BDsqlite
        val bd = BDsqlite(context)

        // Edad
        val txtedad = view.findViewById<TextView>(R.id.txtEdadUsuarioProfile)
        val edad = bd.getIntData(nombreUsuario, BDsqlite.COLUMN_EDAD)
        txtedad.text = if (edad == 0) "Edad: Sin datos" else "Edad: $edad"

        // Estatura
        val txtestatura = view.findViewById<TextView>(R.id.txtEstaturaUsuarioProfile)
        val estatura = bd.getIntData(nombreUsuario, BDsqlite.COLUMN_ESTATURA)
        txtestatura.text = if (estatura == 0) "Estatura: Sin datos" else "Estatura: $estatura"

        //peso
        val txtpeso = view.findViewById<TextView>(R.id.txtPesoUsuarioProfile)
        val peso = bd.getIntData(nombreUsuario, BDsqlite.COLUMN_PESO)
        txtpeso.text = if (peso == 0) "Peso: Sin datos" else "Peso: $peso"

        //CU
        val txtCU = view.findViewById<TextView>(R.id.txtCUUsuarioProfile)
        val centroUniversitario =
            bd.getStringData(nombreUsuario, BDsqlite.COLUMN_CENTRO_UNIVERSITARIO)
        txtCU.text =
            if (!centroUniversitario.isNullOrEmpty()) "Centro Universitario: $centroUniversitario" else "Centro Universitario: Sin datos"

        //carrera
        val txtcarrera = view.findViewById<TextView>(R.id.txtCarreraUsuarioProfile)
        val carrera = bd.getStringData(nombreUsuario, BDsqlite.COLUMN_CARRERA)
        txtcarrera.text =
            if (!carrera.isNullOrEmpty()) "Carrera: $carrera" else "Carrera: Sin datos"
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