package com.cut.android.running.usecases.gestioncarreras.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.cut.android.running.R
import com.cut.android.running.models.Race

class RaceAdapter(private val onClick: (Race) -> Unit): RecyclerView.Adapter<RaceAdapter.RaceViewHolder>() {

    var races = listOf<Race>()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RaceViewHolder {
        val view = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.race_list_item, parent, false)
        return RaceViewHolder(view, onClick)
    }

    override fun getItemCount() = races.size

    override fun onBindViewHolder(holder: RaceViewHolder, position: Int) {
        val race = races[position]
        holder.bind(race)
    }

    class  RaceViewHolder(view: View, val onClick: (Race) -> Unit): RecyclerView.ViewHolder(view) {
        private var currentRace: Race? = null

        init {
            view.setOnClickListener {
                currentRace?.let {
                    onClick(it)
                }
            }
        }

        val lblRaceName: TextView = view.findViewById(R.id.lblRaceName)
        val lblDate: TextView = view.findViewById(R.id.lblDate)
        val lblCedeAcronym: TextView = view.findViewById(R.id.lblCedeAcronym)

        fun bind(race: Race) {
            currentRace = race
            lblRaceName.text = race.name
            lblDate.text = race.date
            lblCedeAcronym.text = race.UC.acronym
        }
    }

}