package com.qrcode.ai.app.ui.main.ui.setupfakecall

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView.OnItemClickListener
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.Recycler
import com.qrcode.ai.app.R
import com.qrcode.ai.app.databinding.FragmentSetupFakeCallBinding
import com.qrcode.ai.app.databinding.ItemTimeBinding
import okhttp3.internal.notify

class TimeAdapter(private val onItemClickListener: (Time) -> Unit) : RecyclerView.Adapter<TimeAdapter.TimeViewHolder>() {

    private val listItem = arrayListOf<Time>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TimeViewHolder {
        return TimeViewHolder(ItemTimeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(list: List<Time>) {
        this.listItem.clear()
        this.listItem.addAll(list)
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return listItem.size
    }

    override fun onBindViewHolder(holder: TimeViewHolder, position: Int) {
        holder.bind(listItem[position])
    }

    inner class TimeViewHolder(private val binding: ItemTimeBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(time: Time) {
            binding.root.setOnClickListener {
                onItemClickListener.invoke(time)
            }

            binding.tvTime.text = time.toString(binding.root.context)
            binding.divider.visibility = if (adapterPosition == listItem.size - 1) View.GONE else View.VISIBLE
        }
    }
}