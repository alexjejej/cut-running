package com.cut.android.running.usecases.home

import android.content.Context
import android.util.Log
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cut.android.running.provider.DatosUsuario
import com.cut.android.running.provider.RetrofitInstance
import com.cut.android.running.provider.services.UserService
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    //LiveData que será observado desde la UI
    private val _usuario = MutableLiveData<String>()
    val usuario: LiveData<String>
        get() = _usuario

    private val _roleId = MutableLiveData<Int?>()
    val roleId: MutableLiveData<Int?>
        get() = _roleId

    // Nuevo LiveData para el estado de la conexión
    private val _apiConnection = MutableLiveData<Boolean>()
    val apiConnection: LiveData<Boolean>
        get() = _apiConnection

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
                        _apiConnection.postValue(response.isSuccessful)
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

    // Nueva función para verificar la conexión con la API
    fun checkApiConnection(email: String) {
        viewModelScope.launch {
            try {
                val response = userService.getUserByEmail(email)
                _apiConnection.postValue(response.isSuccessful)
            } catch (e: Exception) {
                _apiConnection.postValue(false)
            }
        }
    }
}
