package com.qrcode.ai.app.ui.main.ui.messager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.qrcode.ai.app.R


data class ItemHintMessager(
    var id: String? = null,
    var hint: String? = null,
)

class HintMessagerAdapter(private val hintList: ArrayList<ItemHintMessager>) :
    RecyclerView.Adapter<HintMessagerAdapter.ViewHolder>() {
    private lateinit var mlistener: OnItemClickListener


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val hintMessagerView = inflater.inflate(R.layout.item_hint_messager, parent, false)
        return ViewHolder(hintMessagerView, mlistener)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item: ItemHintMessager = hintList[position]
        val txtHint = holder.textHint
        txtHint.setText(item.hint)
    }

    class ViewHolder(item: View, listener: OnItemClickListener) : RecyclerView.ViewHolder(item) {
        val textHint: TextView = itemView.findViewById(R.id.txtHint)
        private val container: ConstraintLayout = itemView.findViewById(R.id.container)

        init {
            container.setOnClickListener {
                listener.onItemCLick(adapterPosition, textHint.text.toString())
            }
        }


    }

    override fun getItemCount(): Int {
        return hintList.size
    }

    interface OnItemClickListener {
        fun onItemCLick(position: Int,data : String)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mlistener = listener
    }

}
