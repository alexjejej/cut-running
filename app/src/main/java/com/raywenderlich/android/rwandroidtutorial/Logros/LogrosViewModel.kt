package com.raywenderlich.android.rwandroidtutorial.Logros

import androidx.lifecycle.ViewModel

// TODO: Mover de fichero
data class Logro(
    val nombre:String,
    val descripcion:String,
    val progreso:String,
    val status:String,
    val photo:String
)

class LogrosViewModel: ViewModel() {
    // Implementacion de Backing properties
    // Hace que una propiedad sea editabldel desde ViewModel (o clase que lo contenga)
    // pero que dicha propiedad sea expuesta como solo lectura
    private var _listaLogros: List<Logro> = listOf<Logro>(
        Logro("500 pasos","Camina por más de 500 pasos",
            "200 de 500","En progreso",
            "https://cdn-icons-png.flaticon.com/512/233/233146.png"),
        Logro("1000 pasos","Camina por más de 1000 pasos",
            "200 de 1000","En progreso",
            "https://cdn-icons-png.flaticon.com/512/233/233146.png"),
        Logro("1500 pasos","Camina por más de 1500 pasos",
            "200 de 1500","En progreso",
            "https://cdn-icons-png.flaticon.com/512/233/233146.png")
    )
    val listaLogros: List<Logro>
        get() = _listaLogros
}