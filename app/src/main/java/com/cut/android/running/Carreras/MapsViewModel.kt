import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cut.android.running.provider.RetrofitInstance
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class MapsViewModel : ViewModel() {
    //proxima carrera
    val nextRaceDateTime = MutableLiveData<ZonedDateTime?>()
    private val raceService = RetrofitInstance.getRetrofit().create(RaceService::class.java)
    val isButtonEnabled = MutableLiveData<Boolean>(true)

    val pathPoints = MutableLiveData<MutableList<LatLng>>(mutableListOf())
    val totalDistance = MutableLiveData<Float>(0f)
    val totalSteps = MutableLiveData<Int>(0)
    var raceId = 0
    var distancie = 0.0
    var EstaturaUser = 0.0
    var distancePerStep = 0.0
    //mostrar toast en viewmodel
    val showToastEvent = MutableLiveData<List<String>>()
    private val messageQueue: MutableList<String> = mutableListOf()
    // Lista para guardar los timestamps de cada paso dado
    private val pasosTimestamps = mutableListOf<Long>()
    private val maxPasosPorIntervalo = 10 // Máximo de pasos permitidos en el intervalo de 3s ¿Dudas? Revisar al fondo
    private var intervaloTiempo = 4000L // Intervalo de tiempo en milisegundos
    private var ultimaVezToastMostrado = 0L
    private val tiempoEsperaToast = 4000L // 4 segundos entre cada Toast
    private val intervaloTiempoOriginal = 4000L
    private var tiempoPenalizacion = 0L // Tiempo hasta que la penalización se restablece

    //Contador de tiempo
    private var isTimerRunning = false
    private var timerHandler = Handler(Looper.getMainLooper())
    private var startTime = 0L

    private val _timeElapsed = MutableLiveData<String>()
    val timeElapsed: LiveData<String> = _timeElapsed


    private var timeUpdateTask = object : Runnable {
        override fun run() {
            val totalSeconds = (SystemClock.uptimeMillis() - startTime) / 1000
            val hours = totalSeconds / 3600
            val minutes = (totalSeconds % 3600) / 60
            val seconds = totalSeconds % 60

            // Decide el formato basado en el tiempo transcurrido
            val timeString = when {
                hours > 0 -> String.format("%02d:%02d:%02d", hours, minutes, seconds) // Horas:Minutos:Segundos
                else -> String.format("%02d:%02d", minutes, seconds) // Minutos:Segundos
            }

            _timeElapsed.postValue(timeString)

            timerHandler.postDelayed(this, 1000)
        }
    }
    fun startTimer() {
        Log.d("MapsViewModel", "Cargando tiempo transcurrido: ${_timeElapsed.value}")
        if (!isTimerRunning) {

            val elapsedTimeMillis = parseElapsedTime(_timeElapsed.value ?: "00:00")
            startTime = SystemClock.uptimeMillis() - elapsedTimeMillis

            timerHandler.postDelayed(timeUpdateTask, 0)
            isTimerRunning = true
        }
    }

    /**
     * Convierte el tiempo transcurrido en formato String (HH:mm:ss o mm:ss) a milisegundos.
     */
    private fun parseElapsedTime(timeString: String): Long {
        val parts = timeString.split(":").map { it.toIntOrNull() ?: 0 }
        val seconds = when (parts.size) {
            3 -> parts[0] * 3600 + parts[1] * 60 + parts[2] // HH:mm:ss
            2 -> parts[0] * 60 + parts[1] // mm:ss
            else -> 0
        }
        return seconds * 1000L
    }



    fun stopTimer() {
        timerHandler.removeCallbacks(timeUpdateTask)
        isTimerRunning = false
    }

    fun addPathPoint(newPoint: LatLng) {
        val lastPoint = pathPoints.value?.lastOrNull()

        // Calcula la distancia solo si hay un último punto
        if (lastPoint != null) {
            val distance = SphericalUtil.computeDistanceBetween(lastPoint, newPoint)
            Log.d("MapsViewModel","Distancia registrada: $distance, lastpoint $lastPoint, newPoint: $newPoint")
            // Verifica si la nueva ubicación está a 2 metros o menos de la última ubicación registrada
            if (distance <= 2) {
                distancie = distance
                // Agrega la nueva ubicación y actualiza la distancia total
                pathPoints.value?.apply {
                    add(newPoint)
                    pathPoints.postValue(this)
                }
            }
        } else {

            Log.d("MapsViewModel","Distancia muy lejana: $distancie")
            // Si no hay último punto, simplemente agrega el nuevo punto
            pathPoints.value?.apply {
                add(newPoint)
                pathPoints.postValue(this)
            }
        }
    }

    fun clearPathPoints() {
        pathPoints.postValue(mutableListOf())
        totalDistance.postValue(0f)
    }

    fun addStep(context: Context) {
        val currentTime = System.currentTimeMillis()

        // Restablecer al intervalo original si la penalización ha expirado
        if (currentTime > tiempoPenalizacion) {
            intervaloTiempo = intervaloTiempoOriginal
        }

        // Elimina los pasos fuera del intervalo de tiempo actual
        pasosTimestamps.removeAll { it < currentTime - intervaloTiempo }

        if (pasosTimestamps.size < maxPasosPorIntervalo) {
            pasosTimestamps.add(currentTime)

            val currentSteps = totalSteps.value ?: 0
            totalSteps.postValue(currentSteps + 1)

            // Actualiza la distancia total
            val pasoDistancia = verificarPromedioDistanciaPorPaso()
            totalDistance.postValue(((currentSteps + 1) * pasoDistancia).toFloat())

            saveTrackingData(context)
        } else {
            if (currentTime - ultimaVezToastMostrado > tiempoEsperaToast) {
                ultimaVezToastMostrado = currentTime
                intervaloTiempo *= 2
                tiempoPenalizacion = currentTime + intervaloTiempo
                var tiempo = intervaloTiempo / 1000
                postMessage("Muchos pasos registrados en poco tiempo, se pausará el conteo de pasos")
                postMessage("No se contarán pasos por los próximos $tiempo segundos")
            }
        }
    }


    private fun verificarPromedioDistanciaPorPaso(): Double {

        if (distancePerStep > 0.0) {
            return distancePerStep
        }
        //.415 longitud del paso en función de la estatura, bibliografia hasta abajo
        var distanciaPromedioPorPaso =.75
        if (EstaturaUser!=0.0){
            distanciaPromedioPorPaso = EstaturaUser * .415
        }
        return distanciaPromedioPorPaso
    }
    fun updateDistancePerStep(distanciaporpaso: Double) {
        distancePerStep = distanciaporpaso
    }



    fun getSteps(): Int? {
        return totalSteps.value
    }

    fun updateEstaturaUser(estatura: Double) {
        EstaturaUser = estatura
    }


    fun clearSteps() {
        totalSteps.postValue(0)
    }

    fun postMessage(message: String) {
        messageQueue.add(message)
        showToastEvent.postValue(messageQueue.toList()) // Crea una copia inmutable para publicar
    }

    fun messageShown() {
        if (messageQueue.isNotEmpty()) {
            messageQueue.removeAt(0) // Elimina el mensaje mostrado de la cola
        }
    }

    fun saveTrackingData(context: Context) {
        val sharedPref = context.getSharedPreferences("MyTrackingPref", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putInt("steps", totalSteps.value ?: 0)
            putFloat("distance", totalDistance.value ?: 0f)
            putString("timeElapsed", timeElapsed.value ?: "00:00")
            apply()
        }
    }

    fun loadTrackingData(context: Context) {
        val sharedPref = context.getSharedPreferences("MyTrackingPref", Context.MODE_PRIVATE)
        totalSteps.postValue(sharedPref.getInt("steps", 0))
        totalDistance.postValue(sharedPref.getFloat("distance", 0f))
        val time = (sharedPref.getString("timeElapsed", "00:00"))
        _timeElapsed.value = time!!
        Log.d("MapsView","$time")
    }

    //Función para verificar si hay una carrera en curso
    @RequiresApi(Build.VERSION_CODES.O)
    fun updateNextRaceDateTime() {
        isButtonEnabled.postValue(false) // Deshabilita el botón antes de iniciar la operación asincrónica
        viewModelScope.launch {
            try {
                val response = raceService.getRaces()
                if (response.isSuccessful) {
                    // Filtrar carreras que están dentro de los próximos 7 días
                    val races = response.body()?.data?.filter { race ->
                        val raceDate = ZonedDateTime.parse(race.date, DateTimeFormatter.ISO_ZONED_DATE_TIME)
                        val now = ZonedDateTime.now()
                        val daysUntilRace = Duration.between(now, raceDate).toDays()
                        daysUntilRace in 0..7
                    }
                    // Encontrar la carrera más próxima si existe
                    if (!races.isNullOrEmpty()) {
                        val nearestRace = races.minByOrNull { race ->
                            ZonedDateTime.parse(race.date, DateTimeFormatter.ISO_ZONED_DATE_TIME)
                        }
                        // Actualizar la fecha/hora de la próxima carrera
                        nearestRace?.date?.let {
                            // Convertir a la zona horaria específica
                            val zonedRaceDateTime = ZonedDateTime.parse(it, DateTimeFormatter.ISO_ZONED_DATE_TIME)
                            val raceDateTimeInLocal = zonedRaceDateTime.withZoneSameInstant(ZoneId.of("America/Mexico_City"))
                            nextRaceDateTime.postValue(raceDateTimeInLocal)
                            //ID carrera
                            raceId = nearestRace.id
                        }
                    } else {
                        // No hay carreras próximas, limpiar el valor
                        val predefinedRaceDateTime = ZonedDateTime.of(2000, 1, 1, 0, 0, 0, 0, ZoneId.of("America/Mexico_City"))
                        nextRaceDateTime.postValue(predefinedRaceDateTime)
                    }
                } else {
                    Log.d("ViewModel", "Error al obtener carreras")
                    nextRaceDateTime.postValue(null)
                }
            } catch (e: Exception) {
                Log.e("ViewModel", "Error: ${e.message}")
                nextRaceDateTime.postValue(null)
            }finally {
                isButtonEnabled.postValue(true) // Vuelve a habilitar el botón una vez completada la operación
            }
        }
    }

}

