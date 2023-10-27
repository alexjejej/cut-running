package com.raywenderlich.android.rwandroidtutorial.Home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    // En este caso, simplemente vamos a mantener un MutableLiveData con el nombre del usuario.
    val usuario: MutableLiveData<String> = MutableLiveData("Alex")
}