package homes.gensokyo.enigma.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import homes.gensokyo.enigma.MainActivity
import homes.gensokyo.enigma.adapter.ConsumeHistoryAdapter
import homes.gensokyo.enigma.bean.MemberFlowJsonBuilder
import homes.gensokyo.enigma.bean.memberflowbean
import homes.gensokyo.enigma.databinding.FragmentDashboardBinding
import homes.gensokyo.enigma.logic.logic.UserRepository
import homes.gensokyo.enigma.util.AppConstants
import homes.gensokyo.enigma.util.DateUtils

import homes.gensokyo.enigma.util.SettingUtils
import homes.gensokyo.enigma.util.TextUtils.toast
import homes.gensokyo.enigma.viewmodel.DataState
import homes.gensokyo.enigma.viewmodel.UsrdataModel
import homes.gensokyo.enigma.viewmodel.UsrdataModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardFragment : Fragment() {
    private val repository = UserRepository()
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private val usrViewModel: UsrdataModel by lazy {
        val factory = UsrdataModelFactory(repository)
        ViewModelProvider(this, factory)[UsrdataModel::class.java]
    }
    private lateinit var adapter: ConsumeHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val recyclerView: RecyclerView = binding.recyclerView
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = ConsumeHistoryAdapter(emptyList())
        recyclerView.adapter = adapter

        usrViewModel.memberFlowAll.observe(viewLifecycleOwner) { data ->

            if (data!!.datas != null ) {
                adapter = data.datas?.let { ConsumeHistoryAdapter(it) }!!
                recyclerView.adapter = adapter
            } else {
                "数据未准备，请稍后".toast()
            }
            //LogUtils.d("DashboardFragment", data.datas.toString())
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
