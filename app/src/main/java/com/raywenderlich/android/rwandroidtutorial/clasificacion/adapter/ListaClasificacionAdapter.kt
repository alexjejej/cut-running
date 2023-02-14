package com.raywenderlich.android.rwandroidtutorial.clasificacion.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.Logros.ListaLogros
import com.raywenderlich.android.rwandroidtutorial.clasificacion.ListaClasificacion
import com.raywenderlich.android.rwandroidtutorial.clasificacion.Posicion

class ListaClasificacionAdapter(private val listaclasificacion:List<Posicion>) : RecyclerView.Adapter<ListaClasificacionViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaClasificacionViewHolder {
        val LayoutInflater=LayoutInflater.from(parent.context)
        return ListaClasificacionViewHolder(LayoutInflater.inflate(R.layout.item_listaclasificacion, parent, false))
    }

    override fun onBindViewHolder(holder: ListaClasificacionViewHolder, position: Int) {
        val item = listaclasificacion[position]
        holder.render(item)
    }

    override fun getItemCount(): Int {
        return listaclasificacion.size
    }
}