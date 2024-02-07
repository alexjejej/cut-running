package com.cut.android.running

import android.app.Application

import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class DIContainer : Application(){

    //private val trackingDatabase by lazy { TrackingDatabase.getDatabase(this) }
    //val trackingRepository by lazy { TrackingRepository(trackingDatabase.getTrackingDao()) }
}