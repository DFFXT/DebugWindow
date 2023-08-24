package com.fxf.debugwindowlibaray.util.touch

import android.content.Context
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration

/**
 * 判断Touch的方向
 * Created by home on 2018/2/15.
 */
abstract class TouchHelper : View.OnTouchListener {
    private var id = 0 //***点击的id
    private var preX = 0f
    private var preY = 0f //**点击位置
    private var movePreX = 0f
    private var movePreY = 0f
    private var up = false
    private var longClicked = false //**这次点下的长点击是否有效
    private var moved = false
    private var startMove = false //****开始滑动，提高距离限制，后面滑动就不进行限制
    private var startLongClickMove = false
    private var longClickConsume = false
    private fun getMoveMinGap(context: Context): Int {
        return ViewConfiguration.get(context).scaledTouchSlop
    }

    override fun onTouch(v: View, event: MotionEvent): Boolean {
        onTouch(event)
        val startMoveMinGap = getMoveMinGap(v.context)
        if (event.action == MotionEvent.ACTION_DOWN) {
            down(event)
            startMove = false
            up = false
            id++
            longClicked = false
            moved = false
            preX = event.rawX
            movePreX = preX
            preY = event.rawY
            movePreY = preY
            val tmpId = id
            v.postDelayed({
                if (tmpId == id) {
                    if (!up && !longClicked && !moved) {
                        longClicked = true
                        longClickConsume = longOnClick(preX, preY)
                    }
                }
            }, 520)
            return false
        } else if (event.action == MotionEvent.ACTION_MOVE) {
            //**开始滑动跳高距离限制
            // Logger.INSTANCE.d(TAG, startMove.toString() + " " + longClickConsume + " " + (Math.abs(preX - event.rawX) > Math.abs(preY - event.rawY) * 0.5))
            if (!startMove && !longClickConsume && Math.abs(preX - event.rawX) > Math.abs(preY - event.rawY) * 0.5) {
                if (Math.abs(preX - event.rawX) >= startMoveMinGap) {
                    touchDirection(event.rawX - movePreX)
                    moved = true
                    startMove = true
                }
            } else if (startMove && !longClickConsume) { //进行滑动即时响应
                touchDirection(event.rawX - movePreX)
                moved = true
            }
            val abs = Math.abs(preX - event.rawX) + Math.abs(preY - event.rawY)
            if (abs > 20) {
                moved = true
            }
            // Logger.INSTANCE.d(TAG, "$startLongClickMove $longClicked $abs $startMoveMinGap")
            if (!startLongClickMove && longClicked && abs >= startMoveMinGap) { //**长点击有效，可以拖动图标
                moved = true
                startLongClickMove = true
                longClickMove(event.rawX - movePreX, event.rawY - movePreY)
            } else if (longClicked && startLongClickMove) {
                longClickMove(event.rawX - movePreX, event.rawY - movePreY)
            }
            movePreX = event.rawX
            movePreY = event.rawY
            return true
        } else if (event.action == MotionEvent.ACTION_UP) {
            startLongClickMove = false
            startMove = false
            up = true
            if (Math.abs(preX - event.rawX) + Math.abs(preY - event.rawY) < startMoveMinGap) {
                if (!longClicked && !moved) {
                    onClick(preX, preY)
                }
            }
            if (longClicked) { //**长点击有效,点击接收时【显示选项】
                if (!moved) longClickUpNoMove(event.rawX, event.rawY) else longClickUpMoved(event.rawX, event.rawY)
            }
            longClickConsume = false
        }
        return false
    }

    /**
     * attach到某个view
     *
     * @param target 待结合的view
     */
    fun attachToView(target: View) {
        target.setOnTouchListener(this)
    }

    /**
     * 水平滑动
     *
     * @param distance 滑动距离
     */
    abstract fun touchDirection(distance: Float)

    /**
     * 点击事件
     *
     * @param x x
     * @param y y
     */
    fun onClick(x: Float, y: Float) {}

    /**
     * 长点击
     *
     * @param x x
     * @param y y
     */
    abstract fun longOnClick(x: Float, y: Float): Boolean

    /**
     * 长点击移动
     *
     * @param dx dx
     * @param dy dy
     */
    abstract fun longClickMove(dx: Float, dy: Float)

    /**
     * 长点击结束
     *
     * @param x x
     * @param y y
     */
    abstract fun longClickUpNoMove(x: Float, y: Float)
    abstract fun longClickUpMoved(x: Float, y: Float)
    abstract fun down(event: MotionEvent?)
    fun onTouch(event: MotionEvent?) {}

    companion object {
        private const val TAG = "TouchHelper"
    }
}