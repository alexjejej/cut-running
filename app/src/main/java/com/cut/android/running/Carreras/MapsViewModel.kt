import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

class MapsViewModel : ViewModel() {
    val pathPoints = MutableLiveData<MutableList<LatLng>>(mutableListOf())
    val totalDistance = MutableLiveData<Float>(0f)
    val totalSteps = MutableLiveData<Int>(0)
    var distancie = 0.0


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

                    // Actualiza la distancia total
                    val currentTotalDistance = totalDistance.value ?: 0f
                    totalDistance.postValue(currentTotalDistance.plus(distance.toFloat()))
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
        val currentSteps = totalSteps.value ?: 0

        totalSteps.postValue(currentSteps + 1)

    }

    fun getSteps(): Int? {
        return totalSteps.value
    }

    private fun verificarDistancia(distance: Double): Boolean {
        val distanciaPromedioPorPaso = 0.75 // metros
        val desviacionPermitida = 0.25 // 25%
        val distanciaEsperada = 1 * distanciaPromedioPorPaso

        val limiteInferior = distanciaEsperada * (1 - desviacionPermitida)
        val limiteSuperior = distanciaEsperada * (1 + desviacionPermitida)

        return distance in limiteInferior..limiteSuperior
    }

    private fun verificarPromedio(currentSteps: Int): Boolean {
        val currentTotalDistance = totalDistance.value ?: 0f

        val distanciaPromedioPorPaso = 0.75 // metros
        val desviacionPermitida = 0.25 // 25%
        val distanciaEsperada = currentSteps * distanciaPromedioPorPaso

        val limiteInferior = distanciaEsperada * (1 - desviacionPermitida)
        val limiteSuperior = distanciaEsperada * (1 + desviacionPermitida)

        return currentTotalDistance in limiteInferior..limiteSuperior
    }


    fun clearSteps() {
        totalSteps.postValue(0)
    }
}
