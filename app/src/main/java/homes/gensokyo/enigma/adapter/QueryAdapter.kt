package homes.gensokyo.enigma.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import homes.gensokyo.enigma.bean.QueryData
import homes.gensokyo.enigma.bean.School
import homes.gensokyo.enigma.databinding.ItemQueryBinding
import homes.gensokyo.enigma.util.LogUtils

class QueryAdapter(private var logList: List<QueryData>) : RecyclerView.Adapter<QueryAdapter.QueryHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): QueryHolder {
        val binding = ItemQueryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return QueryHolder(binding)
    }
    class QueryHolder(binding: ItemQueryBinding) : RecyclerView.ViewHolder(binding.root){
        val imageView: ImageView = binding.imageView
        val titleText: TextView = binding.titleText
        val timeText: TextView = binding.timeText
    }

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newLogList: List<QueryData>) {
        this.logList = newLogList
        LogUtils.d("TAG", "Updating data in adapter: ${newLogList.size} items")
        notifyDataSetChanged()
    }
    override fun onBindViewHolder(holder: QueryHolder, position: Int) {
        val logItem = logList[position]
        Glide.with(holder.itemView.context)
            .load("http://wx.ivxiaoyuan.com/sc/files/capturePhoto/"+logItem.picture)
            .into(holder.imageView)
        holder.titleText.text = logItem.title
        holder.timeText.text = logItem.publishTime
    }

    override fun getItemCount(): Int = logList.size
}
