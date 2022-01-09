package com.zxltrxn.qrnoads

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat.*

import me.dm7.barcodescanner.zbar.Result
import me.dm7.barcodescanner.zbar.ZBarScannerView

class Scanner_Activity : AppCompatActivity(),ZBarScannerView.ResultHandler {
    private var zbView:ZBarScannerView? = null
    private var pLauncher: ActivityResultLauncher<String>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        registerPermissionLauncher()
        checkCameraPermission()

        zbView =  ZBarScannerView(this)
        setContentView(zbView)
    }

    override fun onPause() {
        super.onPause()
        zbView?.stopCamera()
    }

    override fun onResume() {
        super.onResume()
        zbView?.setResultHandler(this)
        zbView?.startCamera()
    }

    override fun handleResult(result: Result?) {
        Log.d("Test","data ${result?.contents}")
        //zbView?.resumeCameraPreview(this)
    }


    // Разрешение на камеру //
    private fun checkCameraPermission(){
        if(checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED){
            Toast.makeText(this,R.string.cam_run,Toast.LENGTH_SHORT).show()
        }
        else{
            pLauncher?.launch(Manifest.permission.CAMERA)
        }
    }

    //callback для события запроса разрешения
    private fun registerPermissionLauncher(){
        pLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()){
            isGranted->
            if(isGranted){
                Toast.makeText(this,R.string.cam_run,Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,R.string.no_permission,Toast.LENGTH_SHORT).show()
            }
        }
    }
}