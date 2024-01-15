package com.fxf.debugwindowlibaray.ui.manager

import android.view.ViewGroup
import android.view.ViewManager

interface ViewManagerExt: ViewManager {

    /**
     * 不响应触摸事件
     */
    fun makeTouchable(touchable: Boolean, lp: ViewGroup.LayoutParams)

    /**
     * 不响应焦点，比如输入法
     */
    fun makeFocusable(focusable: Boolean,  lp: ViewGroup.LayoutParams)
}