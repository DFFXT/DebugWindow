package com.fxf.debugwindowlibaray

import android.app.Application
import android.content.Context
import androidx.startup.Initializer
import java.util.Collections

open class DebugWindowInitializer : Initializer<DebugWindowInitializer> {
    override fun create(context: Context): DebugWindowInitializer {
        ViewDebugManager().init(context.applicationContext as Application)
        return this
    }

    override fun dependencies(): MutableList<Class<out Initializer<*>>> {
        return Collections.emptyList()
    }
}
