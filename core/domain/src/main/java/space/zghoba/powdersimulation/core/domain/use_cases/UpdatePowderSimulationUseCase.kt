package space.zghoba.powdersimulation.core.domain.use_cases

import space.zghoba.powdersimulation.core.model.Board
import space.zghoba.powdersimulation.core.model.Material
import space.zghoba.powdersimulation.core.model.MaterialRule
import space.zghoba.powdersimulation.core.model.RectCoordinates

// This companion object helps with the gravity simulation logic
private object GravityOffsetUnits {
    const val UP = 1
    const val DOWN = -1
    const val LEFT = -1
    const val RIGHT = 1
}


/**
 * Update the powder simulation board by one iteration forward.
 */
class UpdatePowderSimulationUseCase {

    private lateinit var updatedBoard: Board

    /**
     * Update the powder simulation board by one iteration forward.
     */
    operator fun invoke(board: Board): Board {
        updatedBoard = board.copy()
        updateBoard(sourceBoard = board)
        return updatedBoard
    }

    private fun updateBoard(sourceBoard: Board) {
        (0..<sourceBoard.height).forEach { y ->
            (0..<sourceBoard.width).forEach { x ->
                val coordinates = RectCoordinates(x, y)
                updateCell(coordinates, sourceBoard)
            }
        }
    }

    private fun updateCell(coordinates: RectCoordinates, sourceBoard: Board) {
        val material = sourceBoard.getCell(coordinates) as? Material
            ?: throw IllegalArgumentException(
                "The cell at coordinates (${coordinates.x}, ${coordinates.y}) is not a material.",
            )

        var isUpdated = false
        for (rule in material.rules) {
            if (isUpdated) continue

            when (rule) {
                MaterialRule.FALL_STRAIGHT -> {
                    val targetCoordinates = coordinates.copy(
                        y = coordinates.y + GravityOffsetUnits.DOWN,
                    )
                    if (targetCoordinates.y !in 0..<updatedBoard.height) continue
                    if (updatedBoard.getCell(coordinates = targetCoordinates) !is Material.Void)
                        continue

                    this.moveMaterial(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.SLIDE_DIAGONALLY -> {
                    val targetY = coordinates.y + GravityOffsetUnits.DOWN
                    val leftX = coordinates.x + GravityOffsetUnits.LEFT
                    val rightX = coordinates.x + GravityOffsetUnits.RIGHT
                    val leftCoordinates = RectCoordinates(x = leftX, y = targetY)
                    val rightCoordinates = RectCoordinates(x = rightX, y = targetY)

                    if (targetY !in 0..<updatedBoard.height) continue
                    val isLeftOpen: Boolean = leftX in 0..<updatedBoard.width
                            && updatedBoard.getCell(coordinates = leftCoordinates) is Material.Void
                    val isRightOpen: Boolean = rightX in 0..<updatedBoard.width
                            && updatedBoard.getCell(coordinates = rightCoordinates) is Material.Void
                    // Exit if at least one of the two places is not free.
                    if (!isLeftOpen || !isRightOpen) continue

                    val targetCoordinates = listOf(leftCoordinates, rightCoordinates).random()
                    this.moveMaterial(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.SLIDE_LEFT -> {
                    val targetCoordinates = coordinates.copy(
                        x = coordinates.x + GravityOffsetUnits.LEFT,
                        y = coordinates.y + GravityOffsetUnits.DOWN,
                    )
                    if (targetCoordinates.x !in 0..<updatedBoard.width) continue
                    if (targetCoordinates.y !in 0..<updatedBoard.height) continue
                    if (updatedBoard.getCell(coordinates = targetCoordinates) !is Material.Void)
                        continue

                    this.moveMaterial(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.SLIDE_RIGHT -> {
                    val targetCoordinates = coordinates.copy(
                        x = coordinates.x + GravityOffsetUnits.RIGHT,
                        y = coordinates.y + GravityOffsetUnits.DOWN,
                    )
                    if (targetCoordinates.x !in 0..<updatedBoard.width) continue
                    if (targetCoordinates.y !in 0..<updatedBoard.height) continue
                    if (updatedBoard.getCell(coordinates = targetCoordinates) !is Material.Void)
                        continue

                    this.moveMaterial(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.FLOW_HORIZONTAL -> {
                    val leftCoordinates = coordinates.copy(x = coordinates.x + GravityOffsetUnits.LEFT)
                    val rightCoordinates = coordinates.copy(x = coordinates.x + GravityOffsetUnits.RIGHT)

                    val isLeftOpen: Boolean = leftCoordinates.x in 0..<updatedBoard.width
                            && updatedBoard.getCell(coordinates = leftCoordinates) is Material.Void
                    val isRightOpen: Boolean = rightCoordinates.x in 0..<updatedBoard.width
                            && updatedBoard.getCell(coordinates = rightCoordinates) is Material.Void
                    // Exit if at least one of the two places is not free.
                    if (!isLeftOpen || !isRightOpen) continue

                    val targetCoordinates = listOf(leftCoordinates, rightCoordinates).random()
                    this.moveMaterial(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.FLOW_LEFT -> {
                    val targetCoordinates = coordinates.copy(
                        x = coordinates.x + GravityOffsetUnits.LEFT,
                    )
                    if (targetCoordinates.x !in 0..<updatedBoard.width) continue
                    if (updatedBoard.getCell(coordinates = targetCoordinates) !is Material.Void)
                        continue

                    this.moveMaterial(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.FLOW_RIGHT -> {
                    val targetCoordinates = coordinates.copy(
                        x = coordinates.x + GravityOffsetUnits.RIGHT,
                    )
                    if (targetCoordinates.x !in 0..<updatedBoard.width) continue
                    if (updatedBoard.getCell(coordinates = targetCoordinates) !is Material.Void)
                        continue

                    moveMaterial(material, coordinates, targetCoordinates)
                    isUpdated = true
                }
            }
        }
    }

    private fun moveMaterial(
        material: Material,
        currentCoordinates: RectCoordinates,
        targetCoordinates: RectCoordinates,
    ) {
        updatedBoard = updatedBoard.copy { coordinates ->
            when (coordinates) {
                currentCoordinates -> Material.Void
                targetCoordinates -> material
                else -> updatedBoard.getCell(coordinates)
            }
        }
    }
}