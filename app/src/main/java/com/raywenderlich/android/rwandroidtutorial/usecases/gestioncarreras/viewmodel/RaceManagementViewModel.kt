package com.raywenderlich.android.rwandroidtutorial.usecases.gestioncarreras.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raywenderlich.android.rwandroidtutorial.models.Race
import com.raywenderlich.android.rwandroidtutorial.models.dto.RaceDto
import com.raywenderlich.android.rwandroidtutorial.provider.RetrofitInstance
import com.raywenderlich.android.rwandroidtutorial.provider.services.RaceService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RaceManagementViewModel : ViewModel() {

    private val TAG: String = this::class.java.simpleName
    val getRaceModel = MutableLiveData<List<Race>?>()
    val raceActionsModel = MutableLiveData<Boolean>()


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
                if (result != null && result.isSuccess) {
//                    withContext(Dispatchers.Main) {
                        Log.d(TAG, "usando disparchers.main")
                        raceActionsModel.postValue(true)
//                    }
                }
                else {
                    raceActionsModel.postValue(false)
                }
            }
            catch (e: Exception) {
                Log.e(TAG, "Error al registrar carrera", e)
            }
        }
    }
}