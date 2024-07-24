package com.bannking.app.model

interface ExceptionListener {
    fun uncaughtException(thread: Thread, throwable: Throwable)
}