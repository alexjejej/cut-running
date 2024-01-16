package com.raywenderlich.android.rwandroidtutorial.usecases.home

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class HomeViewModel : ViewModel() {
    // En este caso, simplemente vamos a mantener un MutableLiveData con el nombre del usuario.
    val userName = ""
    val usuario: MutableLiveData<String> = MutableLiveData(userName)
}
