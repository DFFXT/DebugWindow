package com.fxf.debugwindowlibaray.ui.manager

import android.view.ViewGroup
import android.view.ViewManager

/**
 * 普通模式，如果没有悬浮窗权限，则可以使用这个
 * @param viewGroup 父布局为普通view
 */
open class CommonViewManagerImpl(private val viewGroup: ViewManager) : ViewManagerExt,
    ViewManager by viewGroup {

    /**
     * 不用实现，SpecialConstraintLayout内部有实现
     */
    override fun makeTouchable(touchable: Boolean, lp: ViewGroup.LayoutParams) {
    }

    /**
     * 不用实现
     */
    override fun makeFocusable(focusable: Boolean, lp: ViewGroup.LayoutParams) {
    }
}