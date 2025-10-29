package space.zghoba.powdersimulation.feature.powdersimulation

import space.zghoba.powdersimulation.core.model.Board
import space.zghoba.powdersimulation.core.model.Material
import space.zghoba.powdersimulation.feature.powdersimulation.utils.PowderSimulationConstants

internal data class PowderSimulationUiState(
    val board: Board = Board(
        width = PowderSimulationConstants.BOARD_WIDTH,
        height = PowderSimulationConstants.BOARD_HEIGHT,
        init = { _ -> Material.Void },
    ),
    val simulationSpeed: Float = PowderSimulationConstants.DEFAULT_SIMULATION_SPEED,
    val materials: List<Material> = Material.all,
    val selectedMaterialIndex: Int = 0,
)
