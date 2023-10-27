package com.raywenderlich.android.rwandroidtutorial

import android.app.Application
import com.raywenderlich.android.rwandroidtutorial.Carrera.TrackingDatabase
import com.raywenderlich.android.rwandroidtutorial.Carrera.TrackingRepository
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DIContainer : Application(){

    private val trackingDatabase by lazy { TrackingDatabase.getDatabase(this) }
    val trackingRepository by lazy { TrackingRepository(trackingDatabase.getTrackingDao()) }
}