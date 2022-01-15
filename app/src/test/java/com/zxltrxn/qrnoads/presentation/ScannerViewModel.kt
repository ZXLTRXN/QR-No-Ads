package com.zxltrxn.qrnoads.presentation

import com.zxltrxn.qrnoads.models.Type
import junit.framework.Assert.assertEquals
import org.junit.Test
import java.net.MalformedURLException
import java.net.URL


class ScannerViewModelTest {
    var vm = ScannerViewModel()


    fun tearDown(){
        vm = ScannerViewModel()
    }

    @Test
    fun shouldReturnCorrectType(){

        val method = vm.javaClass.getDeclaredMethod("dataAnalyzer",String::class.java)
        method.isAccessible = true
        val params = arrayOfNulls<Any>(1)

        params[0] = "WIFI:S:RT-5GPON;T:WPA;P:Terge;H:false;;"
        assertEquals(Type.WIFI,method.invoke(vm,*params))

        params[0] = "tel:89232352533"
        assertEquals(Type.TELEPHONE,method.invoke(vm,*params))

        params[0] = "mailto:fwefewgweg@fewf.ru"
        assertEquals(Type.MAIL,method.invoke(vm,*params))

        params[0] = "urlto:fwegwgw.ru"
        assertEquals(Type.URL,method.invoke(vm,*params))

        params[0] = "http://mail.rug"
        assertEquals(Type.URL,method.invoke(vm,*params))

        params[0] = "tele:89232352533"
        assertEquals(Type.TEXT,method.invoke(vm,*params))

        params[0] = "market://details?id=org.example.foo"
        assertEquals(Type.MARKET,method.invoke(vm,*params))

    }

//    @Test
//    fun shouldChangeDataLiveCorrectly(){
//        var param = "WIFI:S:RT-5GPON;T:WPA;P:Terge;H:false;;"
//        var expected = "WIFI сеть\nтип WPA\nимя RT-5GPON"
//        vm.saveData(param)
//        var actual = vm.dataLive.value?.strVal?:""
//        assertEquals(expected,actual)
//
//        tearDown()
//
//        param = "WIFI:P:Terge;;"
//        expected = "WIFI сеть\nтип нет\nимя ???"
//        vm.saveData(param)
//        actual = vm.dataLive.value?.strVal?:""
//        assertEquals(expected,actual)
//
//        tearDown()
//
//        param = "впцрпкуп"
//        expected = "впцрпкуп"
//        vm.saveData(param)
//        actual = vm.dataLive.value?.strVal?:""
//        assertEquals(expected,actual)
//}

}
