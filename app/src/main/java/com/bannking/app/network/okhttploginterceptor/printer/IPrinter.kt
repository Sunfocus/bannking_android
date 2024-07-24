package com.bannking.app.network.okhttploginterceptor.printer

import com.bannking.app.network.okhttploginterceptor.Priority

interface IPrinter {
    fun print(priority: Priority, tag: String, msg: String)
}