package com.fxf.debugwindowlibaray.ui.pages.log

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.viewdebug.ui.dialog.BaseDialog
import com.fxf.debugwindowlibaray.DebugWindowInitializer
import com.fxf.debugwindowlibaray.databinding.DwLogHistoryDialogBinding
import com.fxf.debugwindowlibaray.databinding.DwLogHistoryItemBinding
import com.fxf.debugwindowlibaray.ui.UIPage
import java.io.File
import java.io.FileOutputStream
import java.util.LinkedList

internal class LogHistoryDialog(uiPage: UIPage) : BaseDialog(uiPage) {

    private lateinit var callback : (item: String)->Unit
    private inner class VH(val binding: DwLogHistoryItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: String) {
            binding.root.text = item
            binding.root.setOnClickListener {
                callback.invoke(item)
                saveHistoryItem(item)
            }
        }
    }

    override fun onCreateDialog(ctx: Context, parent: ViewGroup): View {
        val binding = DwLogHistoryDialogBinding.inflate(LayoutInflater.from(ctx), parent, false)
        val adapter = object : RecyclerView.Adapter<VH>() {

            override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
                return VH(
                    DwLogHistoryItemBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
            }

            override fun getItemCount(): Int = getHistory().size

            override fun onBindViewHolder(holder: VH, position: Int) {
                holder.bind(getHistory()[position])
            }
        }
        binding.rvLog.adapter = adapter
        return binding.root
    }

    fun show(callback:(item: String)->Unit) {

    }


    companion object {
        private val history by lazy {
            val log = File(DebugWindowInitializer.application.cacheDir, logFileName).readLines()
            val list = LinkedList<String>()
            list.addAll(log)
            list
        }

        private val logFileName = "dw_log_history.txt"
        fun saveHistoryItem(string: String) {
            val index = history.indexOf(string)
            if (index == 0) return
            if (index > 0) {
                history.removeAt(index)
            }
            history.add(0, string)
            val historyFile = File(DebugWindowInitializer.application.cacheDir, logFileName)
            val outputStream = FileOutputStream(historyFile)
            history.forEachIndexed { index, s ->
                outputStream.write(s.toByteArray())
                if (index < history.size - 1) {
                    outputStream.write("\n".toByteArray())
                }
            }
        }

        fun getHistory(): List<String> = history
    }
}