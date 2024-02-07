package com.cut.android.running.provider.services

import androidx.lifecycle.MutableLiveData

class TrackingRepository {
    val isTracking = MutableLiveData<Boolean>(false)
    val totalSteps = MutableLiveData<Int>(0)
    val totalDistance = MutableLiveData<Float>(0f)

    // Métodos para actualizar los datos, que serán llamados desde el Service
    fun updateTracking(isTracking: Boolean) {
        this.isTracking.postValue(isTracking)
    }

    fun updateSteps(steps: Int) {
        totalSteps.postValue(steps)
    }

    fun updateDistance(distance: Float) {
        totalDistance.postValue(distance)
    }
}
