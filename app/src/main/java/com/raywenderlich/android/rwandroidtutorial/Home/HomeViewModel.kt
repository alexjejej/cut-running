package com.raywenderlich.android.rwandroidtutorial.Home

import android.content.Context
import android.provider.Settings.Global.getString
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.raywenderlich.android.runtracking.R

class HomeViewModel : ViewModel() {
    // En este caso, simplemente vamos a mantener un MutableLiveData con el nombre del usuario.
    val userName = ""
    val usuario: MutableLiveData<String> = MutableLiveData(userName)
}
