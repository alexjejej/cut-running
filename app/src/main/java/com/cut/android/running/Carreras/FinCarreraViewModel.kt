package com.cut.android.running.Carreras

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.cut.android.running.provider.resources.ManejadorAccionesFallidas

class FinCarreraViewModel : ViewModel() {
    private val _accionesFallidas = MutableLiveData<Boolean>()
    val accionesFallidas: LiveData<Boolean> = _accionesFallidas

    fun verificarAccionesFallidas(manejadorAcciones: ManejadorAccionesFallidas) {
        val tieneAcciones = manejadorAcciones.obtenerAccionesFallidas().any { accion ->
            accion.tipo in listOf("CrearClasificacion", "CrearClasificacionNueva", "consultarlogro")
        }
        _accionesFallidas.postValue(tieneAcciones)
    }
}
