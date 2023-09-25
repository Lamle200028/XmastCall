package com.qrcode.ai.app.ui.main.ui.Wallpapers

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.qrcode.ai.app.R
import com.qrcode.ai.app.ui.main.ui.Wallpapers.api.Wallpaper
import kotlinx.android.synthetic.main.item_wallpapers.view.*


class WallAdapter(
    private val onItemClickListener: (Wallpaper) -> Unit
) :
    RecyclerView.Adapter<WallAdapter.ViewHolder>() {

    private val dataset = arrayListOf<Wallpaper>()

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Wallpaper>) {
        dataset.clear()
        dataset.addAll(data)
        notifyDataSetChanged()
    }

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WallAdapter.ViewHolder {
        val adapterLayout = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_wallpapers, parent, false)
        return ViewHolder(adapterLayout)
    }

    override fun onBindViewHolder(holder: WallAdapter.ViewHolder, position: Int) {
        if (position >= dataset.size) return
        val item = dataset[position]
        Glide.with(holder.itemView.context)
            .load(item.url)
            .addListener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    holder.itemView.shimmer_loading.stopShimmer()
                    holder.itemView.shimmer_loading.visibility = View.GONE
                    return false
                }

            })
            .into(holder.imageView)

        holder.itemView.setOnClickListener {
            onItemClickListener(item)
        }
    }

    override fun getItemCount(): Int = if (dataset.size == 0) 12 else dataset.size
}
