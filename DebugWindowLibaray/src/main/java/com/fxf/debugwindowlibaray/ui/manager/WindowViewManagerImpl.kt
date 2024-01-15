package com.fxf.debugwindowlibaray.ui.manager

import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.view.WindowManager

/**
 * WindowManager 实现view的添加和隐藏
 */
class WindowViewManagerImpl(private val windowManager: WindowManager): ViewManagerExt, ViewManager by windowManager {
    override fun makeTouchable(touchable: Boolean, lp: ViewGroup.LayoutParams) {
        lp as WindowManager.LayoutParams
        if (!touchable) {
            lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        } else {
            lp.flags = lp.flags and WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE.inv()
        }
    }

    override fun makeFocusable(focusable: Boolean, lp: ViewGroup.LayoutParams) {
        lp as WindowManager.LayoutParams
        if (!focusable) {
            lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        } else {
            lp.flags = lp.flags and WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE.inv()
        }
    }
}