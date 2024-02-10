package com.cut.android.running.usecases.gestionuc

import UcService
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cut.android.running.models.UniversityCenter
import com.cut.android.running.provider.RetrofitInstance
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