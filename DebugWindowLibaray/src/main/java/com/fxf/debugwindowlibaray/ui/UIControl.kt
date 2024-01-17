package com.fxf.debugwindowlibaray.ui

import android.app.Activity
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.provider.Settings
import android.view.Gravity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewManager
import android.view.WindowManager
import com.fxf.debugwindowlibaray.databinding.LayoutViewDebugUiControlBinding
import com.fxf.debugwindowlibaray.databinding.ViewDebugLayoutMainContentBinding
import com.fxf.debugwindowlibaray.ui.manager.ViewManagerExt
import java.lang.ref.WeakReference
import java.util.logging.Logger

/**
 * ui 控制
 */
class UIControl(private val ctx: Context) {
    // 顶部控制区域
    private val uiControlBinding by lazy {
        val binding = LayoutViewDebugUiControlBinding.inflate(LayoutInflater.from(ctx))
        TouchDragWrapper(binding.root, binding.root)
        binding
    }

    // 内容区域
    private val contentBinding by lazy {
        ViewDebugLayoutMainContentBinding.inflate(LayoutInflater.from(ctx))
    }
    private val pages = ArrayList<UIPage>()

    private var hostActivity: WeakReference<Activity>? = null

    private var config: UiControlConfig = UiControlConfig()

    private lateinit var viewManager: ViewManagerExt

    // 是否显示
    var isShown = false
        private set

    /**
     * 切换显示的区域
     */
    fun switchViewManager(viewManager: ViewManagerExt) {
        if (this::viewManager.isInitialized) {
            try {
                this.viewManager.removeView(uiControlBinding.root)
                this.viewManager.removeView(contentBinding.root)
                viewManager.addView(uiControlBinding.root, uiControlBinding.root.layoutParams ?: createUiControlLayoutParams())
                viewManager.addView(contentBinding.root, contentBinding.root.layoutParams ?: createContentLayoutParams())
            } catch (_: IllegalArgumentException) {
            }

        }
        this.viewManager = viewManager
    }

    private fun createContentLayoutParams(): WindowManager.LayoutParams {
        val lp = getLayoutParams()
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        lp.flags = lp.flags or WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        return lp
    }

    private fun createUiControlLayoutParams(): WindowManager.LayoutParams {
        val lp = getLayoutParams()
        lp.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
        tryUpdate(lp)
        return lp
    }

    fun show() {
        // if (!hasOverlayPermission(ctx)) return
        if (isShown) return
        // 添加内容区域
        val contentLp = contentBinding.root.layoutParams ?: createContentLayoutParams()
        viewManager.addView(contentBinding.root, contentLp)
        // 添加控制栏
        val lp = uiControlBinding.root.layoutParams ?: createUiControlLayoutParams()
        tryUpdate(lp)
        viewManager.addView(uiControlBinding.root, lp)
        isShown = true
    }

    fun close() {
        if (!isShown) return
        viewManager.removeView(uiControlBinding.root)
        viewManager.removeView(contentBinding.root)
        isShown = false
    }

    fun onActivityChange(hostActivity: WeakReference<Activity>) {
        if (this.hostActivity?.get() != hostActivity.get()) {
            this.hostActivity = hostActivity
            pages.forEach { it.onHostActivityChange(hostActivity) }
        }
    }

    /**
     * 加载功能页
     */
    fun loadPage(page: UIPage, index: Int = pages.size) {
        if (pages.contains(page)) return
        val tabView = page.createTabView(ctx, uiControlBinding.layoutControlBar)
        tabView.setOnClickListener {
            switchPage(page)
        }
        hostActivity?.let {
            page.onHostActivityChange(it)
        }
        uiControlBinding.layoutControlBar.addView(tabView, 0)
        pages.add(index, page)
    }
    fun getAllPage(): List<UIPage> = pages

    fun switchPage(index: Int) {
        if (pages.isEmpty()) return
        val realIndex = if (index < 0) {
            0
        } else if (index > pages.size) {
            pages.size - 1
        } else {
            index
        }
        switchPage(pages[realIndex])
    }

    /**
     * 切换为当前显示的page并隐藏其他page
     * @param delegate 需要显示的page，必须是已经加载的page
     */
    fun switchPage(delegate: UIPage) {
        // api 23后需要动态申请，之前默认开启
        // if (!hasOverlayPermission(ctx)) return

        if (!pages.contains(delegate)) throw IllegalStateException("page not load")
        if (!delegate.isOnShow) {
            contentBinding.layoutContent.addView(delegate.createContentView(ctx))
            delegate.onShow()
            pages.forEach {
                // 是否考虑多个共同显示
                if (it.isOnShow && it != delegate) {
                    contentBinding.layoutContent.removeView(it.createContentView(ctx))
                    it.onClose()
                }
            }
            val lp = contentBinding.root.layoutParams
            // 根据页面配置焦点和触摸状态
            viewManager.makeTouchable(delegate.enableTouch(), lp)
            viewManager.makeFocusable(delegate.enableFocus(), lp)
            viewManager.updateViewLayout(contentBinding.root, lp)
            // 二次设置焦点和触摸状态
            contentBinding.root.makeTouchable(delegate.enableTouch())
            contentBinding.root.makeFocusable(delegate.enableFocus())
        }
    }

    fun hasOverlayPermission(ctx: Context): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.canDrawOverlays(ctx)) {
                // Logger.e("UIControl", "no overlay permission")
                return false
            }
        }
        return true
    }

    fun removePage(p: UIPage) {
        if (!pages.contains(p)) return
        uiControlBinding.layoutControlBar.removeView(p.tabView)
        if (p.isOnShow) {
            contentBinding.layoutContent.removeView(p.contentView)
            p.onClose()
        }
        p.onDestroy()
        pages.remove(p)
    }

    private fun getLayoutParams(): WindowManager.LayoutParams {
        val lp = WindowManager.LayoutParams()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lp.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            lp.type = WindowManager.LayoutParams.TYPE_PHONE
        }
        lp.flags =
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        lp.format = PixelFormat.TRANSLUCENT
        lp.gravity = Gravity.END or Gravity.TOP
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        return lp
    }

    /**
     * 更新看控制栏位置
     */
    fun updatePosition(uiControlConfig: UiControlConfig) {
        this.config = uiControlConfig
        tryUpdate(uiControlBinding.root.layoutParams)
    }

    private fun tryUpdate(lp: ViewGroup.LayoutParams?) {
        if (lp !is WindowManager.LayoutParams) return
        lp.gravity = config.gravity
        lp.x = config.offsetX
        lp.y = config.offsetY
        if (hasOverlayPermission(ctx)) {
            try {
                viewManager.updateViewLayout(uiControlBinding.root, lp)
            } catch (_: Throwable) {

            }
        }
    }

    /**
     * 销毁
     */
    fun destroy() {
        pages.forEach {
            removePage(it)
        }
        close()
    }
}
