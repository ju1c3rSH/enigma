package homes.gensokyo.enigma.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import homes.gensokyo.enigma.bean.School

import homes.gensokyo.enigma.databinding.ItemSchoolSelectBinding
import homes.gensokyo.enigma.ui.oobe.SettingViewModel

class SchoolAdapter(private val vm: SettingViewModel, private var dataList: List<School>) :
    RecyclerView.Adapter<SchoolViewHolder>(){
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolViewHolder {
        val binding = ItemSchoolSelectBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return SchoolViewHolder(binding,vm)
    }

    override fun onBindViewHolder(holder: SchoolViewHolder, position: Int) {
        val school = dataList[position]
        Log.d("SchoolAdapter", "Binding school name: ${school.schoolName}")
        holder.bind(school)
        holder.schoolNameTV.setOnClickListener {
            Log.d("SchoolAdapter", "Clicked school name: ${school.schoolName}")
            (vm.onSchoolSelected(school))
        }

    }

    override fun getItemCount(): Int = dataList.size
    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newSchoolList: List<School>) {
        this.dataList = newSchoolList
        Log.d("TAG", "Updating data in adapter: ${newSchoolList.size} items")
        notifyDataSetChanged()
    }
}

class SchoolViewHolder(private val binding: ItemSchoolSelectBinding,private val vm: SettingViewModel) : RecyclerView.ViewHolder(binding.root) {
    fun bind(school: School) {
        binding.school = school
        binding.executePendingBindings()
        binding.tvSchoolName.text = school.schoolName
        /*
        binding.root.setOnClickListener{
            Log.d("SchoolViewHolder", "Clicked school name: ${school.schoolName} \n ${school.schoolId}")
        }

         */

        Log.d("SchoolViewHolder", "Binding school name: ${school.schoolName}")
    }
    val schoolNameTV = binding.tvSchoolName

}