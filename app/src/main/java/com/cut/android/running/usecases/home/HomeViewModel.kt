package com.cut.android.running.usecases.home

import RaceService
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cut.android.running.provider.RetrofitInstance
import com.cut.android.running.provider.services.UserService
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {

    private val raceService = RetrofitInstance.getRetrofit().create(RaceService::class.java)

    //Declarar MutableLiveData
    private val _nextRace = MutableLiveData<Pair<Boolean, Int>>()

    //Exponer el MutableLiveData como LiveData inmutable
    val nextRace: LiveData<Pair<Boolean, Int>> = _nextRace

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
                Log.d("HOMEFRAGMENT viewmodel","ESTAS CONECTADO")

            } catch (e: Exception) {
                _apiConnection.postValue(false)
                Log.d("HOMEFRAGMENT viewmodel","NO ESTAS CONECTADO")

            }
        }
    }

    // Función para verificar si hay un evento próximo y mostrarlo en home
    @RequiresApi(Build.VERSION_CODES.O)
    fun checkNextRace() {
        viewModelScope.launch {
            try {
                val response = raceService.getRaces()
                if (response.isSuccessful) {
                    val races = response.body()?.data?.filter { race ->
                        val raceDate = ZonedDateTime.parse(race.date, DateTimeFormatter.ISO_ZONED_DATE_TIME)
                        val now = ZonedDateTime.now()
                        val daysUntilRace = Duration.between(now, raceDate).toDays()
                        daysUntilRace in 0..7
                    }
                    if (!races.isNullOrEmpty()) {
                        val nearestRace = races.minByOrNull { race ->
                            val raceDate = ZonedDateTime.parse(race.date, DateTimeFormatter.ISO_ZONED_DATE_TIME)
                            Duration.between(ZonedDateTime.now(), raceDate).toDays()
                            Log.d("HomeViewModel", "datos: $race")
                        }

                        val daysUntilNearestRace = Duration.between(ZonedDateTime.now(), ZonedDateTime.parse(nearestRace?.date, DateTimeFormatter.ISO_ZONED_DATE_TIME)).toDays()
                        _nextRace.postValue(Pair(true, daysUntilNearestRace.toInt()))
                    } else {
                        _nextRace.postValue(Pair(false, 0))
                    }
                } else {
                    Log.d("HomeViewModel", "Error al obtener carreras")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error: ${e.message}")
            }
        }
    }

}
