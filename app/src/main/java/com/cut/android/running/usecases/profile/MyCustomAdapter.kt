package com.cut.android.running.usecases.profile

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.ArrayAdapter
import com.cut.android.running.R


class MyCustomAdapter(context: Context, private val resource: Int, objects: List<String>)
    : ArrayAdapter<String>(context, resource, objects) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return createItemView(position, convertView, parent)
    }

    private fun createItemView(position: Int, recycledView: View?, parent: ViewGroup): View {
        val view = recycledView ?: LayoutInflater.from(context).inflate(resource, parent, false)
        val textView = view.findViewById<TextView>(R.id.textView)
        textView.text = getItem(position)
        // Aquí puedes hacer cualquier configuración adicional a la vista, como aplicar la fuente.
        return view
    }
}


