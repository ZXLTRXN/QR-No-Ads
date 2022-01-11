package com.zxltrxn.qrnoads.presentation

import android.Manifest
import android.app.SearchManager
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.res.stringResource
import androidx.core.content.ContextCompat.*
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.composethemeadapter.MdcTheme
import com.zxltrxn.qrnoads.R
import com.zxltrxn.qrnoads.isURL
import com.zxltrxn.qrnoads.presentation.composeobjects.ResultDialog

import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView
// https://stackoverflow.com/questions/8818290/how-do-i-connect-to-a-specific-wi-fi-network-in-android-programmatically
// https://github.com/zxing/zxing/wiki/Barcode-Contents
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

//      другой способ создания
//        vm = ViewModelProvider(this).get(ScannerViewModel::class.java)

        val resultDialog = findViewById<ComposeView>(R.id.dialog)

        resultDialog.setContent {
            MdcTheme {
                val data:String by vm.dataLive.observeAsState(vm.defaultVal)
                if(data !=vm.defaultVal){
                    ResultDialog(data = data,
                        backFun = ::backFromResultDialog,
                        searchFun = ::searchInBrowser,
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
            scannerView?.resumeCameraPreview(this)
        }
    }

    fun backFromResultDialog(){
        vm.saveData(vm.defaultVal)
        scannerView?.startCamera()
        scannerView?.resumeCameraPreview(this)
    }

    fun searchInBrowser(){
        vm.dataLive.value?.let{
            var searchStr = it
            if(!it.isURL()) {
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

    fun copyToClipBoard(){
        Toast.makeText(this, R.string.copied_to_clipboard, Toast.LENGTH_SHORT).show()
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip: ClipData = ClipData.newPlainText("simple text", vm.dataLive.value)
        clipboard.setPrimaryClip(clip)
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
