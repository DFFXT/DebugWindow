package com.fxf.debugwindowlibaray.ui.pages.log

import android.annotation.SuppressLint
import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import androidx.annotation.UiThread
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.fxf.debugwindowlibaray.R
import com.fxf.debugwindowlibaray.databinding.ItemLogBinding
import com.fxf.debugwindowlibaray.databinding.LayoutLogPageBinding
import com.fxf.debugwindowlibaray.ui.UIPage
import java.io.BufferedReader
import java.io.InputStreamReader
import java.text.SimpleDateFormat
import java.util.Locale
import kotlin.concurrent.thread

/**
 * 功能
 * 1. 显示日志
 * 2. 过滤日志，支持正则
 * 3. 支持显示当前进程日志和设备日志
 * 4. 支持设置显示日志条数
 */
open class LogPage : UIPage() {

    companion object {
        var MAX_LOG_SIZE = 10000
        var autoStartLogcat = true
    }

    private lateinit var binding: LayoutLogPageBinding
    private val logList = ArrayList<LogItem>()
    private val originLog = ArrayList<LogItem>()
    private var logcatEnable = true

    private var autoScrollToBottom = true

    private val handler = Handler(Looper.getMainLooper())

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

    private val levelMapToChar = HashMap<Int, Char>().apply {
        this[Log.VERBOSE] = 'V'
        this[Log.DEBUG] = 'D'
        this[Log.INFO] = 'I'
        this[Log.WARN] = 'W'
        this[Log.ERROR] = 'E'
    }
    var dateFormat = SimpleDateFormat("MM-DD'T'HH:mm:ss.SSS", Locale.getDefault())

    private var filter: String? = null

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

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateContentView(ctx: Context, parent: ViewGroup): View {
        binding = LayoutLogPageBinding.inflate(LayoutInflater.from(ctx), parent, false)
        binding.ivClear.setOnClickListener {
            adapter.notifyItemRangeRemoved(0, logList.size)
            logList.clear()
            autoScrollToBottom = true
        }

        binding.spinnerTag.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                val filter = ctx.resources.getStringArray(R.array.log_level)[p2]
                setFilter(filterLevel = levelMap[filter.first()]!!)
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {
            }
        }
        binding.rvList.itemAnimator = null
        binding.rvList.adapter = adapter
        binding.rvList.setOnTouchListener { v, e ->
            autoScrollToBottom = false
            if (e.action and MotionEvent.ACTION_MASK == MotionEvent.ACTION_DOWN) {
                hideKeyboard(v)
            }
            return@setOnTouchListener false
        }
        binding.edtFilter.setOnKeyListener { v, keyCode, _ ->
            if (keyCode == KeyEvent.KEYCODE_ENTER) {
                setFilter(filter = binding.edtFilter.text.toString())
                hideKeyboard(v)
                return@setOnKeyListener true
            }
            return@setOnKeyListener false
        }
        binding.ivScrollToBottom.setOnClickListener {
            autoScrollToBottom = true
            if (logList.isNotEmpty()) {
                binding.rvList.scrollToPosition(logList.size - 1)
            }
        }
        binding.ivHideKeyboard.setOnClickListener {
            hideKeyboard(it)
        }
        binding.ivInputClear.setOnClickListener {
            binding.edtFilter.text = null
            setFilter(filter = null)
        }
        binding.ivInputHistory.setOnClickListener {
            val dialog = LogHistoryDialog(this)
            dialog.show {
                dialog.close()
                binding.edtFilter.setText(it)
                setFilter(filter = it)
                hideKeyboard(binding.ivHideKeyboard)
            }
        }
        if (autoStartLogcat) {
            startLogcat()
        }
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
                val line = bufferedReader.readLine() ?: continue
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
        val date = dateFormat.format(System.currentTimeMillis())
        handler.post {
            appendInUiThread(level, "$date $text")
        }
    }

    @UiThread
    private fun appendInUiThread(level: Int, text: String) {
        if (logList.size > MAX_LOG_SIZE) {
            logList.clear()
            adapter.notifyDataSetChanged()
        }
        // originLog.add(item)
        if (canShow(level, text)) {
            val item = LogItem(level, levelMapToChar[level].toString() + " " + text)
            logList.add(item)
            if (::binding.isInitialized) {
                val lm = binding.rvList.layoutManager as LinearLayoutManager
                if (autoScrollToBottom) {
                    binding.rvList.scrollToPosition(logList.size - 1)
                }
            }
            adapter.notifyItemInserted(logList.size - 1)
        }
    }

    private fun hideKeyboard(view: View) {
        view.post {
            val manager = view.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            manager.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        }
    }

    private fun canShow(level: Int, text: String): Boolean {
        if (level >= filterLevel) {
            if (filter.isNullOrEmpty()) return true
            return text.contains(filter!!)
        }
        return false
    }

    private fun setFilter(filterLevel: Int = this.filterLevel, filter: String? = this.filter) {
        if (filter != this.filter && !filter.isNullOrEmpty()) {
            LogHistoryDialog.saveHistoryItem(filter.toString())
        }
        binding.root.post {
            this.filter = filter
            this.filterLevel = filterLevel
            logList.clear()
            originLog.forEach {
                if (canShow(it.level, it.text)) {
                    logList.add(it)
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    class LogItem(val level: Int, val text: String)
}
