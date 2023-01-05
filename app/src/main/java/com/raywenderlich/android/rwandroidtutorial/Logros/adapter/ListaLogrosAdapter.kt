package com.raywenderlich.android.rwandroidtutorial.Logros.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.Logros.ListaLogros

class ListaLogrosAdapter(private val listalogros:List<ListaLogros>) : RecyclerView.Adapter<ListaLogrosViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaLogrosViewHolder {
        val LayoutInflater=LayoutInflater.from(parent.context)
        return ListaLogrosViewHolder(LayoutInflater.inflate(R.layout.item_listalogros, parent, false))
    }

    override fun onBindViewHolder(holder: ListaLogrosViewHolder, position: Int) {
        val item = listalogros[position]
        holder.render(item)
    }

    override fun getItemCount(): Int {
        return listalogros.size
    }
}