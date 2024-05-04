package com.cut.android.running.usecases.home

import RaceService
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cut.android.running.models.NextRaceInfo
import com.cut.android.running.provider.RetrofitInstance
import com.cut.android.running.provider.services.UserService
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class HomeViewModel : ViewModel() {

    private val raceService = RetrofitInstance.getRetrofit().create(RaceService::class.java)

    //Declarar MutableLiveData
    private val _nextRace = MutableLiveData<NextRaceInfo>()
    val nextRace: LiveData<NextRaceInfo> = _nextRace


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
                            Log.d("HVM DATA","DATA: ${user}")
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
                        val raceDateTimeInLocal = parseDate(race.date)
                        raceDateTimeInLocal?.let {
                            val now = ZonedDateTime.now(ZoneId.of("America/Mexico_City"))
                            val daysUntilRace = Duration.between(now.toLocalDate().atStartOfDay(), it.toLocalDate().atStartOfDay()).toDays()
                            daysUntilRace >= 0
                        } ?: false
                    }
                    val nearestRace = races?.minByOrNull { race ->
                        parseDate(race.date)?.let {
                            Log.d("HomeViewModel","$it")

                            Duration.between(ZonedDateTime.now(ZoneId.of("America/Mexico_City")), it).toDays()
                        } ?: Long.MAX_VALUE
                    }

                    if (nearestRace != null && nearestRace.id != null) {
                        val nearestRaceDate = parseDate(nearestRace.date)
                        nearestRaceDate?.let {
                            Log.d("HomeViewModel","$it")
                            val now = ZonedDateTime.now(ZoneId.of("America/Mexico_City"))
                            val daysUntilNearestRace = Duration.between(now.toLocalDate().atStartOfDay(), it.toLocalDate().atStartOfDay()).toDays().toInt()
                            val eventTime = if (daysUntilNearestRace == 0 && now.toLocalTime().isBefore(it.toLocalTime())) {
                                it.format(DateTimeFormatter.ofPattern("HH:mm"))
                            } else {
                                null
                            }

                            _nextRace.postValue(NextRaceInfo(true, daysUntilNearestRace, eventTime, nearestRace.id))
                        } ?: _nextRace.postValue(NextRaceInfo(false, 0, null, null))
                    } else {
                        _nextRace.postValue(NextRaceInfo(false, 0, null, null))
                    }
                } else {
                    Log.d("HomeViewModel", "Error al obtener carreras")
                }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun parseDate(dateStr: String): ZonedDateTime? {
        val formatters = listOf(
            DateTimeFormatter.ISO_ZONED_DATE_TIME,
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        )

        for (formatter in formatters) {
            try {
                return if (formatter == DateTimeFormatter.ISO_ZONED_DATE_TIME) {
                    ZonedDateTime.parse(dateStr, formatter)
                } else {
                    LocalDateTime.parse(dateStr, formatter).atZone(ZoneId.of("America/Mexico_City"))
                }
            } catch (e: Exception) {
                // Ignorar y probar con el siguiente formateador
            }
        }

        return null
    }



}
