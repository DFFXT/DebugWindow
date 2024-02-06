package com.fxf.debugwindowlibaray

import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.view.ViewGroup
import com.fxf.debugwindowlibaray.ui.EmptyPage
import com.fxf.debugwindowlibaray.ui.UIControl
import com.fxf.debugwindowlibaray.ui.UIPage
import com.fxf.debugwindowlibaray.ui.UiControlConfig
import com.fxf.debugwindowlibaray.ui.manager.CommonViewManagerImpl
import com.fxf.debugwindowlibaray.ui.manager.ViewManagerExt
import com.fxf.debugwindowlibaray.ui.manager.WindowViewManagerImpl
import com.fxf.debugwindowlibaray.util.hasOverlayPermission
import java.lang.ref.WeakReference

/**
 * 视图调试工具
 * 功能：
 * 1. 查看层级信息
 * 2. 查看图片信息
 */
class ViewDebugManager {
    // 是否支持应用外显示
    private var showOnOtherApplication = false
    private var switchToCommonMode = false
    private var viewManager: ViewManagerExt? = null
    private val activityLifecycleCallbacks = object : ActivityStackCallback() {

        private var topActivity: WeakReference<Activity>? = null
        override fun onActivityCreated(p0: Activity, p1: Bundle?) {
            super.onActivityCreated(p0, p1)
            uiControl.onActivityChange(WeakReference(p0))
        }

        override fun onActivityResumed(p0: Activity) {
            if (topActivity?.get() != p0) {
                topActivity = WeakReference(p0)
                switchRoot(p0)
            }
            uiControl.onActivityChange(WeakReference(p0))
            uiControl.show()
            super.onActivityResumed(p0)
        }

        override fun onActivityDestroyed(p0: Activity) {
            super.onActivityDestroyed(p0)
        }

        override fun onActivityStopped(p0: Activity) {
            super.onActivityStopped(p0)
        }

        override fun onProcessResume() {
            uiControl.show()
        }

        override fun onProcessStop() {
            if (!showOnOtherApplication) {
                uiControl.close()
            }
        }

        override fun onActivityAllFinish() {
            if (!isWindowManagerMode()) {
                uiControl.close()
                // 设置空，防止内存泄露
                uiControl.switchViewManager(null)
            }
        }

        private fun switchRoot(p0: Activity) {
            // 没有悬浮窗权限则使用普通模式，没有主动设置，使用普通模式
            if (!hasOverlayPermission(p0) && isWindowManagerMode() && switchToCommonMode || viewManager == null) {
                uiControl.switchViewManager(CommonViewManagerImpl(p0.findViewById<ViewGroup>(android.R.id.content)))
            }
        }
    }
    private lateinit var app: Application
    val uiControl: UIControl by lazy {
        UIControl(app)
    }

    /**
     * @param defaultPage 默认页面，为了方便默认是[EmptyPage]
     * @param withoutOverlayPermissionSwitchToCommonMode 如果没有悬浮窗权限，是否切换为普通模式（试图将添加到activity中）
     */
    fun init(app: Application, withoutOverlayPermissionSwitchToCommonMode: Boolean = true, defaultPage: UIPage? = EmptyPage()) {
        if (this::app.isInitialized) return
        this.app = app
        this.switchToCommonMode = withoutOverlayPermissionSwitchToCommonMode
        app.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        if (defaultPage != null) {
            addPage(defaultPage)
        }
        // addPage(LogPage())
    }

    /**
     * 是否是通过的windowManger来显示
     */
    private fun isWindowManagerMode(): Boolean {
        return !hasOverlayPermission(app) && viewManager?.isWindowManger() == true
    }

    /**
     * 设置试图管理器，自定义需要注意内存泄露
     * @param viewManager 试图管理器，比如WindowManager|ViewGroup
     * [WindowViewManagerImpl]
     * [CommonViewManagerImpl]
     */
    fun setRootView(viewManager: ViewManagerExt?) {
        this.viewManager = viewManager
        uiControl.switchViewManager(viewManager)
    }

    fun addPage(page: UIPage, index: Int = uiControl.getAllPage().size) {
        uiControl.loadPage(page, index)
    }

    fun <T : UIPage> getPage(clazz: Class<T>): List<T> {
        return uiControl.getAllPage().filter { clazz.isInstance(it) } as List<T>
    }

    fun switchPage(index: Int) {
        uiControl.switchPage(index)
    }

    fun switchPage(uiPage: UIPage) {
        uiControl.switchPage(uiPage)
    }

    fun removePage(page: UIPage) {
        uiControl.removePage(page)
    }

    fun destroy() {
        app.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
        // app 持有不用销毁
        uiControl.destroy()
    }

    /**
     * 更新控制栏显示图标
     */
    fun updatePosition(uiControlConfig: UiControlConfig) {
        uiControl.updatePosition(uiControlConfig)
    }

    /**
     * 是否支持应用外显示
     */
    fun overOtherApplication(enable: Boolean) {
        this.showOnOtherApplication = enable
        // 更新状态
        if (enable) {
            uiControl.show()
        } else {
            if (activityLifecycleCallbacks.processIsFront) {
                uiControl.show()
            } else {
                uiControl.close()
            }
        }
    }
}
