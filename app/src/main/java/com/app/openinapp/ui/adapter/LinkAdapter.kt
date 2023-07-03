package com.app.openinapp.ui.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.app.openinapp.data.model.Link
import com.app.openinapp.databinding.LinkCardItemBinding
import java.text.SimpleDateFormat
import java.util.Locale

class LinkAdapter : RecyclerView.Adapter<LinkAdapter.ViewHolder>() {
    private var dataList: List<Link> = emptyList()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(dataList: List<Link>) {
        this.dataList = dataList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = LinkCardItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currentItem = dataList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int {
        return minOf(4, dataList.size)
    }

    inner class ViewHolder(private val binding: LinkCardItemBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: Link) {
            binding.linkName.text = item.title
            binding.linkClicks.text = item.total_clicks.toString()
            binding.link.text = item.smart_link
            binding.dateAdded.text = formatDate(item.created_at)
            binding.copyLink.setOnClickListener {
                val clipboard = it.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                val textToCopy = item.smart_link

                val clip = ClipData.newPlainText("Copied Text", textToCopy)
                clipboard.setPrimaryClip(clip)

                Toast.makeText(it.context, "Link copied to clipboard", Toast.LENGTH_SHORT).show()
            }

        }

        private fun formatDate(dateString: String): String {
            val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val outputFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())

            val date = inputFormat.parse(dateString)
            return outputFormat.format(date!!)
        }
    }

}
