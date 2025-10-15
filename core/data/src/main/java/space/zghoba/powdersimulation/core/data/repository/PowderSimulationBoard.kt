package space.zghoba.powdersimulation.core.data.repository

import space.zghoba.powdersimulation.core.model.Cell
import space.zghoba.powdersimulation.core.model.Material
import space.zghoba.powdersimulation.core.model.MaterialRule
import space.zghoba.powdersimulation.core.model.materials.Void
import space.zghoba.powdersimulation.core.model.RectCoordinates

// This companion object helps with the gravity simulation logic
private object Gravity {
    const val UP = 1
    const val DOWN = -1
    const val LEFT = -1
    const val RIGHT = 1
}

class PowderSimulationBoard(
    width: Int,
    height: Int,
    init: (coordinates: RectCoordinates) -> Material = { _ -> Void },
) : space.zghoba.powdersimulation.core.domain.repository.Board(width, height, init) {

    fun setMaterial(material: Material, coordinates: RectCoordinates) {
        this.setCell(cell = material, coordinates = coordinates)
    }

    override fun update() {
        this.cells.forEachIndexed { index, cell ->
            updateCell(cell, getCoordinates(index))
        }
    }

    private fun updateCell(cell: Cell, coordinates: RectCoordinates) {
        val material = cell as Material

        var isUpdated = false
        for (rule in material.rules) {
            if (isUpdated) break

            when (rule) {
                MaterialRule.FALL_STRAIGHT -> {
                    val targetCoordinates = coordinates.copy(
                        y = coordinates.y + Gravity.DOWN,
                    )
                    if (targetCoordinates.y !in 0..<height) break
                    if (this.getCell(coordinates = targetCoordinates) !is Void) break

                    moveCell(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.SLIDE_DIAGONALLY -> {
                    val targetY = coordinates.y + Gravity.DOWN
                    val leftX = coordinates.x + Gravity.LEFT
                    val rightX = coordinates.x + Gravity.RIGHT
                    val leftCoordinates = RectCoordinates(x = leftX, y = targetY)
                    val rightCoordinates = RectCoordinates(x = rightX, y = targetY)

                    if (targetY !in 0..<height) break
                    val isLeftOpen: Boolean = leftX in 0..<width
                            && this.getCell(coordinates = leftCoordinates) is Void
                    val isRightOpen: Boolean = rightX in 0..<width
                            && this.getCell(coordinates = rightCoordinates) is Void
                    // Exit if at least one of the two places is not free.
                    if (!isLeftOpen || !isRightOpen) break

                    val targetCoordinates = listOf(leftCoordinates, rightCoordinates).random()
                    moveCell(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.SLIDE_LEFT -> {
                    val targetCoordinates = coordinates.copy(
                        x = coordinates.x + Gravity.LEFT,
                        y = coordinates.y + Gravity.DOWN,
                    )
                    if (targetCoordinates.x !in 0..<width) break
                    if (targetCoordinates.y !in 0..<height) break
                    if (this.getCell(coordinates = targetCoordinates) !is Void) break

                    moveCell(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.SLIDE_RIGHT -> {
                    val targetCoordinates = coordinates.copy(
                        x = coordinates.x + Gravity.RIGHT,
                        y = coordinates.y + Gravity.DOWN,
                    )
                    if (targetCoordinates.x !in 0..<width) break
                    if (targetCoordinates.y !in 0..<height) break
                    if (this.getCell(coordinates = targetCoordinates) !is Void) break

                    moveCell(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.FLOW_HORIZONTAL -> {
                    val leftCoordinates = coordinates.copy(x = coordinates.x + Gravity.LEFT)
                    val rightCoordinates = coordinates.copy(x = coordinates.x + Gravity.RIGHT)

                    val isLeftOpen: Boolean = leftCoordinates.x !in 0..<width
                            && this.getCell(coordinates = leftCoordinates) !is Void
                    val isRightOpen: Boolean = rightCoordinates.x !in 0..<width
                            && this.getCell(coordinates = rightCoordinates) !is Void
                    // Exit if at least one of the two places is not free.
                    if (!isLeftOpen || !isRightOpen) break

                    val targetCoordinates = listOf(leftCoordinates, rightCoordinates).random()
                    moveCell(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.FLOW_LEFT -> {
                    val targetCoordinates = coordinates.copy(
                        x = coordinates.x + Gravity.LEFT,
                    )
                    if (targetCoordinates.x !in 0..<width) break
                    if (this.getCell(coordinates = targetCoordinates) !is Void) break

                    moveCell(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.FLOW_RIGHT -> {
                    val targetCoordinates = coordinates.copy(
                        x = coordinates.x + Gravity.RIGHT,
                    )
                    if (targetCoordinates.x !in 0..<width) break
                    if (this.getCell(coordinates = targetCoordinates) !is Void) break

                    moveCell(material, coordinates, targetCoordinates)
                    isUpdated = true
                }
            }
        }
    }

    private fun moveCell(
        cell: Cell,
        currentCoordinates: RectCoordinates,
        targetCoordinates: RectCoordinates,
    ) {
        setCell(Void, coordinates = currentCoordinates)
        setCell(cell, coordinates = targetCoordinates)
    }
}