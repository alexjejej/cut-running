package com.cut.android.running.Carreras.Adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cut.android.running.R
import com.cut.android.running.models.RankingItem

class RankingAdapter(private val items: List<RankingItem>) : RecyclerView.Adapter<RankingAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.ranking_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        holder.bind(item)
    }

    override fun getItemCount(): Int = items.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtPosicion: TextView = itemView.findViewById(R.id.txtPosicion)
        private val txtNombre: TextView = itemView.findViewById(R.id.txtNombre)
        private val txtTiempo: TextView = itemView.findViewById(R.id.txtTiempo)

        fun bind(item: RankingItem) {
            txtPosicion.text = item.position.toString()
            txtNombre.text = item.nombre
            txtTiempo.text = item.tiempo
        }
    }
}
