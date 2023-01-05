package com.raywenderlich.android.rwandroidtutorial.Logros

class ListaLogrosProvider {
    companion object{
        val listLogros = listOf<ListaLogros>(
            ListaLogros("500 pasos","Camina por más de 500 pasos",
                "200 de 500","En progreso",
                "https://cdn-icons-png.flaticon.com/512/233/233146.png"),
            ListaLogros("1000 pasos","Camina por más de 1000 pasos",
                "200 de 1000","En progreso",
                "https://cdn-icons-png.flaticon.com/512/233/233146.png"),
            ListaLogros("1500 pasos","Camina por más de 1500 pasos",
                "200 de 1500","En progreso",
                "https://cdn-icons-png.flaticon.com/512/233/233146.png"))
    }
}