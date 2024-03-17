package com.cut.android.running.Carreras

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cut.android.running.Carreras.Adapter.RankingAdapter
import com.cut.android.running.R
import com.cut.android.running.models.RankingItem
import com.cut.android.running.provider.DatosUsuario

class FinEvento : AppCompatActivity() {
    private lateinit var txtFinEventoNombre: TextView
    private lateinit var txtFinEventoFelicidades: TextView
    private lateinit var recyclerView: RecyclerView
    private var NombreUsuario: String? = null
    private var CorreoUsuario: String? = null
    private var distancia: String? = null
    private var idCarrera: Int? = null
    private var tiempo: String? = null
    private var lugar: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fin_evento)
        initializeUI()
    }

    private fun initializeUI() {
        txtFinEventoNombre = findViewById(R.id.txtFinEventoNombre)
        txtFinEventoFelicidades = findViewById(R.id.txtFinEventoFelicidades)
        recyclerView = findViewById(R.id.rvRanking)
        recyclerView.layoutManager = LinearLayoutManager(this)

        cargarDatos()
        mostrarDatos()
        setupRecyclerView()
    }
    private fun cargarDatos() {
        val sharedPref = this.getSharedPreferences("MyTrackingPref", Context.MODE_PRIVATE)
        idCarrera = sharedPref.getInt("idCarrera", 0)
        tiempo = sharedPref.getString("timeElapsed", "00:00")
        NombreUsuario = DatosUsuario.getUserName(this)
        CorreoUsuario = DatosUsuario.getEmail(this)
        registrarPosicion(idCarrera!!)
    }

    private fun registrarPosicion(idCarrera: Int) {

    }

    private fun mostrarDatos() {
        txtFinEventoNombre.text = "Felicidades $NombreUsuario"
        txtFinEventoFelicidades.text = "Obtuviste el $lugar lugar en la carrera de $idCarrera con un tiempo de $tiempo"
    }
    private fun setupRecyclerView() {
        obtenerDatosDeAPI { datos ->
            recyclerView.adapter = RankingAdapter(datos)
        }
    }

    private fun obtenerDatosDeAPI(callback: (List<RankingItem>) -> Unit) {
        // Simulando la obtención de datos de una API
        // Aquí deberías hacer la llamada a tu API y obtener los datos reales
        val datosSimulados = listOf(
            RankingItem(1, "Usuario 1", "00:30"),
            RankingItem(2, "Usuario 2", "00:45"),
            RankingItem(3, "Usuario 3", "01:00")
        )
        callback(datosSimulados)
    }

}