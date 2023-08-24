package com.fxf.debugwindowlibaray.ui

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.View
import android.view.ViewGroup
import androidx.core.view.updateLayoutParams
import com.fxf.debugwindowlibaray.R
import com.fxf.debugwindowlibaray.util.enablePress

/**
 * 空白页面
 */
class EmptyPage : UIPage() {

    override fun enableTouch(): Boolean = false

    override fun enableFocus(): Boolean = false

    override fun getTabIcon(): Int = R.mipmap.view_debug_common_close

    override fun onCreateContentView(ctx: Context): View {
        return View(ctx).apply {
            layoutParams = ViewGroup.LayoutParams(0 ,0)
        }
    }
}
