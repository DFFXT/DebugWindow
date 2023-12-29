package com.fxf.debugwindowlibaray.ui.pages

import android.content.Context
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.RecyclerView
import com.fxf.debugwindowlibaray.R
import com.fxf.debugwindowlibaray.databinding.ItemLogBinding
import com.fxf.debugwindowlibaray.databinding.LayoutLogPageBinding
import com.fxf.debugwindowlibaray.ui.UIPage
import java.io.BufferedReader
import java.io.InputStreamReader
import kotlin.concurrent.thread

/**
 * 功能
 * 1. 显示日志
 * 2. 过滤日志，支持正则
 * 3. 支持显示当前进程日志和设备日志
 * 4. 支持设置显示日志条数
 */
class LogPage : UIPage() {

    companion object{
        private const val MAX_LOG_SIZE = 10000
    }
    private lateinit var binding: LayoutLogPageBinding
    private val logList = ArrayList<LogItem>()
    private val originLog = ArrayList<LogItem>()
    private var logcatEnable = true

    private val tagSet by lazy {
        ctx.resources.getStringArray(R.array.log_level).toSet()
    }
    private val unknownLevel = 0
    private val levelMap = HashMap<Char, Int>().apply {
        this['V'] = Log.VERBOSE
        this['D'] = Log.DEBUG
        this['I'] = Log.INFO
        this['W'] = Log.WARN
        this['E'] = Log.ERROR
    }

    private var filter: CharSequence? = null

    private var filterLevel: Int = Log.VERBOSE

    class VH(val binding: ItemLogBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: LogItem) {
            binding.root.text = item.text
        }
    }

    private val adapter = object : RecyclerView.Adapter<VH>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            return VH(ItemLogBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }

        override fun getItemCount(): Int = logList.size

        override fun onBindViewHolder(holder: VH, position: Int) {
            holder.bind(logList[position])
        }
    }

    override fun onCreateContentView(ctx: Context, parent: ViewGroup): View {
        binding = LayoutLogPageBinding.inflate(LayoutInflater.from(ctx), parent, false)
        binding.spinnerTag.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val filter = ctx.resources.getStringArray(R.array.log_level)[p2]
                setFilter(filterLevel = levelMap[filter.first()]!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

        }
        binding.rvList.adapter = adapter
        binding.edtFilter.setOnKeyListener { _, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                setFilter(filter = binding.edtFilter.text)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        startLogcat()
        return binding.root
    }

    override fun getTabIcon(): Int {
        return R.color.debug_window_white
    }

    override fun enableFocus(): Boolean = true

    private fun getTagLevel(line: String): Int {
        var index = 0
        while (index < line.length) {
            Log.VERBOSE
            if (tagSet.contains(line[index].toString())) {
                return levelMap[line[index]]!!
            }
            index++
        }
        return unknownLevel
    }

    private fun startLogcat() {
        thread {
            val process = Runtime.getRuntime().exec("logcat")
            val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))
            while (!isDestroy && logcatEnable) {
                val line = bufferedReader.readLine()
                // 02-02 01:44:09.514 E
                // 12-29 08:23:37.585 29360 29406 I
                binding.root.post {
                    appendText(getTagLevel(line), line)
                }
            }
        }
    }

    private fun stopLogcat() {
        logcatEnable = false
    }

    fun appendText(level: Int, text: String) {
        val item = LogItem(level, text)
        if (originLog.size > MAX_LOG_SIZE) {
            originLog.clear()
        }
        originLog.add(item)
        if (canShow(level, text)) {
            logList.add(item)
            adapter.notifyItemInserted(logList.size - 1)
        }
    }

    private fun hideKeyboard(view: View) {

    }

    private fun canShow(level: Int, text: String): Boolean {
        if (level >= filterLevel) {
            if (filter == null) return true
            return text.contains(filter!!)
        }
        return false
    }
    private fun setFilter(filterLevel: Int = this.filterLevel, filter: CharSequence? = this.filter) {
        this.filter = filter
        this.filterLevel = filterLevel
        logList.clear()
        originLog.forEach {
            if (canShow(it.level, it.text)) {
                logList.add(it)
            }
        }
        binding.root.post {
            adapter.notifyDataSetChanged()
        }
    }

    class LogItem(val level: Int, val text: String)
}
