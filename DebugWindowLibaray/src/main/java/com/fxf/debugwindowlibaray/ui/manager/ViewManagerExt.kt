package com.fxf.debugwindowlibaray.ui.manager

import android.view.View
import android.view.ViewGroup
import android.view.ViewManager

interface ViewManagerExt : ViewManager {

    /**
     * 不响应触摸事件
     */
    fun makeTouchable(touchable: Boolean, lp: ViewGroup.LayoutParams)

    /**
     * 不响应焦点，比如输入法
     */
    fun makeFocusable(focusable: Boolean, lp: ViewGroup.LayoutParams)

    /**
     * 支持拖动
     */
    fun move(target: View, dx: Float, dy: Float)

    /**
     * 是通过windowManger显示还是普通ViewGroup显示, 当是普通模式时，会在所有activity finish时主动释放[ViewManagerExt]的引用
     * @return true 是通过windowManger来显示的，需要权限
     */
    fun isWindowManger(): Boolean
}
