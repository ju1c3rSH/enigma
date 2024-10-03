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
import androidx.lifecycle.lifecycleScope
import homes.gensokyo.enigma.BuildConfig
import homes.gensokyo.enigma.bean.UserDataBean
import homes.gensokyo.enigma.databinding.FragmentHomeBinding
import homes.gensokyo.enigma.logic.logic.UserRepository
import homes.gensokyo.enigma.util.AppConstants
import homes.gensokyo.enigma.util.LogUtils
import homes.gensokyo.enigma.viewmodel.DataState
import homes.gensokyo.enigma.viewmodel.UsrdataModel
import homes.gensokyo.enigma.viewmodel.UsrdataModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {
    private val repository = UserRepository()
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val usrviewModel: UsrdataModel by lazy {
        val factory = UsrdataModelFactory(repository)
        ViewModelProvider(this, factory)[UsrdataModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupObservers()


        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        viewLifecycleOwner.lifecycleScope.launch {
            usrviewModel.refreshData(AppConstants.headerMap)
        }
    }

    private fun setupObservers() {

        usrviewModel.studentData.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is DataState.Loading -> {
                    binding.textHome.text = "Now Loading..."
                    LogUtils.d("HomeFragment", "Loading...")
                }
                is DataState.Success -> {
                    updateData(state.data)
                    binding.textHome.text = ""
                    LogUtils.d("HomeFragment", "Success: ${state.data}")
                }
                is DataState.Error -> {
                    binding.textHome.text = "出错，等待刷新 \n如果刚过初始化出现错误，请等待，这不是您的问题。"
                    Log.e("HomeFragment", "Error: ${state.exception}")
                }
            }
        })

    }

    private fun updateData(userBalance: UserDataBean) {
        binding.cardBalanceTextview.text = userBalance.balance
        binding.cardNameTextview.text = userBalance.studentName
        binding.cardCardNumberTextview.text = userBalance.cardNumber
        binding.cardCCountTextview.text = userBalance.consumptionCount
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
