package space.zghoba.powdersimulation.core.domain.use_cases

import space.zghoba.powdersimulation.core.model.Board
import space.zghoba.powdersimulation.core.model.Material
import space.zghoba.powdersimulation.core.model.MaterialRule
import space.zghoba.powdersimulation.core.model.RectCoordinates

// This companion object helps with the gravity simulation logic
private object Gravity {
    const val UP = 1
    const val DOWN = -1
    const val LEFT = -1
    const val RIGHT = 1
}

/**
 * Update the powder simulation board by one iteration forward.
 */
class UpdatePowderSimulationUseCase {

    /**
     * Update the powder simulation board by one iteration forward.
     */
    operator fun invoke(currentBoard: Board): Board {
        return currentBoard.copy()
            .apply { update() }
    }

    private fun Board.update() {
        val sourceBoard = this.copy()
        (0..<this.height).forEach { y ->
            (0..<this.width).forEach { x ->
                val coordinates = RectCoordinates(x, y)
                this.updateCell(coordinates, sourceBoard)
            }
        }
    }

    private fun Board.updateCell(coordinates: RectCoordinates, sourceBoard: Board) {
        val material = sourceBoard.getCell(coordinates) as? Material
            ?: throw IllegalArgumentException(
                "The cell at coordinates (${coordinates.x}, ${coordinates.y}) is not a material.",
            )

        var isUpdated = false
        for (rule in material.rules) {
            if (isUpdated) break

            when (rule) {
                MaterialRule.FALL_STRAIGHT -> {
                    val targetCoordinates = coordinates.copy(
                        y = coordinates.y + Gravity.DOWN,
                    )
                    if (targetCoordinates.y !in 0..<this.height) break
                    if (this.getCell(coordinates = targetCoordinates) !is Material.Void)
                        break

                    this.moveMaterial(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.SLIDE_DIAGONALLY -> {
                    val targetY = coordinates.y + Gravity.DOWN
                    val leftX = coordinates.x + Gravity.LEFT
                    val rightX = coordinates.x + Gravity.RIGHT
                    val leftCoordinates = RectCoordinates(x = leftX, y = targetY)
                    val rightCoordinates = RectCoordinates(x = rightX, y = targetY)

                    if (targetY !in 0..<this.height) break
                    val isLeftOpen: Boolean = leftX in 0..<this.width
                            && this.getCell(coordinates = leftCoordinates) is Material.Void
                    val isRightOpen: Boolean = rightX in 0..<this.width
                            && this.getCell(coordinates = rightCoordinates) is Material.Void
                    // Exit if at least one of the two places is not free.
                    if (!isLeftOpen || !isRightOpen) break

                    val targetCoordinates = listOf(leftCoordinates, rightCoordinates).random()
                    this.moveMaterial(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.SLIDE_LEFT -> {
                    val targetCoordinates = coordinates.copy(
                        x = coordinates.x + Gravity.LEFT,
                        y = coordinates.y + Gravity.DOWN,
                    )
                    if (targetCoordinates.x !in 0..<this.width) break
                    if (targetCoordinates.y !in 0..<this.height) break
                    if (this.getCell(coordinates = targetCoordinates) !is Material.Void) break

                    this.moveMaterial(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.SLIDE_RIGHT -> {
                    val targetCoordinates = coordinates.copy(
                        x = coordinates.x + Gravity.RIGHT,
                        y = coordinates.y + Gravity.DOWN,
                    )
                    if (targetCoordinates.x !in 0..<this.width) break
                    if (targetCoordinates.y !in 0..<this.height) break
                    if (this.getCell(coordinates = targetCoordinates) !is Material.Void) break

                    this.moveMaterial(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.FLOW_HORIZONTAL -> {
                    val leftCoordinates = coordinates.copy(x = coordinates.x + Gravity.LEFT)
                    val rightCoordinates = coordinates.copy(x = coordinates.x + Gravity.RIGHT)

                    val isLeftOpen: Boolean = leftCoordinates.x !in 0..<this.width
                            && this.getCell(coordinates = leftCoordinates) !is Material.Void
                    val isRightOpen: Boolean = rightCoordinates.x !in 0..<this.width
                            && this.getCell(coordinates = rightCoordinates) !is Material.Void
                    // Exit if at least one of the two places is not free.
                    if (!isLeftOpen || !isRightOpen) break

                    val targetCoordinates = listOf(leftCoordinates, rightCoordinates).random()
                    this.moveMaterial(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.FLOW_LEFT -> {
                    val targetCoordinates = coordinates.copy(
                        x = coordinates.x + Gravity.LEFT,
                    )
                    if (targetCoordinates.x !in 0..<this.width) break
                    if (this.getCell(coordinates = targetCoordinates) !is Material.Void) break

                    this.moveMaterial(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.FLOW_RIGHT -> {
                    val targetCoordinates = coordinates.copy(
                        x = coordinates.x + Gravity.RIGHT,
                    )
                    if (targetCoordinates.x !in 0..<this.width) break
                    if (this.getCell(coordinates = targetCoordinates) !is Material.Void) break

                    this.moveMaterial(material, coordinates, targetCoordinates)
                    isUpdated = true
                }
            }
        }
    }

    private fun Board.moveMaterial(
        material: Material,
        currentCoordinates: RectCoordinates,
        targetCoordinates: RectCoordinates,
    ) {
        this.setCell(Material.Void, coordinates = currentCoordinates)
        this.setCell(material, coordinates = targetCoordinates)
    }
}