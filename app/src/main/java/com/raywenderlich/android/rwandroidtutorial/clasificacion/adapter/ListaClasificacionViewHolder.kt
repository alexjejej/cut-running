package com.raywenderlich.android.rwandroidtutorial.clasificacion.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.clasificacion.ListaClasificacion

class ListaClasificacionViewHolder(view:View):RecyclerView.ViewHolder(view) {

    val numero = view.findViewById<TextView>(R.id.clasificacionnumero)
    val nombre = view.findViewById<TextView>(R.id.clasificacionname)
    val pasos = view.findViewById<TextView>(R.id.clasificacionpasos)


    fun render(ListaClasificacion: ListaClasificacion){
        numero.text = ListaClasificacion.numero
        nombre.text = ListaClasificacion.nombre
        pasos.text = ListaClasificacion.pasos

    }
}