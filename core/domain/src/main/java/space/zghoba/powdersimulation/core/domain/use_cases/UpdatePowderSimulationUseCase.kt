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

                    val materialAtTargetCoordinates =
                        updatedBoard.getCell(coordinates = targetCoordinates) as Material
                    if (material.density <= materialAtTargetCoordinates.density) continue

                    swapMaterials(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.SLIDE_DIAGONALLY -> {
                    val targetY = coordinates.y + GravityOffsetUnits.DOWN
                    val belowLeftX = coordinates.x + GravityOffsetUnits.LEFT
                    val belowRightX = coordinates.x + GravityOffsetUnits.RIGHT
                    val belowLeftCoordinates = RectCoordinates(x = belowLeftX, y = targetY)
                    val belowRightCoordinates = RectCoordinates(x = belowRightX, y = targetY)

                    if (targetY !in 0..<updatedBoard.height) continue

                    // Check a cell below left.
                    if (belowLeftX !in 0..<updatedBoard.width) continue
                    val materialBelowLeft =
                        updatedBoard.getCell(coordinates = belowLeftCoordinates) as Material
                    val isBelowLeftOpen = material.density > materialBelowLeft.density

                    // Check a cell below right.
                    if (belowRightX !in 0..<updatedBoard.width) continue
                    val materialBelowRight =
                        updatedBoard.getCell(coordinates = belowRightCoordinates) as Material
                    val isBelowRightOpen = material.density > materialBelowRight.density

                    // Exit if at least one of the two places is not free.
                    if (!isBelowLeftOpen || !isBelowRightOpen) continue

                    val targetCoordinates =
                        listOf(belowLeftCoordinates, belowRightCoordinates).random()
                    swapMaterials(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.SLIDE_LEFT -> {
                    val targetCoordinates = coordinates.copy(
                        x = coordinates.x + GravityOffsetUnits.LEFT,
                        y = coordinates.y + GravityOffsetUnits.DOWN,
                    )
                    if (targetCoordinates.x !in 0..<updatedBoard.width) continue
                    if (targetCoordinates.y !in 0..<updatedBoard.height) continue
                    val materialAtTargetCoordinates =
                        updatedBoard.getCell(coordinates = targetCoordinates) as Material
                    if (material.density <= materialAtTargetCoordinates.density) continue

                    swapMaterials(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.SLIDE_RIGHT -> {
                    val targetCoordinates = coordinates.copy(
                        x = coordinates.x + GravityOffsetUnits.RIGHT,
                        y = coordinates.y + GravityOffsetUnits.DOWN,
                    )
                    if (targetCoordinates.x !in 0..<updatedBoard.width) continue
                    if (targetCoordinates.y !in 0..<updatedBoard.height) continue
                    val materialAtTargetCoordinates =
                        updatedBoard.getCell(coordinates = targetCoordinates) as Material
                    if (material.density <= materialAtTargetCoordinates.density) continue

                    swapMaterials(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.FLOW_HORIZONTAL -> {
                    val leftCoordinates = coordinates.copy(x = coordinates.x + GravityOffsetUnits.LEFT)
                    val rightCoordinates = coordinates.copy(x = coordinates.x + GravityOffsetUnits.RIGHT)

                    // Check a cell left.
                    if (leftCoordinates.x !in 0..<updatedBoard.width) continue
                    val materialLeft =
                        updatedBoard.getCell(coordinates = leftCoordinates) as Material
                    val isLeftOpen = material.density > materialLeft.density

                    // Check a cell right.
                    if (rightCoordinates.x !in 0..<updatedBoard.width) continue
                    val materialRight =
                        updatedBoard.getCell(coordinates = rightCoordinates) as Material
                    val isRightOpen = material.density > materialRight.density

                    // Exit if at least one of the two places is not free.
                    if (!isLeftOpen || !isRightOpen) continue

                    val targetCoordinates = listOf(leftCoordinates, rightCoordinates).random()
                    swapMaterials(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.FLOW_LEFT -> {
                    val targetCoordinates = coordinates.copy(
                        x = coordinates.x + GravityOffsetUnits.LEFT,
                    )
                    if (targetCoordinates.x !in 0..<updatedBoard.width) continue
                    val materialAtTargetCoordinates =
                        updatedBoard.getCell(coordinates = targetCoordinates) as Material
                    if (material.density <= materialAtTargetCoordinates.density) continue

                    swapMaterials(material, coordinates, targetCoordinates)
                    isUpdated = true
                }

                MaterialRule.FLOW_RIGHT -> {
                    val targetCoordinates = coordinates.copy(
                        x = coordinates.x + GravityOffsetUnits.RIGHT,
                    )
                    if (targetCoordinates.x !in 0..<updatedBoard.width) continue
                    val materialAtTargetCoordinates =
                        updatedBoard.getCell(coordinates = targetCoordinates) as Material
                    if (material.density <= materialAtTargetCoordinates.density) continue

                    swapMaterials(material, coordinates, targetCoordinates)
                    isUpdated = true
                }
            }
        }
    }

    private fun swapMaterials(
        firstMaterial: Material,
        firstMaterialCoordinates: RectCoordinates,
        secondMaterialCoordinates: RectCoordinates,
    ) {
        val secondMaterial = updatedBoard.getCell(secondMaterialCoordinates)
        updatedBoard = updatedBoard.copy { coordinates ->
            when (coordinates) {
                firstMaterialCoordinates -> secondMaterial
                secondMaterialCoordinates -> firstMaterial
                else -> updatedBoard.getCell(coordinates)
            }
        }
    }
}