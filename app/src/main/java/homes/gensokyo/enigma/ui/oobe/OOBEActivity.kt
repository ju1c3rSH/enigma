package homes.gensokyo.enigma.ui.oobe

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import homes.gensokyo.enigma.MainActivity
import homes.gensokyo.enigma.ui.compose.component.KonamiCornerTaps
import homes.gensokyo.enigma.ui.compose.theme.EnigmaTheme

class OOBEActivity : ComponentActivity() {

    private val viewModel: OobeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!viewModel.goBack()) finish()
            }
        })
        setContent {
            EnigmaTheme {
                val state by viewModel.state.collectAsState()
                LaunchedEffect(Unit) {
                    viewModel.finished.collect { ok ->
                        if (ok) {
                            startActivity(Intent(this@OOBEActivity, MainActivity::class.java))
                            finish()
                        }
                    }
                }
                Box(Modifier.fillMaxSize()) {
                    OobeScreen(
                        state = state,
                        onQueryChange = viewModel::onQueryChange,
                        onSelectSchool = viewModel::selectSchool,
                        onSelectGrade = viewModel::selectGrade,
                        onSelectClass = viewModel::selectClass,
                        onChangeSchool = viewModel::changeSchool,
                        onNameChange = viewModel::onNameChange,
                        onCardChange = viewModel::onCardNumberChange,
                        onSubmit = viewModel::submit,
                        onNext = viewModel::goNext,
                        onBack = { viewModel.goBack() }
                    )
                    KonamiCornerTaps()
                }
            }
        }
    }
}
