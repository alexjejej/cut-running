package com.raywenderlich.android.rwandroidtutorial.provider.resources

import android.content.Context
import androidx.annotation.DrawableRes
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DrawableResourcesProvider @Inject constructor(
    @ApplicationContext private val _context: Context
) {

    fun getDrawable(@DrawableRes drawableResId: Int) = _context.getDrawable(drawableResId)
}