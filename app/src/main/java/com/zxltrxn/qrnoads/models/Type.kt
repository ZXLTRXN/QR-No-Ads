package com.zxltrxn.qrnoads.models

enum class Type(val prefix:String) {
    TEXT(""),
    URL("urlto:"),
    WIFI("wifi:"),
    TELEPHONE("tel:"),
    MAIL("mailto:"),
    MARKET("market:")
}