package com.qrcode.ai.app.ui.main.ui.messager

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.qrcode.ai.app.databinding.ItemMessageMeBinding
import com.qrcode.ai.app.databinding.ItemMessageYouBinding
import com.qrcode.ai.app.platform.BaseHolder
import com.qrcode.ai.app.utils.TimeUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class ViewType(val type: Int) {
    TYPE_ME(1),
    TYPE_BOT(2)
}

class FakeMessagerAdapter(private val context: Context) :
    ListAdapter<ItemFakeMessager, RecyclerView.ViewHolder>(MyDiffUtil) {
    var listMessager: ArrayList<ItemFakeMessager> = ArrayList()

    companion object {
        private val MyDiffUtil =
            object : DiffUtil.ItemCallback<ItemFakeMessager>() {
                override fun areItemsTheSame(
                    oldItem: ItemFakeMessager,
                    newItem: ItemFakeMessager
                ): Boolean {
                    return oldItem == newItem
                }

                override fun areContentsTheSame(
                    oldItem: ItemFakeMessager,
                    newItem: ItemFakeMessager
                ): Boolean {
                    return oldItem.id == newItem.id
                }
            }
    }
    internal inner class MessagerMeViewHolder(var binding: ItemMessageMeBinding) :
        BaseHolder<ItemMessageMeBinding>(binding)

    internal inner class MessagerYouViewHolder(var binding: ItemMessageYouBinding) :
        BaseHolder<ItemMessageYouBinding>(binding)

    @SuppressLint("NotifyDataSetChanged")
    fun addMessage(item: ItemFakeMessager) {
        listMessager.add(item)
        this.notifyItemInserted(listMessager.size - 1)
    }

    fun setData(data: ArrayList<ItemFakeMessager>) {
        listMessager = data
    }
    fun scrollToLastMessage(recyclerView: RecyclerView) {
        recyclerView.scrollToPosition(listMessager.size - 1)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = when (viewType) {
            ViewType.TYPE_ME.type -> MessagerMeViewHolder(
                ItemMessageMeBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )

            else -> MessagerYouViewHolder(
                ItemMessageYouBinding.inflate(
                    LayoutInflater.from(context),
                    parent,
                    false
                )
            )
        }
        return holder
    }

    override fun getItemViewType(position: Int): Int {
        return if (listMessager[position].isMe == true) {
            ViewType.TYPE_ME.type
        } else {
            ViewType.TYPE_BOT.type
        }
    }

    override fun getItemCount(): Int = listMessager.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val message = listMessager[position]

        if (holder is MessagerMeViewHolder) {
            holder.binding.txtMessagerMe.text = message.messager

        }
        if (holder is MessagerYouViewHolder) {
            holder.binding.txtMessagerYou.text = message.messager
            holder.binding.txtTime.text = message.time
            holder.binding.ctnTime.visibility = message.isTime
            holder.binding.avatar.visibility = message.isAvatar

        }
    }
}
