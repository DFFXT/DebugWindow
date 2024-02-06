package com.fxf.debugwindow

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import androidx.activity.ComponentActivity
import com.fxf.debugwindowlibaray.DebugWindowInitializer
import com.fxf.debugwindowlibaray.ui.manager.CommonViewManagerImpl

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<View>(R.id.btn_next).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
        }
        // DebugWindowInitializer.viewDebugManager.setRootView(CommonViewManagerImpl(findViewById<ViewGroup>(android.R.id.content)))
    }
}
class SubActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // DebugWindowInitializer.viewDebugManager.setRootView(CommonViewManagerImpl(findViewById<ViewGroup>(android.R.id.content)))
    }
}