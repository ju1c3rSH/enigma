package homes.gensokyo.enigma.ui.faceScan

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import homes.gensokyo.enigma.MainApplication.Companion.repository
import homes.gensokyo.enigma.R
import homes.gensokyo.enigma.adapter.QueryAdapter
import homes.gensokyo.enigma.bean.MemberFlowJsonBuilder
import homes.gensokyo.enigma.databinding.FragmentFaceScanBinding
import homes.gensokyo.enigma.viewmodel.UsrdataModel
import homes.gensokyo.enigma.viewmodel.UsrdataModelFactory

class FaceScanFragment : Fragment() {

    companion object {
        fun newInstance() = FaceScanFragment()
    }

    private val usrViewModel: UsrdataModel by lazy {
        val factory = UsrdataModelFactory(repository)
        ViewModelProvider(this, factory)[UsrdataModel::class.java]
    }

    private lateinit var adapter: QueryAdapter
    private var _binding: FragmentFaceScanBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFaceScanBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 初始化 RecyclerView
        setupRecyclerView()

        // 观察 ViewModel 中的 queryData
        observeViewModel()

        // 启动数据加载
        //usrViewModel.fetchQueryData() // 假设你有一个 fetchQueryData 方法来加载数据

        return root
    }

    private fun setupRecyclerView() {
        adapter = QueryAdapter(emptyList())
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@FaceScanFragment.adapter
        }
    }

    private fun observeViewModel() {
        usrViewModel.queryData.observe(viewLifecycleOwner) { queryData ->
            queryData?.let {
                adapter.updateData(it.datas) // 更新适配器数据
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // 避免内存泄漏
    }
}
