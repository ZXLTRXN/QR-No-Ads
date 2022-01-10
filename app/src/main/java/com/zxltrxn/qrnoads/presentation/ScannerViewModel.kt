package com.zxltrxn.qrnoads.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class ScannerViewModel:ViewModel() {
    val defaultVal = ""
    private val dataLiveMutable = MutableLiveData<String>(defaultVal)
    val dataLive:LiveData<String> = dataLiveMutable //getter

    fun saveData(data:String) {dataLiveMutable.value = data}
}