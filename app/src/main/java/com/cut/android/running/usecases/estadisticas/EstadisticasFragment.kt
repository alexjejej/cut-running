package com.cut.android.running.usecases.estadisticas

import android.animation.ValueAnimator
import android.app.AlertDialog
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.cut.android.running.R
import com.cut.android.running.provider.BDsqlite
import com.cut.android.running.provider.DatosUsuario
import com.cut.android.running.usecases.home.HomeFragment

class EstadisticasFragment : Fragment() {

    private lateinit var txtNombreUsuarioEstadisticas: TextView
    private lateinit var txtPasosTotalesEstadisticas: TextView
    private lateinit var txtDistanciaEstadisticas: TextView
    private lateinit var txtCaloriasEstadisticas: TextView
    private lateinit var txtCaloriasCalculo: TextView
    private lateinit var btnVolverAlInicio: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Infla el layout para este fragmento
        return inflater.inflate(R.layout.fragment_estadisticas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initializeUI(view)
    }

    private fun initializeUI(view: View) {
        txtNombreUsuarioEstadisticas = view.findViewById(R.id.txtNombreUsuarioEstadisticas)
        txtPasosTotalesEstadisticas = view.findViewById(R.id.txtPasosTotalesEstadisticas)
        txtDistanciaEstadisticas = view.findViewById(R.id.txtDistanciaEstadisticas)
        txtCaloriasEstadisticas = view.findViewById(R.id.txtCaloriasEstadisticas)
        txtCaloriasCalculo = view.findViewById(R.id.txtInformacionCalorias)
        btnVolverAlInicio = view.findViewById(R.id.ButtonHomeEstadisticas)

        txtCaloriasCalculo.text = Html.fromHtml(getString(R.string.info_calorias_html), Html.FROM_HTML_MODE_LEGACY)
        val nombreUsuario = DatosUsuario.getUserName(requireContext())
        val emailUsuario = DatosUsuario.getEmail(requireActivity())
        val bdSqlite = BDsqlite(requireContext())

        txtCaloriasCalculo.setOnClickListener {
            abrirDialogoExplicativo()
        }
        // Configurando el texto del nombre de usuario
        txtNombreUsuarioEstadisticas.text = "Estadísticas de $nombreUsuario"

        // Configurando los pasos totales
        val pasosTotales = bdSqlite.getIntData(emailUsuario, BDsqlite.COLUMN_PASOS_TOTALES)
        animarValorTextView(txtPasosTotalesEstadisticas, pasosTotales)

        mostrarDistancia(pasosTotales) // Actualiza la vista con la distancia
        calcularYMostrarCalorias() // Calcula y muestra las calorías quemadas

        // Configurando el botón para volver al inicio
        btnVolverAlInicio.setOnClickListener {
            navigateToFragment(HomeFragment())
        }
    }

    private fun navigateToFragment(fragment: Fragment) {
        val transaction = requireActivity().supportFragmentManager.beginTransaction()
        transaction.replace(R.id.home_container_fragment, fragment)
        transaction.addToBackStack(null) // Optional
        transaction.commit()
    }

    private fun calcularYMostrarCalorias() {
        val emailUsuario = DatosUsuario.getEmail(requireActivity())
        val bdSqlite = BDsqlite(requireContext())
        val peso = bdSqlite.getIntData(emailUsuario, BDsqlite.COLUMN_PESO)

        // Verificar si el peso está disponible
        if (peso <= 0) {
            // Mostrar mensaje si el peso no está disponible
            txtCaloriasEstadisticas.apply {
                text = "Debes agregar tu peso al perfil para ver este dato"
                textSize = 12f // Cambiar el tamaño del texto a 12sp
            }
        } else {
            val totalPasos = bdSqlite.getIntData(emailUsuario, BDsqlite.COLUMN_PASOS_TOTALES)
            val caloriasQuemadas = calcularCaloriasQuemadas(peso, totalPasos)
            animarValorTextView(txtCaloriasEstadisticas,caloriasQuemadas.toInt())
        }
    }


    fun calcularCaloriasQuemadas(peso: Int, totalPasos: Int): Double {
        Log.d("EstadisticasFragment","$peso  $totalPasos")
        val pasosPorMinuto = 90.0
        val tiempoEnHoras = (totalPasos / pasosPorMinuto) / 60.0
        val MET = 3.5 // Valor promedio para caminata moderada
        val calorias = MET * peso * tiempoEnHoras
        Log.d("EstadisticasFragment","$tiempoEnHoras  $calorias")

        return calorias
    }

    private fun mostrarDistancia(pasosTotales: Int) {
        val emailUsuario = DatosUsuario.getEmail(requireActivity())
        val db = BDsqlite(requireContext())

        // Intentar obtener la distancia por paso de la base de datos
        val distancePerStep = db.getFloatData(emailUsuario, BDsqlite.COLUMN_DISTANCEPERSTEP)?.let { it / 100 }
        var distanciaUsada = 0.0

        // Si distancePerStep es 0, calcular la distancia promedio por paso
        if (distancePerStep?.toDouble() == 0.0){
            // Obtener la estatura del usuario de la base de datos y convertirla a metros
            val userEstatura = db.getFloatData(emailUsuario, BDsqlite.COLUMN_ESTATURA)?.let { it / 100 } ?: 0.0f // Asegúrate de manejar el caso de null

            // Calcular la distancia promedio por paso
            val distanciaPromedioPorPaso = if (userEstatura > 0) {
                distanciaUsada = userEstatura.toDouble() * .415
            } else {
                distanciaUsada = .75 // Valor predeterminado si no se conoce la estatura
            }

        }else{
            distanciaUsada = distancePerStep!!.toDouble()
        }

        // Calcula la distancia total basada en pasos totales y la distancia usada
        val distanciaTotal = pasosTotales * distanciaUsada

        // Anima el valor de la distancia total en el TextView
        animarValorTextView(txtDistanciaEstadisticas, distanciaTotal.toInt())
    }



    private fun animarValorTextView(textView: TextView, valorFinal: Int) {
        val animator = ValueAnimator.ofInt(0, valorFinal)
        animator.duration = 1000 // Duración en milisegundos
        animator.interpolator = AccelerateDecelerateInterpolator() // Interpolador para suavizar la animación
        animator.addUpdateListener { animation ->
            textView.text = animation.animatedValue.toString()
        }
        animator.start()
    }

    private fun abrirDialogoExplicativo() {
        val mensajeExplicativo = Html.fromHtml(getString(R.string.explicacion_calorias), Html.FROM_HTML_MODE_LEGACY)

        val dialogBuilder = AlertDialog.Builder(context)
        dialogBuilder.setMessage(mensajeExplicativo)
            .setCancelable(true)
            .setPositiveButton("Entendido") { dialog, _ ->
                dialog.dismiss()
            }

        val alert = dialogBuilder.create()
        alert.setTitle("Cómo Calculamos las Calorías")
        alert.show()
    }



}
