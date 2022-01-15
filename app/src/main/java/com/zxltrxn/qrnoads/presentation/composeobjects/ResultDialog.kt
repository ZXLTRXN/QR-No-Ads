package com.zxltrxn.qrnoads.presentation.composeobjects

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.zxltrxn.qrnoads.R
import com.zxltrxn.qrnoads.models.Type

@Composable
fun ResultDialog(
    data:String,
    actionString: String,
    backFun:()->Unit,
    actionFun:()->Unit,
    copyFun:()->Unit
) {
    Column {
        AlertDialog(
            onDismissRequest = {
                backFun()
            },
            title = {
                Text(text = stringResource(id = R.string.result_title))
            },
            text = {
                Text(
                    text = data,
                    modifier = Modifier
                        .clickable { copyFun() }
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        actionFun()
                    }) {
                    Text(text = stringResource(id = R.string.search))
                }
            },
            dismissButton = {
                Button(
                    onClick = {
                        backFun()
                    }) {
                    Text(text = stringResource(id = R.string.back))
                }
            }
        )
    }
}