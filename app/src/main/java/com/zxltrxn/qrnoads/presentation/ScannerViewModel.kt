package com.zxltrxn.qrnoads.presentation

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zxltrxn.qrnoads.isURL
import com.zxltrxn.qrnoads.models.StringFromScanner
import com.zxltrxn.qrnoads.models.Type
import com.zxltrxn.qrnoads.toMap
import java.util.Scanner

class ScannerViewModel:ViewModel() {
    val defaultVal = StringFromScanner("",Type.TEXT)

    private val dataLiveMutable = MutableLiveData<StringFromScanner>(defaultVal)
    val dataLive:LiveData<StringFromScanner> = dataLiveMutable //getter

    fun setDefaultData(){ dataLiveMutable.value = defaultVal }

    fun saveData(string:String):Map<String,String>? {
        val type = dataAnalyzer(string)
        var params:Map<String,String>? = null
        var resultStr = string

        when(type){
            Type.WIFI->{
                params = string.toMap()
                val typeName = params.getOrElse("T"){"нет"}
                val name = params.getOrElse("S"){"???"}
                resultStr = "WIFI сеть\nтип ${typeName}\nимя ${name}"
            }
            else->{}
        }
        dataLiveMutable.value = StringFromScanner(resultStr,type)
        return params
    }


// https://github.com/zxing/zxing/wiki/Barcode-Contents
    private fun dataAnalyzer(string:String):Type{
        var type = Type.TEXT
        when{
            // doesnt change the order!
            string.startsWith(prefix = "mailto:", ignoreCase = true)
            ->type = Type.MAIL

            string.isURL() || string.startsWith(prefix = "urlto:", ignoreCase = true)
            ->type = Type.URL

            string.startsWith(prefix = "wifi:", ignoreCase = true)
            ->type = Type.WIFI

            string.startsWith(prefix = "tel:", ignoreCase = true)
            ->type = Type.TELEPHONE


            else -> {}
        }
        return type
    }

}