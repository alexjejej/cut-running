package com.raywenderlich.android.rwandroidtutorial.Logros

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.Logros.adapter.ListaLogrosAdapter

class PrincipalLogros : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal_logros)
//        initRecyclerView()
    }

//    private fun initRecyclerView(){
//        val manager = LinearLayoutManager(this)
//        val decoration = DividerItemDecoration(this,manager.orientation)
//        val recyclerView = findViewById<RecyclerView>(R.id.rvLogros)
//        recyclerView.layoutManager = manager
//        recyclerView.adapter = ListaLogrosAdapter(ListaLogrosProvider.listLogros)
//        recyclerView.addItemDecoration(decoration)
//    }



}