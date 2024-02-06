package com.fxf.debugwindowlibaray.ui

import android.os.Build
import android.view.View
import androidx.appcompat.content.res.AppCompatResources
import com.example.viewdebug.util.touch.BaseDragHelper
import com.fxf.debugwindowlibaray.R
import com.fxf.debugwindowlibaray.ui.manager.ViewManagerExt

/**
 * view拖动
 * @param moveTarget 被移动的view
 * @param touchTarget 被触发的view
 * @param viewManager 视图更新管理器
 */
class TouchDragWrapper(private val moveTarget: View, touchTarget: View, private val viewManagerProvider: () -> ViewManagerExt) {
    private val ctx = moveTarget.context

    init {
        object : BaseDragHelper() {
            override fun longClickMove(dx: Float, dy: Float) {
                viewManagerProvider.invoke().move(moveTarget, dx, dy)
            }

            override fun longOnClick(x: Float, y: Float): Boolean {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    moveTarget.foreground = AppCompatResources.getDrawable(ctx, R.drawable.view_debug_common_rect_stroke_1dp)
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
        }.attachToView(touchTarget)
    }
}
