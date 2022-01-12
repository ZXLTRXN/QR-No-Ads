package com.zxltrxn.qrnoads

import java.net.MalformedURLException
import java.net.URL
import java.util.*

// protocol is needed
fun String.isURL():Boolean{
    return try {
        URL(this)
        true
    } catch(e: MalformedURLException){
        false
    }
}

fun String.toMap(dropFirst:Boolean = true):Map<String,String>{
    val sc: Scanner = Scanner(this)
    sc.useDelimiter(":|;")
    val map = mutableMapOf<String, String>()

    if(dropFirst) sc.next()
    while(sc.hasNext()){
        val key = sc.next()
        if(key != "")
            map.put(key,sc.next())
    }
    sc.close()
    return map
}
