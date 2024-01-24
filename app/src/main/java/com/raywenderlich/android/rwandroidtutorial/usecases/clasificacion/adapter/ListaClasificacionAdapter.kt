package com.raywenderlich.android.rwandroidtutorial.usecases.clasificacion.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.models.Classification

class ListaClasificacionAdapter(private val listaclasificacion:List<Classification>, private val context : Context) : RecyclerView.Adapter<ListaClasificacionAdapter.ListaClasificacionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListaClasificacionViewHolder {
        val LayoutInflater = LayoutInflater.from(parent.context).inflate(R.layout.item_listaclasificacion,parent,false)
        return ListaClasificacionViewHolder(LayoutInflater)
    }

    override fun onBindViewHolder(holder: ListaClasificacionViewHolder, position: Int) {
        val currentitem = listaclasificacion[position]
        holder.nombre.text = currentitem.nombre
        holder.numero.text = (position+1).toString()
        holder.pasos.text = currentitem.pasos.toString()
        if((position+1)==1){
            holder.nombre.setTextColor(Color.parseColor("#D4AF37"))
            holder.numero.setTextColor(Color.parseColor("#D4AF37"))
            holder.pasos.setTextColor(Color.parseColor("#D4AF37"))
        }
        if((position+1)==2){
            holder.nombre.setTextColor(Color.parseColor("#C0C0C0"))
            holder.numero.setTextColor(Color.parseColor("#C0C0C0"))
            holder.pasos.setTextColor(Color.parseColor("#C0C0C0"))
        }
        if((position+1)==3){
            holder.nombre.setTextColor(Color.parseColor("#CD7F32"))
            holder.numero.setTextColor(Color.parseColor("#CD7F32"))
            holder.pasos.setTextColor(Color.parseColor("#CD7F32"))
        }

    }

    override fun getItemCount(): Int {
        return listaclasificacion.size
    }

    class ListaClasificacionViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val numero:TextView = view.findViewById(R.id.clasificacionnumero)
        val nombre:TextView = view.findViewById(R.id.clasificacionname)
        val pasos:TextView = view.findViewById(R.id.clasificacionpasos)

    }
}