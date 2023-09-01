package com.fxf.debugwindowlibaray

import android.app.Activity
import android.app.Application
import android.os.Bundle
import com.fxf.debugwindowlibaray.ui.EmptyPage
import com.fxf.debugwindowlibaray.ui.UIControl
import com.fxf.debugwindowlibaray.ui.UIPage
import com.fxf.debugwindowlibaray.ui.UiControlConfig
import java.lang.ref.WeakReference
import java.util.LinkedList

/**
 * 视图调试工具
 * 功能：
 * 1. 查看层级信息
 * 2. 查看图片信息
 */
object ViewDebugManager {
    // 是否支持应用外显示
    private var showOnOtherApplication = false
    private val activityLifecycleCallbacks = object : ActivityStackCallback() {
        private val activities = LinkedList<Activity>()
        private var currentActivity: Activity? = null

        override fun onActivityCreated(p0: Activity, p1: Bundle?) {
            super.onActivityCreated(p0, p1)
            activities.add(0, p0)
            uiControl.onActivityChange(WeakReference(p0))
        }

        override fun onActivityResumed(p0: Activity) {
            super.onActivityResumed(p0)
            currentActivity = p0
            uiControl.onActivityChange(WeakReference(p0))
            uiControl.show()
        }

        override fun onActivityDestroyed(p0: Activity) {
            super.onActivityDestroyed(p0)
            activities.remove(p0)
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
    }
    private lateinit var app: Application
    private val uiControl by lazy { UIControl(app) }
    fun init(app: Application) {
        if (this::app.isInitialized) return
        this.app = app
        app.registerActivityLifecycleCallbacks(activityLifecycleCallbacks)
        addPage(EmptyPage())
    }

    fun addPage(page: UIPage) {
        uiControl.loadPage(page)
    }

    fun removePage(page: UIPage) {
        uiControl.removePage(page)
    }

    /*fun destroy() {
        app.unregisterActivityLifecycleCallbacks(activityLifecycleCallbacks)
        // app 持有不用销毁
        uiControl.destroy()
    }*/

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
