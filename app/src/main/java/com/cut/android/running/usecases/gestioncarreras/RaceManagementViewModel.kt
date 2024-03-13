
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cut.android.running.models.Race
import com.cut.android.running.models.User
import com.cut.android.running.models.dto.RaceDto
import com.cut.android.running.provider.RetrofitInstance
import kotlinx.coroutines.launch

class RaceManagementViewModel : ViewModel() {

    private val TAG: String = this::class.java.simpleName
    val getRaceModel = MutableLiveData<List<Race>?>()
    val addRaceActionsModel = MutableLiveData<Boolean>()
    val updateRaceActionsModel = MutableLiveData<Boolean>()
    val deleteRaceActionsModel = MutableLiveData<Boolean>()
    val addUserRelationModel = MutableLiveData<Boolean>()
    val getRaceByUserModel = MutableLiveData<List<Race>?>()
    val getUserByRaceModel = MutableLiveData<List<User>?>()
    val deleteUserRelationModel = MutableLiveData<Boolean>()


    /**
     * Get Races
     */
    fun getRaces() {
        viewModelScope.launch {
            try {
                val call = RetrofitInstance.getRetrofit().create(RaceService::class.java).getRaces()
                val result = call.body()
                if (result != null && result.isSuccess) {
                    getRaceModel.postValue(result.data)
                }
                else getRaceModel.postValue(null)
            } catch (e: Exception) {
                Log.e(TAG, "Error al obtener carreras", e)
            }
        }
    }

    /**
     * Add new race to Database
     */
    fun addRace(race: RaceDto) {
        viewModelScope.launch {
            try {
                val call = RetrofitInstance.getRetrofit().create(RaceService::class.java).addRace(race)
                val result = call.body()
                if (result != null && result.isSuccess)
                    addRaceActionsModel.postValue(true)
                else
                    addRaceActionsModel.postValue(false)
            }
            catch (e: Exception) {
                Log.e(TAG, "Error al registrar carrera", e)
            }
        }
    }

    /**
     * Update a race
     */
    fun updateRace(race: RaceDto) {
        viewModelScope.launch {
            try {
                val call = RetrofitInstance.getRetrofit().create(RaceService::class.java).updateRace(race)
                val result = call.body()
                if (result != null && result.isSuccess)
                    updateRaceActionsModel.postValue(true)
                else
                    updateRaceActionsModel.postValue(true)
            }
            catch (e: Exception) {
                Log.e(TAG, "Error al actualizar carrera", e)
            }
        }
    }

    /**
     * Delete a race
     */
    fun deleteRace(id: Int) {
        viewModelScope.launch {
            try {
                val call = RetrofitInstance.getRetrofit().create(RaceService::class.java).deleteRace(id)
                val result = call.body()
                if (result != null && result.isSuccess)
                    deleteRaceActionsModel.postValue(true)
                else
                    deleteRaceActionsModel.postValue(false)
            }
            catch (e: Exception) {
                Log.e(TAG, "Error al eliminar carrera", e)
            }
        }
    }

    /**
     * Add realationship between user and race
     */
    fun addUserRelation(email: String, raceId: Int) {
        viewModelScope.launch {
            try {
                val call = RetrofitInstance.getRetrofit().create(RaceService::class.java).addUserRelation(email, raceId)
                val result = call.body()
                if (result != null && result.isSuccess)
                    addUserRelationModel.postValue(true)
                else
                    addUserRelationModel.postValue(false)
            }
            catch (e: Exception) {
                Log.e(TAG, "Error al registrarse en carrera", e)
            }
        }
    }

    fun getUserByRace(raceId: Int) {
        viewModelScope.launch {
            try {
                val call = RetrofitInstance.getRetrofit().create(RaceService::class.java).getUserByRace(raceId)
                val result = call.body()
                if (result != null && result.isSuccess)
                    getUserByRaceModel.postValue(result.data)
                else
                    getUserByRaceModel.postValue(null)
            }
            catch (e: Exception) {
                Log.e(TAG, "Error al obtener usuario por carrera")
            }
        }
    }

    fun getRaceByUser(email: String) {
        viewModelScope.launch {
            try {
                val call = RetrofitInstance.getRetrofit().create(RaceService::class.java).getRaceByUser(email)
                val result = call.body()
                if (result != null && result.isSuccess) {
                    getRaceByUserModel.postValue(result.data)
                }
                else
                    getRaceByUserModel.postValue(null)
            }
            catch (e: Exception) {
                Log.e(TAG, "Error al obtener carreras por usuario")
            }
        }
    }
}