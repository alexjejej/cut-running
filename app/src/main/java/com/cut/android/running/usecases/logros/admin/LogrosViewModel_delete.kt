import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cut.android.running.models.Achievement
import com.cut.android.running.provider.RetrofitInstance
import com.cut.android.running.provider.services.AchievementService
import kotlinx.coroutines.launch

class LogrosViewModel_delete : ViewModel() {

    private val _mensajeError = MutableLiveData<String>()
    val mensajeError: LiveData<String> get() = _mensajeError

    private val achievementService = RetrofitInstance.getRetrofit().create(AchievementService::class.java)

    fun obtenerLogros(callback: (List<Achievement>) -> Unit) {
        viewModelScope.launch {
            try {
                val response = achievementService.getAchievements()
                if (response.isSuccessful) {
                    val logros = response.body()?.data ?: emptyList()
                    callback(logros)
                } else {
                    _mensajeError.postValue("Error al obtener logros: ${response.errorBody()}")
                }
            } catch (e: Exception) {
                _mensajeError.postValue("Error de red: ${e.message}")
            }
        }
    }
    fun actualizarLogro(logro: Achievement, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = achievementService.updateAchievement(logro)
            callback(response.isSuccessful && response.body()?.data == true)
        }
    }


    fun eliminarLogro(logroId: Int, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = achievementService.deleteAchievement(logroId)
            callback(response.isSuccessful && response.body()?.data == true)
        }
    }

    fun desactivarLogro(logro: Achievement, callback: (Boolean) -> Unit) {
        viewModelScope.launch {
            val response = achievementService.updateAchievement(logro)
            callback(response.isSuccessful && response.body()?.data == true)
        }
    }

}
