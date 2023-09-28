package com.fxf.debugwindow

import android.app.Application
import com.fxf.debugwindowlibaray.ViewDebugManager

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        ViewDebugManager().init(this)
    }
}
