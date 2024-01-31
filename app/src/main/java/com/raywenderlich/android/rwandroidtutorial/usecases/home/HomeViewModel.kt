package com.raywenderlich.android.rwandroidtutorial.usecases.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.raywenderlich.android.rwandroidtutorial.provider.RetrofitInstance
import com.raywenderlich.android.rwandroidtutorial.provider.services.AchievementService
import com.raywenderlich.android.rwandroidtutorial.provider.services.UserService
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    //LiveData que será observado desde la UI
    private val _usuario = MutableLiveData<String>()
    val usuario: LiveData<String>
        get() = _usuario

    private val _roleId = MutableLiveData<Int?>()
    val roleId: MutableLiveData<Int?>
        get() = _roleId

    private val userService = RetrofitInstance.getRetrofit().create(UserService::class.java)

    // Llamada a la API para verificar el rol del usuario
    fun checkUserRole(userEmail: String?) {
        userEmail?.let { email ->
            viewModelScope.launch {
                try {
                    val response = userService.getUserByEmail(email)
                    if (response.isSuccessful) {
                        val user = response.body()?.data
                        _roleId.postValue(user?.Role?.id) // Actualiza el LiveData del roleId
                        _usuario.postValue(user?.firstname)
                        if (user != null) {
                            //Log.d("HVM DATA","DATA: ${user}")
                        }
                    } else {
                        // Manejo de errores
                        Log.d("HVM error","Error en la respuesta")
                    }
                } catch (e: Exception) {
                    // Manejo de excepciones
                    Log.d("HVM Exception","$e")
                }
            }
        }
    }
}
