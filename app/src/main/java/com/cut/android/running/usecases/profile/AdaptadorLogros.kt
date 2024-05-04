package com.cut.android.running.usecases.profile

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.cut.android.running.R
import com.cut.android.running.models.Achievement
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AdaptadorLogros(private val logros: List<Achievement>) : RecyclerView.Adapter<AdaptadorLogros.LogroViewHolder>() {

    class LogroViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nombreLogro: TextView = view.findViewById(R.id.textViewNombreLogro)
        val fechaLogro: TextView = view.findViewById(R.id.textViewFechaLogro)
        val imagenLogro: ImageView = view.findViewById(R.id.imageViewLogro)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LogroViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_logros_user, parent, false)
        return LogroViewHolder(view)
    }

    override fun onBindViewHolder(holder: LogroViewHolder, position: Int) {
        val logro = logros[position]
        holder.nombreLogro.text = logro.name
        // Formateo de la fecha
        try {
            // Formato original de la fecha
            val formatoOriginal = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            formatoOriginal.timeZone = TimeZone.getTimeZone("America/Mexico_City") // Asegúrate de usar la zona horaria correcta

            // Formato deseado
            val formatoDeseado = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())

            // Parsea la fecha original a un objeto Date
            val fecha = formatoOriginal.parse(logro.updateDate)

            // Convierte el objeto Date al nuevo formato
            fecha?.let {
                val fechaFormateada = formatoDeseado.format(it)
                holder.fechaLogro.text = fechaFormateada
            }
        } catch (e: Exception) {
            holder.fechaLogro.text = logro.updateDate // O manejar el error adecuadamente
        }

        Glide.with(holder.imagenLogro.context)
            .load(logro.photo)
            .placeholder(R.drawable.ic_launcher_background) // Usa un placeholder
            .into(holder.imagenLogro)
    }

    override fun getItemCount() = logros.size
}
