package com.fxf.debugwindowlibaray

import android.app.Activity
import android.app.Application.ActivityLifecycleCallbacks
import android.os.Bundle
import java.lang.ref.WeakReference

/**
 * 应用生命周期监听
 */
internal open class ActivityStackCallback : ActivityLifecycleCallbacks {

    // 当前进程是否在前台
    var processIsFront = false

    // 当前resume状态页面数量
    private var resumeCount = 0
    override fun onActivityCreated(p0: Activity, p1: Bundle?) {
    }

    override fun onActivityStarted(p0: Activity) {
    }

    override fun onActivityResumed(p0: Activity) {
        if (resumeCount == 0) {
            onProcessResume()
        }
        resumeCount++
    }

    override fun onActivityPaused(p0: Activity) {
        resumeCount--
    }

    override fun onActivityStopped(p0: Activity) {
        if (resumeCount == 0) {
            onProcessStop()
        }
    }

    override fun onActivitySaveInstanceState(p0: Activity, p1: Bundle) {
    }

    override fun onActivityDestroyed(p0: Activity) {
    }

    /**
     * 应用前台
     */
    open fun onProcessResume() {
        processIsFront = true
    }

    /**
     * 应用后台
     */
    open fun onProcessStop() {
        processIsFront = false
    }
}
