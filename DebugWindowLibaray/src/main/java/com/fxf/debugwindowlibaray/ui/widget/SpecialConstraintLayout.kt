package com.fxf.debugwindowlibaray.ui.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.constraintlayout.widget.ConstraintLayout

open class SpecialConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    private var touchable = true
    private var focusable = true
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if (!touchable) return false
        return super.dispatchTouchEvent(ev)
    }

    open fun makeTouchable(touchable: Boolean) {
        this.touchable = touchable
    }
    open fun makeFocusable(focusable: Boolean) {
        this.focusable = focusable
        descendantFocusability = if (focusable) {
            ViewGroup.FOCUS_AFTER_DESCENDANTS
        } else {
            ViewGroup.FOCUS_BLOCK_DESCENDANTS
        }
    }
}