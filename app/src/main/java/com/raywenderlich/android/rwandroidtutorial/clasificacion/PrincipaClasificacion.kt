package com.raywenderlich.android.rwandroidtutorial.clasificacion

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*
import com.raywenderlich.android.runtracking.R
import com.raywenderlich.android.rwandroidtutorial.Logros.ListaLogros
import com.raywenderlich.android.rwandroidtutorial.Logros.adapter.ListaLogrosAdapter

import com.raywenderlich.android.rwandroidtutorial.clasificacion.adapter.ListaClasificacionAdapter

class PrincipaClasificacion : AppCompatActivity() {

    private lateinit var dbref : DatabaseReference
    private lateinit var clasificacionArrayList : ArrayList<ListaClasificacion>
    private lateinit var clasificacionRecyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_principa_clasificacion)
        clasificacionRecyclerView = findViewById(R.id.rvClasificacion)
        clasificacionRecyclerView.layoutManager = LinearLayoutManager(this)
        clasificacionRecyclerView.setHasFixedSize(true)
        //logrosRecyclerView.addItemDecoration(decoration)
        clasificacionArrayList = arrayListOf<ListaClasificacion>()

        ObtenerClasificacion(this)
    }

    private fun ObtenerClasificacion(context: Context) {

        // obtiene la clasificacion de la bd
        Log.d("array:","obteniendo datos")
        dbref = FirebaseDatabase.getInstance().getReference("clasificacion").child("historica")
        dbref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    for (ClasificacionSnapshot in snapshot.children) {

                        val datos = ClasificacionSnapshot.getValue(ListaClasificacion::class.java)
                        clasificacionArrayList.add(datos!!)
                        clasificacionArrayList.sortByDescending { it.pasos }
                        //}
                    }
                    clasificacionRecyclerView.adapter = ListaClasificacionAdapter(clasificacionArrayList,context)

                }else{
                    Log.d("array:","no hay datos")
                }
            }
            override fun onCancelled(error: DatabaseError) {
                Log.d("error:","error en principalclasificacion"+error)
            }
        })
    }


}