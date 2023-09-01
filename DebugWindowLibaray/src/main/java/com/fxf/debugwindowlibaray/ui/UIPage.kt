package com.fxf.debugwindowlibaray.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.annotation.CallSuper
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.updateLayoutParams
import com.example.viewdebug.ui.dialog.BaseDialog
import com.fxf.debugwindowlibaray.R
import com.fxf.debugwindowlibaray.util.enablePress
import com.fxf.debugwindowlibaray.util.enableSelect
import java.lang.ref.WeakReference

abstract class UIPage {
    var isOnShow = false
        private set

    protected lateinit var tabView: View
    protected lateinit var contentView: ViewGroup
    val ctx by lazy { tabView.context }

    // 当前activity
    protected var hostActivity: WeakReference<Activity>? = null
    protected fun createTabView(ctx: Context, parent: ViewGroup): View {
        if (!this::tabView.isInitialized) {
            tabView = onCreateTabView(ctx, parent)
        }
        return tabView
    }

    protected open fun createContentView(ctx: Context): View {
        if (!this::contentView.isInitialized) {
            contentView = ConstraintLayout(ctx)
            contentView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT,
            )
            contentView.addView(onCreateContentView(ctx, contentView))
        }
        return contentView
    }

    @SuppressLint("ClickableViewAccessibility")
    fun showDialog(dialog: BaseDialog, dismissCallback: Runnable? = null) {
        val dialogView = dialog.dialogView
        if (dialogView.parent != null) return
        val container = dialog.parent
        container.layoutParams = ViewGroup.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT,
        )

        container.addView(dialogView)
        val lp = dialogView.layoutParams as? ConstraintLayout.LayoutParams
        if (lp != null) {
            lp.startToStart = ConstraintLayout.LayoutParams.PARENT_ID
            lp.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
            lp.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID
            lp.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
            dialogView.layoutParams = lp
        }
        contentView.addView(container)
        if (dialog.clickClose()) {
            container.setOnTouchListener { _, event ->
                if (event.action == MotionEvent.ACTION_UP) {
                    dismissCallback?.run()
                    closeDialog(dialog)
                }
                return@setOnTouchListener true
            }
        } else {
            container.isClickable = true
            container.isFocusable = true
        }
        val b = dialog.background()
        if (b != null) {
            container.background = b
        }
    }

    fun closeDialog(dialog: BaseDialog) {
        if (dialog.dialogView.parent != null) {
            dialog.onClose()
            val p = dialog.dialogView.parent as ViewGroup
            contentView.removeView(p)
            p.removeView(dialog.dialogView)
        }
    }

    /**
     * 当前activity发生变更
     */
    open fun onHostActivityChange(hostActivity: WeakReference<Activity>) {
        this.hostActivity = hostActivity
    }

    open fun enableTouch(): Boolean = true
    open fun enableFocus(): Boolean = false
    open fun onCreateTabView(ctx: Context, parent: ViewGroup): View {
        return AppCompatImageView(ctx).apply {
            val size =
                ctx.resources.getDimensionPixelSize(R.dimen.view_debug_control_ui_status_bar_height)
            layoutParams = ViewGroup.MarginLayoutParams(size, size)
            updateLayoutParams<ViewGroup.MarginLayoutParams> {
                marginStart = ctx.resources.getDimensionPixelOffset(R.dimen.view_debug_ui_control_button_margin)
                marginEnd = ctx.resources.getDimensionPixelOffset(R.dimen.view_debug_ui_control_button_margin)
            }
            setImageResource(getTabIcon())
            backgroundTintList = ColorStateList.valueOf(Color.WHITE)
            enablePress()
            enableSelect()
        }
    }

    abstract fun onCreateContentView(ctx: Context, parent: ViewGroup): View

    // 获取tab图片样式
    abstract fun getTabIcon(): Int

    @CallSuper
    open fun onShow() {
        tabView.isSelected = true
        isOnShow = true
    }

    @CallSuper
    open fun onClose() {
        tabView.isSelected = false
        isOnShow = false
    }

    @CallSuper
    open fun onDestroy() {
    }
}
