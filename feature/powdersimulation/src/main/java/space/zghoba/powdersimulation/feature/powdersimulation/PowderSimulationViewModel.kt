package space.zghoba.powdersimulation.feature.powdersimulation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import space.zghoba.powdersimulation.core.domain.use_cases.UpdatePowderSimulationUseCase
import space.zghoba.powdersimulation.core.model.copy
import space.zghoba.powdersimulation.feature.powdersimulation.utils.PowderSimulationConstants
import kotlin.math.round

class PowderSimulationViewModel : ViewModel() {

    // TODO: Use dependency injection to get the use case
    private val updatePowderSimulationUseCase = UpdatePowderSimulationUseCase()

    private val _uiState = MutableStateFlow(PowderSimulationUiState())
    internal val uiState = _uiState.asStateFlow()
    private val _delayBetweenFramesMs = _uiState
        .map { this.calculateDelay(it.simulationSpeed) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = this.calculateDelay(_uiState.value.simulationSpeed)
        )
    private var simulationJob: Job? = null


    init {
        this.onEvent(event = PowderSimulationEvent.StartSimulation)
    }

    internal fun onEvent(event: PowderSimulationEvent) {
        when (event) {
            is PowderSimulationEvent.SetMaterialAt -> {
                viewModelScope.launch {
                    val updatedBoard = _uiState.value.board.copy { coordinates ->
                        if (coordinates == event.coordinates) {
                            _uiState.value.materials[_uiState.value.selectedMaterialIndex]
                        } else {
                            _uiState.value.board.getCell(coordinates)
                        }
                    }
                    _uiState.value = _uiState.value.copy(board = updatedBoard)
                }
            }
            is PowderSimulationEvent.SelectMaterial -> {
                val lastIndex = _uiState.value.materials.lastIndex
                require(event.index in 0..lastIndex) {
                    "Cannot get material with index ${event.index}. " +
                            "The index should be in the range [0, $lastIndex]"
                }
                _uiState.value = _uiState.value.copy(selectedMaterialIndex = event.index)
            }
            is PowderSimulationEvent.StartSimulation -> {
                simulationJob?.cancel()
                simulationJob = viewModelScope.launch {
                    while (true) {
                        val updatedBoard = updatePowderSimulationUseCase(_uiState.value.board)
                        _uiState.value = _uiState.value.copy(board = updatedBoard)
                        delay(_delayBetweenFramesMs.value)
                    }
                }
            }
            is PowderSimulationEvent.StopSimulation -> {
                simulationJob?.cancel()
            }
        }
    }

    private fun calculateDelay(speed: Float): Long {
        return round(speed * PowderSimulationConstants.DELAY_BETWEEN_FRAMES_MS).toLong()
    }
}