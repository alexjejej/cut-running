package com.cut.android.running.usecases.clasificacion

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cut.android.running.models.Classification
import com.cut.android.running.provider.RetrofitInstance
import com.cut.android.running.provider.services.ClassificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClasificacionViewModel : ViewModel() {

    private val classificationService = RetrofitInstance.getRetrofit().create(ClassificationService::class.java)
    private var _listaClasificacion = MutableLiveData<ArrayList<Classification>>()
    val listaClasificacion: LiveData<ArrayList<Classification>>
        get() = _listaClasificacion

    init {
        obtenerClasificaciones()
    }

    private fun obtenerClasificaciones() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = classificationService.getClassifications()
                if (response.isSuccessful && response.body() != null) {
                    val responseData = response.body()!!.data
                    if (responseData != null) {
                        _listaClasificacion.postValue(ArrayList(responseData))
                    }
                } else {
                    Log.e("API Error", "Error al obtener clasificaciones")
                }
            } catch (e: Exception) {
                Log.e("API Error", "Excepción al obtener clasificaciones", e)
            }
        }
    }
}
