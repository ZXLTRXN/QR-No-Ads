package com.zxltrxn.qrnoads

import java.net.MalformedURLException
import java.net.URL

fun String.isURL():Boolean{
    return try {
        URL(this)
        true
    } catch(e: MalformedURLException){
        false
    }
}
