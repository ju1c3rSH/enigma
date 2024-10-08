package homes.gensokyo.enigma.ui.home

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import homes.gensokyo.enigma.bean.UserDataBean
import homes.gensokyo.enigma.databinding.FragmentHomeBinding
import homes.gensokyo.enigma.logic.logic.UserRepository
import homes.gensokyo.enigma.viewmodel.DataState
import homes.gensokyo.enigma.viewmodel.UsrdataModel
import homes.gensokyo.enigma.viewmodel.UsrdataModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers

class HomeFragment : Fragment() {
    private val repository = UserRepository()
    private var _binding: FragmentHomeBinding? = null
    private val usrviewModel: UsrdataModel by lazy {
        val factory = UsrdataModelFactory(repository)
        ViewModelProvider(this, factory)[UsrdataModel::class.java]
    }

    lateinit var state :
            DataState<UserDataBean>

    private val binding get() = _binding!!
    private val serviceScope = CoroutineScope(Dispatchers.IO)
    @SuppressLint("SetTextI18n")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val homeViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        usrviewModel.studentData.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is DataState.Loading -> {

                    Log.d("HomeFragment", "Loading...")
                }
                is DataState.Success -> {

                    binding.textHome.text = "Debug Build"
                    updateData(state.data)
                    Log.d("HomeFragment", "Success: ${state.data}")
                }
                is DataState.Error -> {
                    Log.e("HomeFragment", "Error: ${state.exception }")
                    binding.textHome.text = "出错，等待刷新 \n如果刚过初始化出现错误，请等待，这不是您的问题。"
                    //hideLoading()
                }
            }

        })
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
        //serviceScope.launch {usrviewModel.refreshData(AppConstants.headerMap)}

        val textView: TextView = binding.textHome
        homeViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        return root
    }
    private fun updateData(userBalance: UserDataBean) {
        binding.cardBalanceTextview.text = userBalance.balance.toString()
        binding.cardNameTextview.text = userBalance.studentName
        binding.cardCardNumberTextview.text = userBalance.cardNumber
        binding.cardCCountTextview.text = userBalance.consumptionCount
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}