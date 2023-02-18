package com.raywenderlich.android.rwandroidtutorial.Logros

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.rwandroidtutorial.models.Logro
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class LogrosViewModel: ViewModel() {
    private lateinit var database: DatabaseReference
    private var auxArrayLiveData: ArrayList<Logro> = ArrayList()

    // Implementacion de Backing properties
    // Hace que una propiedad sea editabldel desde ViewModel (o clase que lo contenga)
    // pero que dicha propiedad sea expuesta como solo lectura
    private var _listaLogros: MutableLiveData<ArrayList<Logro>> = MutableLiveData()
    val listaLogros: LiveData<ArrayList<Logro>>
        get() = _listaLogros

    init {
        database = Firebase.database.getReference("logros")

        lateinit var logroListener: ValueEventListener

        viewModelScope.launch(Dispatchers.IO) {
            logroListener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("LogrosVM", "Cantidad de hijos: ${snapshot.childrenCount}")
                    for(children in snapshot.children) {
                        Log.d("LogrosVM", "Nombre: ${children.getValue(Logro::class.java)?.titulo ?: "No name"}")
                        val data = children.getValue(Logro::class.java)
                        auxArrayLiveData.add(data!!)
                    }
                    _listaLogros.value = auxArrayLiveData
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("Cancelled", "loadLogro: onCancelled", error.toException())
                }
            }
            database.addValueEventListener(logroListener)
        }
    }
}