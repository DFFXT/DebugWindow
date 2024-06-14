package com.fxf.debugwindowlibaray.ui.widget

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.viewdebug.util.touch.BaseDragHelper
import com.fxf.debugwindowlibaray.R
import com.fxf.debugwindowlibaray.ui.manager.ViewManagerExt

class TouchConstraintLayout @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : ConstraintLayout(context, attrs) {

    var viewManagerProvider:(()->ViewManagerExt?)? = null

    private val touchHelper = object : BaseDragHelper() {
        private val moveTarget = this@TouchConstraintLayout
        // 存储丢失的浮点数，不然精度丢失会累计导致严重偏移触摸位置
        private var ddx = 0f
        private var ddy = 0f
        override fun longClickMove(dx: Float, dy: Float) {
            val mDx = dx + ddx
            val mDy = dy + ddy
            ddx = mDx - mDx.toInt()
            ddy = mDy - mDy.toInt()
            viewManagerProvider?.invoke()?.move(moveTarget, mDx.toInt().toFloat(), mDy.toInt().toFloat())
        }

        override fun longOnClick(x: Float, y: Float): Boolean {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                moveTarget.foreground = AppCompatResources.getDrawable(context, R.drawable.view_debug_common_rect_stroke_1dp)
            }
            return true
        }

        override fun longClickUpMoved(x: Float, y: Float) {
            super.longClickUpMoved(x, y)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                moveTarget.foreground = null
            }
        }

        override fun longClickUpNoMove(x: Float, y: Float) {
            super.longClickUpNoMove(x, y)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                moveTarget.foreground = null
            }
        }
    }// .apply { attachToView(this@TouchConstraintLayout) }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return touchHelper.onTouch(this, event)
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        return touchHelper.onTouch(this, ev) || super.onInterceptTouchEvent(ev)
    }
}