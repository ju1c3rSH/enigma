package homes.gensokyo.enigma.ui.overview

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import homes.gensokyo.enigma.R
import homes.gensokyo.enigma.logic.logic.UserRepository
import homes.gensokyo.enigma.ui.compose.theme.EnigmaTheme
import homes.gensokyo.enigma.viewmodel.UsrdataModel
import homes.gensokyo.enigma.viewmodel.UsrdataModelFactory

class OverviewFragment : Fragment() {

    private val usrViewModel: UsrdataModel by lazy {
        val factory = UsrdataModelFactory(UserRepository())
        ViewModelProvider(requireActivity(), factory)[UsrdataModel::class.java]
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                EnigmaTheme {
                    OverviewScreen(
                        studentData = usrViewModel.studentData,
                        memberFlow = usrViewModel.memberFlow,
                        onViewAll = { findNavController().navigate(R.id.navigation_dashboard) }
                    )
                }
            }
        }
    }
}
