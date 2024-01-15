package com.fxf.debugwindow

import android.os.Bundle
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import com.fxf.debugwindowlibaray.DebugWindowInitializer
import com.fxf.debugwindowlibaray.ui.manager.CommonViewManagerImpl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // DebugWindowInitializer.viewDebugManager.setRootView(CommonViewManagerImpl(findViewById<ViewGroup>(android.R.id.content)))
    }
}