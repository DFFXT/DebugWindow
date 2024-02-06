package com.fxf.debugwindowlibaray.ui.manager

import android.view.View
import android.view.ViewGroup
import android.view.ViewManager
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.core.view.updateLayoutParams

/**
 * 普通模式，如果没有悬浮窗权限，则可以使用这个
 * @param viewGroup 父布局为普通view
 */
open class CommonViewManagerImpl(private val viewGroup: ViewGroup) :
    ViewManagerExt,
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

    override fun addView(p0: View, p1: ViewGroup.LayoutParams) {
        viewGroup.addView(p0, p1)
        // 适配windowManger里面设置的gravity属性
        if (p1 is WindowManager.LayoutParams) {
            p0.layoutParams?.let {
                if (it is FrameLayout.LayoutParams) {
                    p0.updateLayoutParams<FrameLayout.LayoutParams> {
                        gravity = p1.gravity
                    }
                }
            }
        }
    }

    override fun move(target: View, dx: Float, dy: Float) {
        target.translationX += dx
        target.translationY += dy
        if (target.x < 0) {
            target.translationX -= target.x
        } else if (target.x + target.width > viewGroup.width) {
            target.translationX -= target.x + target.width - viewGroup.width
        }
        if (target.y < 0) {
            target.translationY -= target.y
        } else if (target.y + target.height > viewGroup.height) {
            target.translationY -= target.y + target.height - viewGroup.height
        }
    }

    override fun isWindowManger() = false
}
