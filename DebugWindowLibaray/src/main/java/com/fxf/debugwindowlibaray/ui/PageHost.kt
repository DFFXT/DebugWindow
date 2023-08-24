package com.fxf.debugwindowlibaray.ui

import android.view.View

/**
 * 主页面
 */
interface PageHost {
    fun showDialog(dialog: View, dismissCallback: Runnable? = null)

    fun closeDialog(dialog: View)
}
