package com.fxf.debugwindowlibaray.util

import android.content.res.Resources
import android.os.Build
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.fxf.debugwindowlibaray.R

internal fun View.setSize(width: Int? = null, height: Int? = null) {
    val lp = layoutParams ?: ViewGroup.LayoutParams(
        ViewGroup.LayoutParams.WRAP_CONTENT,
        ViewGroup.LayoutParams.WRAP_CONTENT,
    )
    lp.width = width ?: lp.width
    lp.height = height ?: lp.height
    layoutParams = lp
}

fun View.enablePress() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        foreground =
            AppCompatResources.getDrawable(context, R.drawable.view_debug_common_press_foreground)
    }
}

fun View.enableSelect() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        foreground =
            AppCompatResources.getDrawable(context, R.drawable.view_debug_common_selected_foreground)
    }
    /*val d = StateListDrawable()
    val pd = AppCompatResources.getDrawable(context, drawableId)!!.mutate()
    pd.setTint(resources.getColor(R.color.view_debug_common_selected_color))
    d.addState(intArrayOf(android.R.attr.state_pressed), pd)
    val nd = AppCompatResources.getDrawable(context, drawableId)!!.mutate()
    d.addState(intArrayOf(), nd)
    background = d*/
}



internal fun adjustOrientation(rootView: View) {
    val ctx = rootView.context
    if (ctx.resources.displayMetrics.widthPixels > ctx.resources.displayMetrics.heightPixels) {
        rootView.setSize(width = (ctx.resources.displayMetrics.widthPixels * 0.7).toInt())
        // rootView.minimumWidth = (ctx.resources.displayMetrics.widthPixels * 0.77f).toInt()
    } else {
        rootView.updateLayoutParams<ConstraintLayout.LayoutParams> {
            matchConstraintMinWidth = (ctx.resources.displayMetrics.widthPixels * 0.8f).toInt()
        }
        rootView.minimumWidth = (ctx.resources.displayMetrics.widthPixels * 0.8f).toInt()
    }
}
