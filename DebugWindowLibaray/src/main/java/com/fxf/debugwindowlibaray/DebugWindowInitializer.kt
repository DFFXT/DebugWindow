package com.fxf.debugwindowlibaray

import android.app.Application
import android.content.Context
import android.view.WindowManager
import androidx.startup.Initializer
import com.fxf.debugwindowlibaray.ui.manager.WindowViewManagerImpl
import com.fxf.debugwindowlibaray.util.hasOverlayPermission
import java.util.Collections

open class DebugWindowInitializer : Initializer<DebugWindowInitializer> {
    companion object {
        lateinit var application: Application
        lateinit var viewDebugManager: ViewDebugManager
    }
    override fun create(context: Context): DebugWindowInitializer {
        application = context.applicationContext as Application
        viewDebugManager = ViewDebugManager()
        viewDebugManager.init(application)
        if (hasOverlayPermission(application)) {
           viewDebugManager.setRootView(WindowViewManagerImpl(context.getSystemService(Context.WINDOW_SERVICE) as WindowManager))
        }
        return this
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }
}
