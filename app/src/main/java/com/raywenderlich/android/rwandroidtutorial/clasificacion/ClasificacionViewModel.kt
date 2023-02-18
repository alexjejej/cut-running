package com.raywenderlich.android.rwandroidtutorial.clasificacion

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.rwandroidtutorial.models.Posicion
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ClasificacionViewModel: ViewModel() {
//    private val dbref = FirebaseDatabase.getInstance()
//        .getReference("clasificacion").child("historica")
    private lateinit var database: DatabaseReference

    private var auxArrayList: ArrayList<Posicion> = ArrayList()
    private var _listaClasificacion: MutableLiveData<ArrayList<Posicion>> = MutableLiveData<ArrayList<Posicion>>()
    val listaClasificacion: LiveData<ArrayList<Posicion>>
        get() = _listaClasificacion

    init {
        // this.obtenerClasificacion()
//        Log.d("ClasificacionVM", "Inicio del ViewModel Clasificacion")
        database = Firebase.database.getReference("clasificacion").child("historica")

        lateinit var positionListener: ValueEventListener

        viewModelScope.launch(Dispatchers.IO) {
            positionListener = object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("ClasificacionVM", "${snapshot.childrenCount}")
                    for (children in snapshot.children) {
                        Log.d("ClasificacionVM", "${children.getValue(Posicion::class.java)?.nombre ?: "No name"}")
                        val data = children.getValue(Posicion::class.java)
                        auxArrayList.add( data!! )
                    }
                    _listaClasificacion.value = auxArrayList
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.w("Cancelled", "loadPosition: onCancelled", error.toException())
                }
            }
            database.addValueEventListener(positionListener)
        }
    }

    fun obtenerClasificacion() {

    }
}