/*
longitud del paso en función de la estatura (.415) =
https://www.edu-casio.es/wp-content/uploads/2020/04/El-paso-humano.pdf

/*
 * Cálculo de la velocidad promedio de caminata en términos de pasos dados en un intervalo de tiempo específico (3 segundos).
 *
 * - Velocidad promedio de caminata: 1.4 metros por segundo (m/s).
 * - Longitud promedio de un paso: aproximadamente entre 0.7 a 0.8 metros por paso.
 *   Para este cálculo, se utilizará un valor promedio de 0.75 metros por paso.
 *
 * Distancia recorrida en 3 segundos a velocidad promedio:
 *   Distancia = Velocidad * Tiempo
 *             = 1.4 m/s * 3 s
 *             = 4.2 metros
 *
 * Conversión de distancia recorrida a número de pasos:
 *   Número de pasos = Distancia recorrida / Longitud promedio del paso
 *                   = 4.2 metros / 0.75 metros/paso
 *                   ≈ 5.6 pasos
 *
 * Dado que no se pueden dar pasos fraccionarios, esto significa que, en la práctica, una persona daría aproximadamente 6 pasos en 3 segundos,
 * considerando la velocidad y longitud de paso promedio.
 *
 * Nota: Estos números son aproximaciones y pueden variar según factores individuales como la estatura, la condición física, y la velocidad de caminata específica de cada persona.
 */

 */
