import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cut.android.running.provider.RetrofitInstance
import com.cut.android.running.provider.services.UserService
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.SphericalUtil

class MapsViewModel : ViewModel() {
    val pathPoints = MutableLiveData<MutableList<LatLng>>(mutableListOf())
    val totalDistance = MutableLiveData<Float>(0f)
    val totalSteps = MutableLiveData<Int>(0)
    var distancie = 0.0
    var EstaturaUser = 0.0



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
        val currentSteps = totalSteps.value ?: 0
        totalSteps.postValue(currentSteps + 1)
        // Actualiza la distancia total
        val PasoDistancia = verificarPromedioPasos()
        totalDistance.postValue((currentSteps*PasoDistancia).toFloat())
    }

    fun getSteps(): Int? {
        return totalSteps.value
    }

    fun updateEstaturaUser(estatura: Double) {
        EstaturaUser = estatura
    }

    private fun verificarPromedioPasos(): Double {
        //.415 longitud del paso en función de la estatura
        // https://www.edu-casio.es/wp-content/uploads/2020/04/El-paso-humano.pdf

        var distanciaPromedioPorPaso =.75
        if (EstaturaUser!=0.0){
            distanciaPromedioPorPaso = EstaturaUser * .415
        }


        return distanciaPromedioPorPaso
    }


    fun clearSteps() {
        totalSteps.postValue(0)
    }
}
