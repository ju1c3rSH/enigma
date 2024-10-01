package homes.gensokyo.enigma.ui.faceScan

import androidx.fragment.app.viewModels
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import homes.gensokyo.enigma.R

class FaceScanFragment : Fragment() {

    companion object {
        fun newInstance() = FaceScanFragment()
    }

    private val viewModel: FaceScanViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // TODO: Use the ViewModel
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_face_scan, container, false)
    }
}