package com.raywenderlich.android.rwandroidtutorial.Logros.adapter

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.Logros.ListaLogros
import com.raywenderlich.android.rwandroidtutorial.Logros.Logro

class ListaLogrosViewHolder(view:View):RecyclerView.ViewHolder(view) {

    val nombre = view.findViewById<TextView>(R.id.logroname)
    val descripcion = view.findViewById<TextView>(R.id.logrodescripcion)
    val progreso = view.findViewById<TextView>(R.id.logroprogreso)
    val status = view.findViewById<TextView>(R.id.logrostatus)
    val photo = view.findViewById<ImageView>(R.id.imlogro)

    fun render(ListaLogros: Logro){
        nombre.text = ListaLogros.nombre
        descripcion.text = ListaLogros.descripcion
        progreso.text = ListaLogros.progreso
        status.text = ListaLogros.status
        Glide.with(photo.context).load(ListaLogros.photo).into(photo)
    }
}