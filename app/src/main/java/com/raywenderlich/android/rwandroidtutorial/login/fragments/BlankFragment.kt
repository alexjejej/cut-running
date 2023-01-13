package com.raywenderlich.android.rwandroidtutorial.login.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import com.raywenderlich.android.runtracking.R


/** Fragment de ejemplo **/
class BlankFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    lateinit var btnReturn: Button

    /**
     * Se ejecuta cuando se crea una instancia del fragment
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Accedemos a los argumentos
        arguments?.let {
        // "let" hace algo con el objeto en caso de que este no sea null (ejecutar si no es null)
            param1 = it.getString(ARG_PARAM1) // Obtiene los valores del Bundle segun el Key que se de como parametro
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val rootView = inflater.inflate(R.layout.fragment_blank, container, false)
        btnReturn = rootView.findViewById(R.id.btnReturn)

        btnReturn.setOnClickListener {
            Log.d("Fragment", "button de fragment")
        }
        return rootView
    }

    /**
     * Metodo publico mediante el cual se intancia el fragment
     * Lo unico que hace este metodo es devolver el fragment
     */
    companion object {
        const val ARG_PARAM1 = "param1"
        const val ARG_PARAM2 = "param2"

        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            BlankFragment().apply {
                /**
                 * Con "apply" aplicacmos lo que se indique, en este caso se indica que aplique
                 * un Bundle a la propiedad "arguments" del fragment
                 */
                arguments = Bundle().apply {
                    // "arguments es una propiedad de fragment (viene por defecto)"
                    putString(ARG_PARAM1, param1) // Pares Key, Value
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}