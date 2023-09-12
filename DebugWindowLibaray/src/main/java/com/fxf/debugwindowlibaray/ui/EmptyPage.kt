package com.fxf.debugwindowlibaray.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.fxf.debugwindowlibaray.R

/**
 * 空白页面
 */
class EmptyPage : UIPage() {

    override fun enableTouch(): Boolean = false

    override fun enableFocus(): Boolean = false
    override fun onCreateTabView(ctx: Context, parent: ViewGroup): View {
        return super.onCreateTabView(ctx, parent).apply {
            this as ImageView
            imageTintList = ColorStateList.valueOf(Color.WHITE)
        }
    }

    override fun getTabIcon(): Int = R.mipmap.view_debug_common_close

    override fun onCreateContentView(ctx: Context, parent: ViewGroup): View {
        return View(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(0, 0)
        }
    }
}
