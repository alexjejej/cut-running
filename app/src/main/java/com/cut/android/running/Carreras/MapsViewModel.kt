import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

class MapsViewModel : ViewModel() {
    val pathPoints = MutableLiveData<MutableList<LatLng>>(mutableListOf())
    val totalDistance = MutableLiveData<Float>(0f)
    val totalSteps = MutableLiveData<Int>(0)
    var distancie = 0.0
    var EstaturaUser = 0.0
    //mostrar toast en viewmodel
    val showToastEvent = MutableLiveData<List<String>>()
    private val messageQueue: MutableList<String> = mutableListOf()
    // Lista para guardar los timestamps de cada paso dado
    private val pasosTimestamps = mutableListOf<Long>()
    private val maxPasosPorIntervalo = 8 // Máximo de pasos permitidos en el intervalo de 3s ¿Dudas? Revisar al fondo
    private var intervaloTiempo = 4000L // Intervalo de tiempo en milisegundos
    private var ultimaVezToastMostrado = 0L
    private val tiempoEsperaToast = 4000L // 4 segundos entre cada Toast
    private val intervaloTiempoOriginal = 4000L
    private var tiempoPenalizacion = 0L // Tiempo hasta que la penalización se restablece





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

    fun addStep() {
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
            totalDistance.postValue((currentSteps * pasoDistancia).toFloat())
        } else {
            if (currentTime - ultimaVezToastMostrado > tiempoEsperaToast) {
                ultimaVezToastMostrado = currentTime
                intervaloTiempo *= 2
                tiempoPenalizacion = currentTime + intervaloTiempo
                var tiempo = intervaloTiempo / 1000
                postMessage("Muchos pasos registrados en poco tiempo ¿Eres Flash?")
                postMessage("No se contarán pasos por los próximos $tiempo segundos")
            }
        }
    }


    private fun verificarPromedioDistanciaPorPaso(): Double {
        //.415 longitud del paso en función de la estatura, bibliografia hasta abajo

        var distanciaPromedioPorPaso =.75
        if (EstaturaUser!=0.0){
            distanciaPromedioPorPaso = EstaturaUser * .415
        }


        return distanciaPromedioPorPaso
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
