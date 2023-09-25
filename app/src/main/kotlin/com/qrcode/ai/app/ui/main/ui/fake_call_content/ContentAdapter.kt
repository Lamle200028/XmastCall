package com.qrcode.ai.app.ui.main.ui.fake_call_content

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.LayoutItemContentBinding
import com.qrcode.ai.app.utils.api.FakeVideoData

class ContentAdapter(private var items: ArrayList<FakeVideoData>, val listener: OnClickItemListener) :
    RecyclerView.Adapter<ContentAdapter.ContentViewHolder>() {
    var mCheckPosition: Int = 0

    interface OnClickItemListener {
        fun onClickItem(item: FakeVideoData)
    }

    inner class ContentViewHolder(private var binding: LayoutItemContentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("NotifyDataSetChanged")
        fun bind(item: FakeVideoData, position: Int) {
            binding.content.text = item.name

            binding.root.setOnClickListener {
                listener.onClickItem(item)
                mCheckPosition = position
                notifyDataSetChanged()
            }

            if (position == mCheckPosition) {
                binding.checkbox.setImageResource(R.drawable.ic_ratio_content_check)
            } else {
                binding.checkbox.setImageResource(R.drawable.ic_ratio_content_uncheck)
            }

        }


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContentViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val bindingItem: LayoutItemContentBinding =
            DataBindingUtil.inflate(inflater, R.layout.layout_item_content, parent, false)

        return ContentViewHolder(bindingItem)
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ContentViewHolder, position: Int) {
        val item: FakeVideoData = items[position]
        holder.bind(item = item, position)
    }
}