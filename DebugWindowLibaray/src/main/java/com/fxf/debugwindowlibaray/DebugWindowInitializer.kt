package com.fxf.debugwindowlibaray

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import java.util.Collections

open class DebugWindowInitializer : Initializer<DebugWindowInitializer> {
    companion object {
        lateinit var application: Application
        var viewDebugManager: ViewDebugManager? = null
    }
    override fun create(context: Context): DebugWindowInitializer {
        application = context.applicationContext as Application
        viewDebugManager = ViewDebugManager()
        viewDebugManager!!.init(application)
        return this
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }
}
