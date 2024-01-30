package com.raywenderlich.android.rwandroidtutorial.usecases.logros

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raywenderlich.android.rwandroidtutorial.models.Achievement
import com.raywenderlich.android.rwandroidtutorial.provider.RetrofitInstance
import com.raywenderlich.android.rwandroidtutorial.provider.services.AchievementService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogrosViewModel: ViewModel() {
    private val achievementService = RetrofitInstance.getRetrofit().create(AchievementService::class.java)
    private var _listaLogros: MutableLiveData<ArrayList<Achievement>> = MutableLiveData()
    val listaLogros: LiveData<ArrayList<Achievement>>
        get() = _listaLogros

    init {
        fetchAchievements()
    }

    private fun fetchAchievements() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response = achievementService.getAchievements()
                if (response.isSuccessful && response.body() != null) {
                    // Actualizar LiveData con los datos recibidos
                    _listaLogros.postValue(response.body()?.data as ArrayList<Achievement>?)
                } else {
                    // Manejar caso de respuesta fallida
                    Log.e("API Error", "Failed to fetch achievements")
                }
            } catch (e: Exception) {
                // Manejar excepciones
                Log.e("API Error", "Exception when fetching achievements", e)
            }
        }
    }
}
