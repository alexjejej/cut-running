package com.cut.android.running.usecases.gestioncarreras.adapter

import android.content.ContentValues.TAG
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cut.android.running.R
import com.cut.android.running.models.Race

class UserRaceAdapter(private val onClick: (Race) -> Unit): RecyclerView.Adapter<UserRaceAdapter.RaceViewHolder>() {

    var races = listOf<Race>()
        set(value) {
            field = value
            Log.d(TAG, "UserRaceAdapter - datos establecidos: $value")
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.race_list_item, parent, false)
        return RaceViewHolder(view, onClick)
    }

    override fun getItemCount(): Int {
        Log.d(TAG, "UserRaceAdapter - getItemCount: ${races.size}")
        return races.size
    }
    override fun onBindViewHolder(holder: RaceViewHolder, position: Int) {
        Log.d(TAG, "UserRaceAdapter - onBindViewHolder - Posición: $position")
        val race = races[position]
        holder.bind(race)
    }

    class RaceViewHolder(view: View, val onClick: (Race) -> Unit): RecyclerView.ViewHolder(view) {
        private var currentRace: Race? = null

        init {
            view.setOnClickListener {
                currentRace?.let { onClick(it) }
            }
        }

        private val imgRace: ImageView = view.findViewById(R.id.imgRace)
        private val lblRaceName: TextView = view.findViewById(R.id.lblRaceName)
        private val lblDate: TextView = view.findViewById(R.id.lblDate)
        private val lblCedeAcronym: TextView = view.findViewById(R.id.lblCedeAcronym)

        fun bind(race: Race) {
            currentRace = race
            lblRaceName.text = race.name
            lblDate.text = race.date
            imgRace.setImageResource(R.drawable.flag)

            // Verificar si 'UC' es nulo antes de intentar acceder a 'acronym'
            if (race.UC != null) {
                lblCedeAcronym.text = race.UC.acronym
            } else {
                lblCedeAcronym.text = "N/A" // O cualquier valor por defecto que consideres apropiado
            }
        }

    }
}
