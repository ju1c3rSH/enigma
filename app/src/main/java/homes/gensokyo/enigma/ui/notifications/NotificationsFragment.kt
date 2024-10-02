package homes.gensokyo.enigma.ui.notifications

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import homes.gensokyo.enigma.MainApplication.Companion.repository
import homes.gensokyo.enigma.databinding.FragmentNotificationsBinding
import homes.gensokyo.enigma.ui.about.AboutActivity
import homes.gensokyo.enigma.ui.setting.SettingsActivity
import homes.gensokyo.enigma.viewmodel.DataState
import homes.gensokyo.enigma.viewmodel.UsrdataModel
import homes.gensokyo.enigma.viewmodel.UsrdataModelFactory

class NotificationsFragment : Fragment() {

    private var _binding: FragmentNotificationsBinding? = null
    private val usrviewModel: UsrdataModel by lazy {
        val factory = UsrdataModelFactory(repository)
        ViewModelProvider(this, factory)[UsrdataModel::class.java]
    }
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val notificationsViewModel =
            ViewModelProvider(this).get(NotificationsViewModel::class.java)

        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.about.setOnClickListener {
            val intent = Intent(requireContext(), AboutActivity::class.java)
            startActivity(intent)
        }
        binding.button3.setOnClickListener {
            val intent = Intent(requireContext(), SettingsActivity::class.java)
            startActivity(intent)
        }
        val textView: TextView = binding.textNotifications
        notificationsViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }
        usrviewModel.studentData.observe(viewLifecycleOwner, Observer { state ->
            when (state) {
                is DataState.Loading -> {

                    Log.d("NotificationsFragment", "Loading...")
                }
                is DataState.Success -> {
                    Log.d("NotificationsFragment", "Success: ${state.data}")
                    binding.textViewNameENG.text = state.data.studentNamePinyin
                    binding.textViewName.text= state.data.studentName
                    binding.textViewWhere.text = state.data.className
                    binding.textViewCardNumber.text = state.data.cardNumber
                    Glide.with(binding.imageView)
                        .load("http://wx.ivxiaoyuan.com/sc/files/personPhoto/"+state.data.headSculpture)
                        .into(binding.imageView)
                }
                is DataState.Error -> {
                    Log.e("NotificationsFragment", "Error: ${state.exception }")
                    //hideLoading()
                }
            }

        })
        return root

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}