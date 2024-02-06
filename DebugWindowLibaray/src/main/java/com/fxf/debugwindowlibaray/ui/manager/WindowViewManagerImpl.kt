package com.fxf.debugwindowlibaray.ui.manager

import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.view.WindowManager
import com.fxf.debugwindowlibaray.DebugWindowInitializer

/**
 * WindowManager 实现view的添加和隐藏
 */
class WindowViewManagerImpl(private val windowManager: WindowManager) : ViewManagerExt, ViewManager by windowManager {
    private val ctx = DebugWindowInitializer.application
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

    override fun move(target: View, dx: Float, dy: Float) {
        val lp = target.layoutParams
        if (lp !is WindowManager.LayoutParams) return
        // 根据gravity确定能够移动的范围，gravity不支持left和right，请使用start和end
        var xFactory = 1
        var yFactory = 1
        var minStart = 0
        var minTop = 0
        var maxEnd = ctx.resources.displayMetrics.widthPixels - target.measuredWidth
        var maxBottom = ctx.resources.displayMetrics.heightPixels - target.measuredHeight
        if (lp.gravity and Gravity.START == Gravity.START) {
            xFactory = 1
        } else if (lp.gravity and Gravity.END == Gravity.END) {
            xFactory = -1
        } else {
            minStart = -(ctx.resources.displayMetrics.widthPixels - target.measuredWidth) / 2
            maxEnd = -minStart
        }

        if (lp.gravity and Gravity.TOP == Gravity.TOP) {
            yFactory = 1
        } else if (lp.gravity and Gravity.BOTTOM == Gravity.BOTTOM) {
            yFactory = -1
        } else {
            minTop = -(ctx.resources.displayMetrics.heightPixels - target.height) / 2
            maxBottom = -minTop
        }
        target.translationX += dx
        target.translationX += dy

        lp.x += dx.toInt() * xFactory
        lp.y += dy.toInt() * yFactory
        // 范围限制
        if (lp.x < minStart) {
            lp.x = minStart
        }
        if (lp.x > maxEnd) {
            lp.x = maxEnd
        }
        if (lp.y < minTop) {
            lp.y = minTop
        }
        if (lp.y > maxBottom) {
            lp.y = maxBottom
        }
        updateViewLayout(target, lp)
    }

    override fun isWindowManger(): Boolean = true
}
