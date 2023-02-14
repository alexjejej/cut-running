package com.raywenderlich.android.rwandroidtutorial.clasificacion

import androidx.lifecycle.ViewModel

data class Posicion(
    val numero:String,
    val nombre:String,
    val pasos:String
)

class ClasificacionViewModel: ViewModel() {
    private var _listaClasificacion: List<Posicion> = listOf(
        Posicion("1","Juan","500"),
        Posicion("2","Pedro","50"),
        Posicion("3","Roberto","103"),
        Posicion("4","Alex","102"),
        Posicion("5","Maria","180"),
        Posicion("6","Juana","101"),
        Posicion("7","Azul","100"),
        Posicion("8","Manolo","50")
    )

    val listaClasificacion: List<Posicion>
        get() = _listaClasificacion
}