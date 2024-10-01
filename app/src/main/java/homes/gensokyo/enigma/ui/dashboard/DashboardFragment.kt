package homes.gensokyo.enigma.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import homes.gensokyo.enigma.MainActivity
import homes.gensokyo.enigma.adapter.ConsumeHistoryAdapter
import homes.gensokyo.enigma.bean.MemberFlowJsonBuilder
import homes.gensokyo.enigma.bean.memberflowbean
import homes.gensokyo.enigma.databinding.FragmentDashboardBinding
import homes.gensokyo.enigma.util.AppConstants
import homes.gensokyo.enigma.util.DateUtils

import homes.gensokyo.enigma.util.SettingUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

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
        CoroutineScope(Dispatchers.IO).launch {
            val memberFlowRequest = MemberFlowJsonBuilder(
                SettingUtils.get("kidUuid", "1111"),
                listOf(2, 5, 6, 7),
                7,
                1,
                100,
                DateUtils.Date2Str(-30),
                DateUtils.Date2Str(1)
            )

            val result: List<memberflowbean.Data> = MainActivity.repository.fetchMemberFlow(memberFlowRequest, AppConstants.headerMap)?.datas!!
            withContext(Dispatchers.Main) {
                adapter = ConsumeHistoryAdapter(result)
                recyclerView.adapter = adapter
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
