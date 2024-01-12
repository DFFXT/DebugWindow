package com.fxf.debugwindow

import android.app.Application
import com.fxf.debugwindowlibaray.DebugWindowInitializer
import com.fxf.debugwindowlibaray.ViewDebugManager
import com.fxf.debugwindowlibaray.ui.pages.log.LogPage

class App : Application() {
    override fun onCreate() {
        super.onCreate()
        DebugWindowInitializer.viewDebugManager!!.addPage(LogPage())
    }
}
