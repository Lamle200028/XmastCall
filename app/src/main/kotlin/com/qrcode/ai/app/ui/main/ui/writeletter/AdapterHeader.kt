import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.qrcode.ai.app.R

class AdapterHeader(private val data: ArrayList<Int>) :
    RecyclerView.Adapter<AdapterHeader.ViewHolder>() {

    private var onClickItem: ((Int) -> Unit)? = null
    private var selectedItem = -1
    private var isCreate = false

    fun setOnClickItemHeader(onClick: (Int) -> Unit) {
        onClickItem = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.itemheaderwrite, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = data[position]
        holder.view.setImageResource(item)
        if (selectedItem == position) {
            holder.boder.visibility = View.VISIBLE
            holder.check.visibility = View.VISIBLE
        } else {
            holder.boder.visibility = View.GONE
            holder.check.visibility = View.GONE
        }
        if (position == 0 && !isCreate){
            holder.boder.visibility = View.VISIBLE
            holder.check.visibility = View.VISIBLE
            isCreate = true
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {
        val view: ImageView = itemView.findViewById(R.id.img_header1)
        val boder: ImageView = itemView.findViewById(R.id.img_boder)
        val check: ImageView = itemView.findViewById(R.id.icon_check)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(view: View) {
            val position = adapterPosition
            selectedItem = position
            onClickItem?.invoke(data[position])
            notifyDataSetChanged()
        }
    }
}