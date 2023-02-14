package com.raywenderlich.android.rwandroidtutorial.Logros

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.Logros.adapter.ListaLogrosAdapter
import kotlin.collections.ArrayList

class PrincipalLogros : AppCompatActivity() {

    private lateinit var dbref : DatabaseReference
    private lateinit var LogrosArrayList : ArrayList<ListaLogros>
    private lateinit var logrosRecyclerView: RecyclerView



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principal_logros)

        //val manager = LinearLayoutManager(this)
        //val decoration = DividerItemDecoration(this,manager.orientation)
        //logrosRecyclerView.layoutManager = manager

        logrosRecyclerView = findViewById(R.id.rvLogros)
        logrosRecyclerView.layoutManager = LinearLayoutManager(this)
        logrosRecyclerView.setHasFixedSize(true)
        //logrosRecyclerView.addItemDecoration(decoration)
        LogrosArrayList = arrayListOf<ListaLogros>()
        //insertar()
        ObtenerDatos(this)

    }

    private fun insertar() {


        //Creador de logros aleatorios
        val random = 1
        val titulo = "¡Mi primer paso!"
        val database = Firebase.database
        val myRef = database.getReference("logros").child(""+titulo)
        val lista = ListaLogros(""+titulo,"Logra tu primer paso en la aplicación","",
            random,"https://cdn-icons-png.flaticon.com/512/233/233146.png")
        myRef.setValue(lista)

    }


    private fun ObtenerDatos(context: Context) {
        // variables locales
        val sharedPreferences =  context.getSharedPreferences("Datos", Context.MODE_PRIVATE)
        var pasosT = sharedPreferences.getInt("PasosTotales",0)
        // obtiene los logros de la bd
        Log.d("array:","obteniendo datos")
        dbref = FirebaseDatabase.getInstance().getReference("logros")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (logrosSnapshot in snapshot.children) {
                        // logros
                        //if (Integer.parseInt(logrosSnapshot.child("pasos").value.toString())<=pasosT) {
                            val datos = logrosSnapshot.getValue(ListaLogros::class.java)
                            LogrosArrayList.add(datos!!)
                            LogrosArrayList.sortBy { it.pasos }
                        //}
                    }
                    logrosRecyclerView.adapter = ListaLogrosAdapter(LogrosArrayList,context)

                }else{
                    Log.d("array:","no hay datos")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }



}