package com.raywenderlich.android.rwandroidtutorial.clasificacion

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.runtracking.R

import com.raywenderlich.android.rwandroidtutorial.clasificacion.adapter.ListaClasificacionAdapter

class PrincipaClasificacion : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principa_clasificacion)
        initRecyclerView()
    }

    private fun initRecyclerView(){
        val manager = LinearLayoutManager(this)
        val decoration = DividerItemDecoration(this,manager.orientation)
        val recyclerView = findViewById<RecyclerView>(R.id.rvClasificacion)
        recyclerView.layoutManager = manager
        recyclerView.adapter = ListaClasificacionAdapter(ListaClasificacionProvider.listClasificacion)
        recyclerView.addItemDecoration(decoration)
    }
}