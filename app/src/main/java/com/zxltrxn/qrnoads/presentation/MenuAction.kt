package com.zxltrxn.qrnoads.presentation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.zxltrxn.qrnoads.R

sealed class MenuAction(@StringRes val label: Int, @DrawableRes val icon: Int){
    object Flashlight : MenuAction(R.string.flashlight,R.drawable.ic_baseline_flash_on_24)
}

