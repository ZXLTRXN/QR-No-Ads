package com.zxltrxn.qrnoads.presentation

import android.Manifest
import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.WifiNetworkSuggestion
import android.net.wifi.hotspot2.PasspointConfiguration
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Scaffold
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.content.ContextCompat.*
import com.google.android.material.composethemeadapter.MdcTheme
import com.zxltrxn.qrnoads.R
import com.zxltrxn.qrnoads.isURL
import com.zxltrxn.qrnoads.models.StringFromScanner
import com.zxltrxn.qrnoads.models.Type
import com.zxltrxn.qrnoads.presentation.composeobjects.AppBar
import com.zxltrxn.qrnoads.presentation.composeobjects.ResultDialog

import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
import android.net.wifi.WifiConfiguration
import android.provider.Settings.ACTION_WIFI_ADD_NETWORKS
import android.provider.Settings.EXTRA_WIFI_NETWORK_LIST


// https://stackoverflow.com/questions/8818290/how-do-i-connect-to-a-specific-wi-fi-network-in-android-programmatically
// https://developer.android.com/codelabs/jetpack-compose-state#
val TAG = "QRScanner"

class Scanner_Activity : AppCompatActivity(),ZBarScannerView.ResultHandler {
    private var scannerView:ZBarScannerView? = null
    private var scannerFrame: FrameLayout? = null

    private var permLauncher: ActivityResultLauncher<String>? = null
    private val vm by viewModels<ScannerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)
        registerPermissionLauncher()
        checkCameraPermission()

        scannerView = ZBarScannerView(this)
        scannerView?.setAutoFocus(true)

        scannerFrame = findViewById(R.id.scanner_frame)
        scannerFrame?.addView(scannerView)

        val resultDialog = findViewById<ComposeView>(R.id.dialog)

        resultDialog.setContent {
            MdcTheme {
                val data:StringFromScanner by vm.dataLive.observeAsState(vm.defaultVal)
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                ) {
                    AppBar(menuAction = MenuAction.Flashlight,actionFun = ::flashChange)
                }

                if(data !=vm.defaultVal){
                    ResultDialog(data = data.strVal,
                        backFun = ::backFromResultDialog,
                        actionString = "",
                        actionFun = ::action,
                        copyFun = ::copyToClipBoard
                    )
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        scannerView?.stopCamera()
    }

    override fun onResume() {
        super.onResume()
        scannerView?.setResultHandler(this)
//        scannerView?.stopCameraPreview() не работает
        if (vm.dataLive.value == vm.defaultVal) {
            scannerView?.startCamera()
        }
    }

    override fun handleResult(result: Result?) {
        if (result?.contents != null){
            vm.saveData(result.contents)
        }else{
            Toast.makeText(this,R.string.decryption_error,Toast.LENGTH_SHORT).show()
            Log.e(TAG, "handleResult: null content from camera")
            scannerView?.resumeCameraPreview(this)
        }
    }

    fun backFromResultDialog(){
        vm.setDefaultData()
        scannerView?.startCamera()
//        scannerView?.resumeCameraPreview(this)
    }

    fun action(){
        when(vm.dataLive.value?.type){
            Type.WIFI-> connectWifi()
            else-> searchInBrowser()
        }
    }

    fun searchInBrowser(){
        vm.dataLive.value?.let{
            var searchStr = it.strVal
            if(it.type != Type.URL) {
                searchStr.replace(' ', '+').also { searchStr = it }
                searchStr = "https://google.com/search?q=$searchStr"
            }

            val intent = Intent(Intent.ACTION_WEB_SEARCH).apply {
                putExtra(SearchManager.QUERY, searchStr)
            }

            if(intent.resolveActivity(packageManager) !=null) {
                startActivity(intent)
            }else{
                Toast.makeText(this,
                    R.string.open_browser_error, Toast.LENGTH_SHORT).show()
            }
        }
        if(vm.dataLive.value == null){
            Log.e(TAG, "searchInBrowser: null in dataLive.value")
        }
    }

    fun connectWifi(){
        val ssid = vm.dataLive.value?.asMap?.getOrElse("S"){"no ssid"}?:"no ssid"
        val pass = vm.dataLive.value?.asMap?.getOrElse("P"){"12345"}?:"12345"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            Log.d(TAG, "connectWifi: api >=Q")

            val suggestions = ArrayList<WifiNetworkSuggestion>()

//            val passpointConfig = PasspointConfiguration()
//            val suggPassPoint = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//                WifiNetworkSuggestion.Builder()
//                    .setSsid(ssid)
//                    .setPasspointConfig(passpointConfig)
//                    .build()
//            } else { null }

            suggestions.add(
                WifiNetworkSuggestion.Builder()
                    .setSsid(ssid)
                    .build()
            )

            suggestions.add(
                WifiNetworkSuggestion.Builder()
                .setSsid(ssid)
                .setWpa2Passphrase(pass)
                .build()
            )

            suggestions.add(
                WifiNetworkSuggestion.Builder()
                .setSsid(ssid)
                .setWpa3Passphrase(pass)
                .build()
            )

            val wifiManager = applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

            val status = wifiManager.addNetworkSuggestions(suggestions);
            if (status != WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
                Log.e(TAG, "connectWifi: suggestion fail")
            }

            val bundle = Bundle()
            bundle.putParcelableArrayList(EXTRA_WIFI_NETWORK_LIST, suggestions)
            val intent = Intent(ACTION_WIFI_ADD_NETWORKS)
            intent.putExtras(bundle)

            if(intent.resolveActivity(packageManager) !=null) {
                startActivity(intent)

            }else{
                Toast.makeText(this,
                    R.string.wifi_error, Toast.LENGTH_SHORT).show()
            }

        } else {

        }
    }

    fun copyToClipBoard(){
        Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("simple text", vm.dataLive.value?.strVal)
        clipboard.setPrimaryClip(clip)
    }

    fun flashChange(){
        scannerView?.flash?.let{
            scannerView?.flash = !it
        }
    }


    // Разрешение на камеру //
    private fun checkCameraPermission(){
        if(checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED){
            permLauncher?.launch(Manifest.permission.CAMERA)
        }
    }

    //callback для события запроса разрешения
    private fun registerPermissionLauncher(){
        permLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()){
            isGranted->
            if(!isGranted){
                Toast.makeText(this,R.string.no_permission,Toast.LENGTH_SHORT).show()
            }
        }
    }
}


