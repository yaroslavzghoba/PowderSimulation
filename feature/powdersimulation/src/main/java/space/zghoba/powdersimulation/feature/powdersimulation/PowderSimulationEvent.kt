package space.zghoba.powdersimulation.feature.powdersimulation

import space.zghoba.powdersimulation.core.model.RectCoordinates

internal sealed class PowderSimulationEvent {

    data class SetMaterialAt(val coordinates: RectCoordinates)
        : PowderSimulationEvent()

    data class SelectMaterial(val index: Int) : PowderSimulationEvent()

    data object StartSimulation : PowderSimulationEvent()

    data object StopSimulation : PowderSimulationEvent()
}