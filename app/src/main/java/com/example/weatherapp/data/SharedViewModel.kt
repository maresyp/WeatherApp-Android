package com.example.weatherapp.data

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class SharedViewModel : ViewModel() {
    val refreshEvent = MutableLiveData<Unit>()

    fun refresh() {
        refreshEvent.value = Unit
    }
}