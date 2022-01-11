package com.zxltrxn.qrnoads.presentation.composeobjects

import android.icu.text.UnicodeSetIterator
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.painterResource
import com.zxltrxn.qrnoads.R
import com.zxltrxn.qrnoads.presentation.MenuAction


@Composable
fun AppBar(menuAction: MenuAction, actionFun:()->Unit) {
    TopAppBar {
        Text(stringResource(id = R.string.app_name))
        Spacer(Modifier.weight(1f, true))

        IconButton(onClick = { actionFun() }) {
            Icon(
                painter = painterResource(id = menuAction.icon),
                contentDescription = stringResource(id = menuAction.label)
            )
        }
    }
}


//Icon(
//painter = painterResource(id = R.drawable.ic_baseline_flash_on_24),
//contentDescription = stringResource(id = R.string.flashlight)
//)
