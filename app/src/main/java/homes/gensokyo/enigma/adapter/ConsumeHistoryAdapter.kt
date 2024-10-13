package homes.gensokyo.enigma.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import homes.gensokyo.enigma.bean.memberflowbean
import homes.gensokyo.enigma.databinding.ItemNotificationBinding

class ConsumeHistoryAdapter(

    private var dataList: List<memberflowbean.Data>
) : RecyclerView.Adapter<ConsumeHistoryHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ConsumeHistoryHolder {

        val binding = ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ConsumeHistoryHolder(binding)
    }

    override fun getItemCount(): Int = dataList.size


    override fun onBindViewHolder(holder: ConsumeHistoryHolder, position: Int) {
        val memberFlow = dataList[position]
        holder.bind(memberFlow,position)
    }
    fun updateData(newDataList: List<memberflowbean.Data>) {
        dataList = newDataList
        notifyDataSetChanged()
    }
}

class ConsumeHistoryHolder(
    private val binding: ItemNotificationBinding
) : RecyclerView.ViewHolder(binding.root) {

    fun bind(memberFlow: memberflowbean.Data,position: Int) {

        binding.tvAmount.text = "消费：${memberFlow.amount.toString()}  \n 余额：${memberFlow.balance.toString()}"
        binding.tvPlace.text ="${memberFlow.deviceName} "
        binding.tvTime.text = memberFlow.consumeTime
        binding.executePendingBindings()
        binding.root.setOnClickListener {
            //TODO 加上json'页
        }
    }
}
