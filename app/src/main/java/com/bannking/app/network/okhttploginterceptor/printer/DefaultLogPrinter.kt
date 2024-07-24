package com.bannking.app.network.okhttploginterceptor.printer

import android.util.Log
import com.bannking.app.network.okhttploginterceptor.Priority

class DefaultLogPrinter : IPrinter {
    override fun print(priority: Priority, tag: String, msg: String) {
        Log.println(priority.toInt(), tag, msg)
    }
}