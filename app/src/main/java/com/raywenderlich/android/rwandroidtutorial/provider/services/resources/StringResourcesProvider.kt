package com.raywenderlich.android.rwandroidtutorial.provider.services.resources

import android.content.Context
import androidx.annotation.StringRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class StringResourcesProvider @Inject constructor(
    @ApplicationContext private val _context: Context
){

    /** Retorna un recurso del archivo de strings **/
    fun getString(@StringRes stringResId: Int): String = _context.getString(stringResId)
}