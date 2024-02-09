package com.raywenderlich.android.rwandroidtutorial.usecases.gestionuc

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raywenderlich.android.rwandroidtutorial.models.UniversityCenter
import com.raywenderlich.android.rwandroidtutorial.provider.RetrofitInstance
import com.raywenderlich.android.rwandroidtutorial.provider.services.UcService
import kotlinx.coroutines.launch

class UcManagementViewModel: ViewModel() {

    private val TAG: String = this::class.java.simpleName
    val getUcModel = MutableLiveData<List<UniversityCenter>?>()

    /**
     * Get University Centers
     */
    fun getUc() {
        viewModelScope.launch {
            try {
                val call = RetrofitInstance.getRetrofit().create(UcService::class.java).getUc()
                val result = call.body()
                if (result != null && result.isSuccess) {
                    getUcModel.postValue(result.data)
                }
                else getUcModel.postValue(null)
            }
            catch (e: Exception) {
                Log.e(TAG, "Error al obtener centros universitarios", e)
            }
        }
    }
}