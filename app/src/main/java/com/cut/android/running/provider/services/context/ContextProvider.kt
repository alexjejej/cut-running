package com.cut.android.running.provider.services.context

import androidx.appcompat.app.AppCompatActivity
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ContextProvider @Inject constructor() {
    private lateinit var _context: AppCompatActivity

    /** Obtiene el contexto definido **/
    fun getContext(): AppCompatActivity = _context

    /** Establece un nuevo contexto **/
    fun setContext(context: AppCompatActivity) {
        _context = context
    }
}