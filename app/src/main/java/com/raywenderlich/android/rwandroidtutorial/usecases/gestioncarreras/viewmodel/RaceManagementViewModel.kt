package com.raywenderlich.android.rwandroidtutorial.usecases.gestioncarreras.viewmodel

import android.util.Log
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raywenderlich.android.rwandroidtutorial.models.Race
import com.raywenderlich.android.rwandroidtutorial.provider.RetrofitInstance
import com.raywenderlich.android.rwandroidtutorial.provider.services.RaceService
import kotlinx.coroutines.launch

class RaceManagementViewModel : ViewModel() {

    private val TAG: String = this::class.java.simpleName
    val raceModel = MutableLiveData<List<Race>?>()

    fun getRaces() {
        viewModelScope.launch {
            try {
                val call = RetrofitInstance.getRetrofit().create(RaceService::class.java).getRaces()
                val result = call.body()
                if (result != null && result.isSuccess) {
                    raceModel.postValue(result.data)
                }
                else raceModel.postValue(null)
                /*requireActivity().runOnUiThread {
                    if (result != null && result.isSuccess) {
                        raceAdapter.races = result.data!!
                    } else {
                        Toast.makeText(requireContext(), "${result?.message ?: "Error desconocido"}", Toast.LENGTH_LONG).show()
                    }
                }*/
            } catch (e: Exception) {
                Log.e(TAG, "Error al obtener carreras", e)
                /*requireActivity().runOnUiThread {
                    Toast.makeText(requireContext(), "Error con la conexión", Toast.LENGTH_LONG).show()
                }*/
            }
        }
    }
}