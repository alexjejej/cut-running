package com.cut.android.running.usecases.home

import MapsFragment
import RacesManagement
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.cut.android.running.R
import com.cut.android.running.usecases.clasificacion.ClasificacionFragment
import com.cut.android.running.provider.DatosUsuario
import com.cut.android.running.provider.resources.ManejadorAccionesFallidas
import com.cut.android.running.usecases.admin.AdminUsers
import com.cut.android.running.usecases.estadisticas.EstadisticasFragment
import com.cut.android.running.usecases.logros.LogrosFragment
import com.cut.android.running.usecases.logros.admin.AdminLogrosFragment
import com.cut.android.running.usecases.profile.ProfileFragment



class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var viewModel: HomeViewModel
    private lateinit var btnCarrera: Button
    private lateinit var btnLogros: Button
    private lateinit var btnClasificacion: Button
    private lateinit var btnAdminLogros: Button
    private lateinit var btnAdminCarreras: Button
    private lateinit var btnCarreras: Button
    private lateinit var btnReintentarEstatus : Button
    private lateinit var btnAdminUsuarios: Button
    private lateinit var btnPerfil: Button
    private lateinit var btnEstadisticas : Button
    private lateinit var layoutBienvenida: LinearLayout
    private lateinit var layoutCarrera: LinearLayout
    private lateinit var manejadorAcciones: ManejadorAccionesFallidas
    private lateinit var txtAvisoFecha: TextView
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI(view)
        setupViewModel()
    }

    private fun initializeUI(view: View) {
        //Rerefenciar botones
        btnCarrera = view.findViewById(R.id.btnCarrera)
        btnLogros = view.findViewById(R.id.btnLogros)
        btnClasificacion = view.findViewById(R.id.btnClasificacion)
        btnAdminLogros = view.findViewById(R.id.btnAdminLogros)
        btnAdminCarreras = view.findViewById(R.id.btnAdminCarreras)
        btnPerfil = view.findViewById(R.id.btnProfile)
        btnEstadisticas = view.findViewById(R.id.btnEstadisticas)
        btnCarreras = view.findViewById(R.id.btnCarreras)
        btnAdminUsuarios = view.findViewById(R.id.btnAdminUsuarios)
        //Dar clic a los botones
        btnCarrera.setOnClickListener { navigateToFragment(MapsFragment()) }
        btnLogros.setOnClickListener { navigateToFragment(LogrosFragment()) }
        btnClasificacion.setOnClickListener { navigateToFragment(ClasificacionFragment()) }
        btnAdminLogros.setOnClickListener { navigateToFragment(AdminLogrosFragment()) }
        btnAdminCarreras.setOnClickListener{ navigateToFragment(RacesManagement()) }
        btnPerfil.setOnClickListener{ navigateToFragment(ProfileFragment()) }
        btnEstadisticas.setOnClickListener{ navigateToFragment(EstadisticasFragment())}
        btnCarreras.setOnClickListener{ navigateToFragment(RacesManagement())}
        btnAdminUsuarios.setOnClickListener{ navigateToFragment(AdminUsers())}
        // Instanciar ManejadorAccionesFallidas
        manejadorAcciones = ManejadorAccionesFallidas(requireContext())

        btnReintentarEstatus = view.findViewById(R.id.btnReintentarEstatus)
        btnReintentarEstatus.setOnClickListener {
            Log.d("HOMEFRAGMENT", "btnReintentarEstatus presionado")
            viewModel.checkApiConnection(DatosUsuario.getEmail(requireActivity()))
        }
        //Rerefenciar txt
        txtAvisoFecha = view.findViewById(R.id.txtAvisoFecha)
        //Rerefenciar Layouts
        layoutBienvenida = view.findViewById(R.id.LayoutBienvenida)
        layoutCarrera = view.findViewById(R.id.LayoutCarrera)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setupViewModel() {
        val nombreuser = DatosUsuario.getUserName(requireContext())

        updateWelcomeMessage(nombreuser!!)

        viewModel = ViewModelProvider(this).get(HomeViewModel::class.java)

        viewModel.roleId.observe(viewLifecycleOwner) { roleId ->
            updateAdminOptionsVisibility(roleId)
        }
        // Nuevo observador para el estado de la conexión
        viewModel.apiConnection.observe(viewLifecycleOwner) { isConnected ->
            updateApiConnectionStatus(isConnected)
        }
        viewModel.checkUserRole(DatosUsuario.getEmail(requireActivity()))

        // Llama a checkNextRace() para verificar las carreras próximas
        viewModel.checkNextRace()

        viewModel.nextRace.observe(viewLifecycleOwner) { (hasNextRace, daysUntilNextRace, eventTime) ->
            if (hasNextRace) {
                if (daysUntilNextRace in 0..7) {
                    // Evento próximo dentro de 7 días o es hoy
                    layoutBienvenida.visibility = View.GONE
                    layoutCarrera.visibility = View.VISIBLE

                    txtAvisoFecha.text = when {
                        daysUntilNextRace == 0 && eventTime != null -> {
                            // El evento es hoy y no ha pasado la hora del evento
                            "La carrera es hoy a las $eventTime. Para registrarte, da clic en el siguiente botón:"
                        }
                        daysUntilNextRace > 0 -> {
                            // Hay un evento próximo en 1 a 7 días
                            "Hay una carrera que empieza en $daysUntilNextRace días. Para registrarte, da clic en el siguiente botón:"
                        }
                        else -> {
                            // Para manejar cualquier otro caso inesperado, puedes decidir qué mostrar,
                            "La carrerá ya comenzó"
                        }
                    }
                } else {
                    // Evento es en 8 días o más, mostrar solo layout de bienvenida
                    layoutBienvenida.visibility = View.VISIBLE
                    layoutCarrera.visibility = View.GONE
                }
            } else {
                // No hay eventos próximos, mostrar solo layout de bienvenida
                layoutBienvenida.visibility = View.VISIBLE
                layoutCarrera.visibility = View.GONE
            }
        }


    }
    private fun updateApiConnectionStatus(isConnected: Boolean) {
        val txtApiConnectionStatus = view?.findViewById<TextView>(R.id.txtApiConnectionStatus)
        val (hayPendiente, tipoAccion) = manejadorAcciones.accionPendiente()

        if (isConnected) {
            txtApiConnectionStatus?.text = "Conectado"
            txtApiConnectionStatus?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_connected, 0, 0, 0)
            btnReintentarEstatus.visibility = View.GONE
            /*
               if (hayPendiente) {
                    Hay tareas pendientes, mostrar botón y mensaje adecuado
                   btnReintentarEstatus.visibility = View.VISIBLE
                   btnReintentarEstatus.text = "Tarea pendiente de enviar $tipoAccion"
                   // Considera cambiar el color del botón a uno que indique acción requerida
                   btnReintentarEstatus.setBackgroundColor(Color.parseColor("#FFA500"))  Color naranja, por ejemplo

                Log.d("HomeFragment","Tarea pendiente $hayPendiente")
            } else {
                // No hay tareas pendientes, esconder botón de reintentar
                btnReintentarEstatus.visibility = View.GONE
            } */
        } else {
            txtApiConnectionStatus?.text = "Desconectado"
            txtApiConnectionStatus?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_desconnected, 0, 0, 0)
            // Mantén el botón de reintentar visible si hay desconexion, independientemente de si hay tareas pendientes
            btnReintentarEstatus.visibility = View.VISIBLE
            btnReintentarEstatus.setBackgroundColor(Color.parseColor("#FF8D8D")) // Color rojo, por ejemplo
        }
    }


    private fun EstadoDesconectado() {
        val txtApiConnectionStatus = view?.findViewById<TextView>(R.id.txtApiConnectionStatus)
        txtApiConnectionStatus?.text = "Desconectado"
        txtApiConnectionStatus?.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_desconnected, 0, 0, 0)
        // Mantén el botón de reintentar visible si hay desconexion, independientemente de si hay tareas pendientes
        btnReintentarEstatus.visibility = View.VISIBLE
        btnReintentarEstatus.setBackgroundColor(Color.parseColor("#FF8D8D")) // Color rojo, por ejemplo
    }


    private fun updateWelcomeMessage(userName: String) {
        //var userName2 = DatosUsuario.getUserName(requireContext())
        Log.d("username","$userName")
        view?.findViewById<TextView>(R.id.bienvenidaTextView)?.text = "Bienvenido, $userName"
    }

    private fun updateAdminOptionsVisibility(roleId: Int?) {
        Log.d("HF","ROLEID: $roleId")
        val isAdmin = roleId == 1
        btnAdminCarreras.visibility = if (isAdmin) View.VISIBLE else View.GONE
        btnAdminLogros.visibility = if (isAdmin) View.VISIBLE else View.GONE
        btnAdminUsuarios.visibility = if (isAdmin) View.VISIBLE else View.GONE
    }

    private fun navigateToFragment(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .replace(R.id.home_container_fragment, fragment)
            .addToBackStack(null)
            .commit()
    }


}
