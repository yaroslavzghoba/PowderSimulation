package space.zghoba.powdersimulation

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.ui.Modifier
import space.zghoba.powdersimulation.feature.powdersimulation.PowderSimulationScreen
import space.zghoba.powdersimulation.feature.powdersimulation.PowderSimulationViewModel
import space.zghoba.powdersimulation.ui.theme.PowderSimulationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PowderSimulationTheme {
                val viewModel: PowderSimulationViewModel by viewModels()
                PowderSimulationScreen(
                    viewModel = viewModel,
                    modifier = Modifier
                        .fillMaxSize()
                        .systemBarsPadding(),
                )
            }
        }
    }
}