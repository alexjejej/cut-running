package com.cut.android.running.Carreras

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cut.android.running.Carreras.Adapter.RankingAdapter
import com.cut.android.running.R
import com.cut.android.running.common.response.IResponse
import com.cut.android.running.models.RaceResult
import com.cut.android.running.models.RankingItem
import com.cut.android.running.provider.DatosUsuario
import com.cut.android.running.provider.RetrofitInstance
import com.cut.android.running.provider.resources.Presets
import com.cut.android.running.provider.services.RaceResultService
import com.cut.android.running.usecases.home.HomeActivity
import retrofit2.Response
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch

class FinEvento : AppCompatActivity() {
    private lateinit var txtFinEventoNombre: TextView
    private lateinit var txtFinEventoFelicidades: TextView
    private lateinit var txtEstatusEvento: TextView
    private lateinit var btnReintentar: Button
    private lateinit var btnFinEventoCerrar: Button
    private lateinit var btnFinEventoHome: Button
    private lateinit var LayoutError: LinearLayout
    private lateinit var LayoutSuccess: LinearLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var konfettiView: nl.dionsegijn.konfetti.xml.KonfettiView
    private var NombreUsuario: String? = null
    private var CorreoUsuario: String? = null
    private var idCarrera: Int? = null
    private var tiempo: String? = null
    private var lugar: Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fin_evento)
        initializeUI()
    }

    private fun initializeUI() {
        //konfeti
        konfettiView = findViewById(R.id.konfettiView)
        //Cargar txts
        txtFinEventoNombre = findViewById(R.id.txtFinEventoNombre)
        txtFinEventoFelicidades = findViewById(R.id.txtFinEventoFelicidades)
        txtEstatusEvento = findViewById(R.id.txtEstatusEvento)
        //Cargar buttons
        btnReintentar = findViewById(R.id.btnReintentar)
        btnReintentar.setOnClickListener{
            txtEstatusEvento.text = "Cargando datos..."
            txtEstatusEvento.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_update, 0, 0, 0)
            cargarDatos()
            btnReintentar.visibility = View.GONE
        }
        btnFinEventoCerrar = findViewById(R.id.btnFinEventoCerrar)
        btnFinEventoCerrar.setOnClickListener{
            finishAffinity()
        }
        btnFinEventoHome = findViewById(R.id.btnFinEventoHome)
        btnFinEventoHome.setOnClickListener{
            ReturnHome()
        }
        //Cargar layouts
        LayoutError = findViewById(R.id.LayoutError)
        LayoutSuccess = findViewById(R.id.LayoutSuccess)
        recyclerView = findViewById(R.id.rvRanking)
        recyclerView.layoutManager = LinearLayoutManager(this)

        //Funcion principal que carga los datos y llama a la API
        cargarDatos()
    }
    private fun cargarDatos() {
        val sharedPref = this.getSharedPreferences("MyTrackingPref", Context.MODE_PRIVATE)
        idCarrera = sharedPref.getInt("idCarrera", 0)
        tiempo = sharedPref.getString("timeElapsed", "00:00")
        val nameuser = DatosUsuario.getUserName(this)
        val emailuser = DatosUsuario.getEmail(this)
        NombreUsuario = nameuser
        CorreoUsuario = emailuser
        registrarPosicion(idCarrera!!, tiempo, emailuser, nameuser)
    }

    private fun registrarPosicion(
        idCarrera: Int,
        tiempo: String?,
        emailuser: String,
        nameuser: String?
    ) {
        val raceResult = RaceResult(
            id = null,
            raceId = idCarrera,
            time = tiempo!!,
            userEmail = emailuser,
            userName =  nameuser!!,
            position = null
        )

        CoroutineScope(Dispatchers.IO).launch {
            val response = addRaceResult(raceResult)
            withContext(Dispatchers.Main) {
                if (response.isSuccessful && response.body()?.data != null) {
                    handleSuccess(response.body()!!.data!!)
                } else {
                    handleError(response)
                }
            }
        }
    }
    private suspend fun addRaceResult(raceResult: RaceResult): Response<IResponse<RaceResult>> {
        val raceResultService = RetrofitInstance.getRetrofit().create(RaceResultService::class.java)
        return raceResultService.addRaceResult(raceResult)
    }

    private fun handleSuccess(raceResult: RaceResult) {
        val newPosition = raceResult.position ?: return // Si es null, retorna y no ejecuta el resto
        mostrarDatos(newPosition)
        setupRecyclerView(raceResult.raceId)
        Log.d("RegistrarPosicion", "Posición registrada exitosamente: $newPosition")
        LayoutSuccess.visibility = View.VISIBLE
        LayoutError.visibility = View.GONE
        // Inicia la animación de konfeti
        konfettiView.start(Presets.rain())
    }

    private fun handleError(response: Response<IResponse<RaceResult>>) {
        btnReintentar.visibility = View.VISIBLE
        txtEstatusEvento.text = "Error con la conexión, intente de nuevo"
        txtEstatusEvento.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_desconnected, 0, 0, 0)
        Log.e("RegistrarPosicion", "Error al registrar la posición: ${response.errorBody()?.string()}")
    }

    private fun handleException(e: Exception) {
        btnReintentar.visibility = View.VISIBLE
        txtEstatusEvento.text = "Error con la conexión, intente de nuevo"
        txtEstatusEvento.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_desconnected, 0, 0, 0)
        Log.e("RegistrarPosicion", "Excepción al registrar la posición: ${e.message}")
    }

    private fun mostrarDatos(Position: Int) {
        txtFinEventoNombre.text = "Felicidades $NombreUsuario"
        val textoBase = "Obtuviste el $Position lugar en la carrera con un tiempo de $tiempo minutos"
        val spannableString = SpannableString(textoBase)

        // Estilo para 'Position'
        val positionStart = textoBase.indexOf("$Position")
        val positionEnd = positionStart + "$Position".length
        spannableString.setSpan(StyleSpan(Typeface.BOLD), positionStart, positionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.BLUE), positionStart, positionEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Estilo para 'tiempo'
        val tiempoStart = textoBase.indexOf("$tiempo")
        val tiempoEnd = tiempoStart + "$tiempo".length
        spannableString.setSpan(StyleSpan(Typeface.ITALIC), tiempoStart, tiempoEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(ForegroundColorSpan(Color.RED), tiempoStart, tiempoEnd, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)

        txtFinEventoFelicidades.text = spannableString
    }

    private fun setupRecyclerView(idCarrera: Int) {
        obtenerDatosDeAPI(idCarrera) { datos ->
            recyclerView.adapter = RankingAdapter(datos)
        }
    }

    private fun obtenerDatosDeAPI(idCarrera: Int, callback: (List<RankingItem>) -> Unit) {
        val retrofit = RetrofitInstance.getRetrofit()
        val raceResultService = retrofit.create(RaceResultService::class.java)

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = raceResultService.getResultsByRaceId(idCarrera)
                if (response.isSuccessful && response.body()?.data != null) {
                    val raceResults = response.body()!!.data!!.map { raceResult ->
                        RankingItem(raceResult.position ?: 0, raceResult.userName, raceResult.time)
                    }
                    CoroutineScope(Dispatchers.Main).launch {
                        callback(raceResults)
                    }
                } else {
                    // Manejar el caso de respuesta no exitosa o cuerpo nulo
                    println("Error: ${response.errorBody()?.string()}")
                }
            } catch (e: Exception) {
                // Manejar el caso de excepción
                e.printStackTrace()
            }
        }
    }

    private fun registrarPosicionAgain(raceResult: RaceResult) {
        txtEstatusEvento.text = "Cargando datos..."
        txtEstatusEvento.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icon_update, 0, 0, 0)
        registrarPosicion(
            raceResult.raceId,
            raceResult.time,
            raceResult.userEmail,
            raceResult.userName
        )
    }

    fun ReturnHome() {

        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
    }

}