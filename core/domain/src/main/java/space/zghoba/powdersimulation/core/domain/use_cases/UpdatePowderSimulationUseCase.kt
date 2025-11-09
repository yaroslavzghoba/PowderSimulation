package space.zghoba.powdersimulation.core.domain.use_cases

import space.zghoba.powdersimulation.core.mappers.toBoard
import space.zghoba.powdersimulation.core.mappers.toMutableBoard
import space.zghoba.powdersimulation.core.model.Board
import space.zghoba.powdersimulation.core.model.Material
import space.zghoba.powdersimulation.core.model.MaterialMovementRule
import space.zghoba.powdersimulation.core.model.MutableBoard
import space.zghoba.powdersimulation.core.model.RectCoordinates
import space.zghoba.powdersimulation.core.model.copy

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

    /**
     * Update the powder simulation board by one iteration forward.
     */
    operator fun invoke(board: Board): Board {
        return board.toMutableBoard()
            .apply { this.update() }
            .toBoard()
    }

    private fun MutableBoard.update() {
        val sourceBoard = this.copy()
        (0..<sourceBoard.height).forEach { y ->
            (0..<sourceBoard.width).forEach { x ->
                val coordinates = RectCoordinates(x, y)
                updateCell(coordinates, sourceBoard)
            }
        }
    }

    private fun MutableBoard.updateCell(coordinates: RectCoordinates, sourceBoard: Board) {
        val material = sourceBoard.getCell(coordinates) as? Material
            ?: throw IllegalArgumentException(
                "The cell at coordinates $coordinates is not a material.",
            )

        // Move the material according to its rules.
        var isMoved = false
        for (rule in material.rules) {
            if (isMoved) continue

            isMoved = when (rule) {
                MaterialMovementRule.FALL_STRAIGHT -> {
                    this.tryMoveMaterialDown(
                        materialAtCurrentCoordinates = material,
                        currentCoordinates = coordinates,
                    )
                }

                MaterialMovementRule.SLIDE_DIAGONALLY -> {
                    this.tryMoveMaterialDownDiagonally(
                        materialAtCurrentCoordinates = material,
                        currentCoordinates = coordinates,
                    )
                }

                MaterialMovementRule.SLIDE_LEFT -> {
                    this.tryMoveMaterialDownLeft(
                        materialAtCurrentCoordinates = material,
                        currentCoordinates = coordinates,
                    )
                }

                MaterialMovementRule.SLIDE_RIGHT -> {
                    this.tryMoveMaterialDownRight(
                        materialAtCurrentCoordinates = material,
                        currentCoordinates = coordinates,
                    )
                }

                MaterialMovementRule.FLOW_HORIZONTAL -> {
                    this.tryMoveMaterialHorizontally(
                        materialAtCurrentCoordinates = material,
                        currentCoordinates = coordinates,
                    )
                }

                MaterialMovementRule.FLOW_LEFT -> {
                    this.tryMoveMaterialLeft(
                        materialAtCurrentCoordinates = material,
                        currentCoordinates = coordinates,
                    )
                }

                MaterialMovementRule.FLOW_RIGHT -> {
                    this.tryMoveMaterialRight(
                        materialAtCurrentCoordinates = material,
                        currentCoordinates = coordinates,
                    )
                }
            }
        }
    }

    /**
     * Move the material down if it is possible.
     */
    private fun MutableBoard.tryMoveMaterialDown(
        materialAtCurrentCoordinates: Material,
        currentCoordinates: RectCoordinates,
    ): Boolean {
        val targetCoordinates = currentCoordinates.copy(
            y = currentCoordinates.y + GravityOffsetUnits.DOWN,
        )
        if (targetCoordinates.y !in 0..<this.height) return false

        val materialAtTargetCoordinates =
            this.getCell(coordinates = targetCoordinates) as Material
        if (materialAtCurrentCoordinates.density <= materialAtTargetCoordinates.density) {
            return false
        }

        // Swap cells.
        this.setCell(coordinates = currentCoordinates, cell = materialAtTargetCoordinates)
        this.setCell(coordinates = targetCoordinates, cell = materialAtCurrentCoordinates)
        return true
    }

    /**
     * Move the material down diagonally if it is possible.
     */
    private fun MutableBoard.tryMoveMaterialDownDiagonally(
        materialAtCurrentCoordinates: Material,
        currentCoordinates: RectCoordinates,
    ): Boolean {
        val targetY = currentCoordinates.y + GravityOffsetUnits.DOWN
        val belowLeftX = currentCoordinates.x + GravityOffsetUnits.LEFT
        val belowRightX = currentCoordinates.x + GravityOffsetUnits.RIGHT
        val belowLeftCoordinates = RectCoordinates(x = belowLeftX, y = targetY)
        val belowRightCoordinates = RectCoordinates(x = belowRightX, y = targetY)

        if (targetY !in 0..<this.height) return false

        // Check a cell below left.
        if (belowLeftX !in 0..<this.width) return false
        val materialBelowLeft =
            this.getCell(coordinates = belowLeftCoordinates) as Material
        val isBelowLeftOpen = materialAtCurrentCoordinates.density > materialBelowLeft.density

        // Check a cell below right.
        if (belowRightX !in 0..<this.width) return false
        val materialBelowRight =
            this.getCell(coordinates = belowRightCoordinates) as Material
        val isBelowRightOpen = materialAtCurrentCoordinates.density > materialBelowRight.density

        // Exit if at least one of the two places is not free.
        if (!isBelowLeftOpen || !isBelowRightOpen) return false

        // Select the target cell.
        val targetCoordinates =
            listOf(belowLeftCoordinates, belowRightCoordinates).random()
        val materialAtTargetCoordinates = this.getCell(coordinates = targetCoordinates)

        // Swap cells.
        this.setCell(coordinates = currentCoordinates, cell = materialAtTargetCoordinates)
        this.setCell(coordinates = targetCoordinates, cell = materialAtCurrentCoordinates)
        return true
    }

    /**
     * Move the material down left if it is possible.
     */
    private fun MutableBoard.tryMoveMaterialDownLeft(
        materialAtCurrentCoordinates: Material,
        currentCoordinates: RectCoordinates,
    ): Boolean {
        val targetCoordinates = currentCoordinates.copy(
            x = currentCoordinates.x + GravityOffsetUnits.LEFT,
            y = currentCoordinates.y + GravityOffsetUnits.DOWN,
        )
        if (targetCoordinates.x !in 0..<this.width) return false
        if (targetCoordinates.y !in 0..<this.height) return false
        val materialAtTargetCoordinates =
            this.getCell(coordinates = targetCoordinates) as Material
        if (materialAtCurrentCoordinates.density <= materialAtTargetCoordinates.density) {
            return false
        }

        // Swap cells.
        this.setCell(coordinates = currentCoordinates, cell = materialAtTargetCoordinates)
        this.setCell(coordinates = targetCoordinates, cell = materialAtCurrentCoordinates)
        return true
    }

    /**
     * Move the material down right if it is possible.
     */
    private fun MutableBoard.tryMoveMaterialDownRight(
        materialAtCurrentCoordinates: Material,
        currentCoordinates: RectCoordinates,
    ): Boolean {
        val targetCoordinates = currentCoordinates.copy(
            x = currentCoordinates.x + GravityOffsetUnits.RIGHT,
            y = currentCoordinates.y + GravityOffsetUnits.DOWN,
        )
        if (targetCoordinates.x !in 0..<this.width) return false
        if (targetCoordinates.y !in 0..<this.height) return false
        val materialAtTargetCoordinates =
            this.getCell(coordinates = targetCoordinates) as Material
        if (materialAtCurrentCoordinates.density <= materialAtTargetCoordinates.density) {
            return false
        }

        // Swap cells.
        this.setCell(coordinates = currentCoordinates, cell = materialAtTargetCoordinates)
        this.setCell(coordinates = targetCoordinates, cell = materialAtCurrentCoordinates)
        return true
    }

    /**
     * Move the material horizontally if it is possible.
     */
    private fun MutableBoard.tryMoveMaterialHorizontally(
        materialAtCurrentCoordinates: Material,
        currentCoordinates: RectCoordinates,
    ): Boolean {
        val leftCoordinates = currentCoordinates
            .copy(x = currentCoordinates.x + GravityOffsetUnits.LEFT)
        val rightCoordinates = currentCoordinates
            .copy(x = currentCoordinates.x + GravityOffsetUnits.RIGHT)

        // Check a cell left.
        if (leftCoordinates.x !in 0..<this.width) return false
        val materialLeft =
            this.getCell(coordinates = leftCoordinates) as Material
        val isLeftOpen = materialAtCurrentCoordinates.density > materialLeft.density

        // Check a cell right.
        if (rightCoordinates.x !in 0..<this.width) return false
        val materialRight =
            this.getCell(coordinates = rightCoordinates) as Material
        val isRightOpen = materialAtCurrentCoordinates.density > materialRight.density

        // Exit if at least one of the two places is not free.
        if (!isLeftOpen || !isRightOpen) return false

        // Select the target cell.
        val targetCoordinates =
            listOf(leftCoordinates, rightCoordinates).random()
        val materialAtTargetCoordinates = this.getCell(coordinates = targetCoordinates)

        // Swap cells.
        this.setCell(coordinates = currentCoordinates, cell = materialAtTargetCoordinates)
        this.setCell(coordinates = targetCoordinates, cell = materialAtCurrentCoordinates)
        return true
    }

    /**
     * Move the material left if it is possible.
     */
    private fun MutableBoard.tryMoveMaterialLeft(
        materialAtCurrentCoordinates: Material,
        currentCoordinates: RectCoordinates,
    ): Boolean {
        val targetCoordinates = currentCoordinates.copy(
            x = currentCoordinates.x + GravityOffsetUnits.LEFT,
        )
        if (targetCoordinates.x !in 0..<this.width) return false
        val materialAtTargetCoordinates =
            this.getCell(coordinates = targetCoordinates) as Material
        if (materialAtCurrentCoordinates.density <= materialAtTargetCoordinates.density) {
            return false
        }

        // Swap cells.
        this.setCell(coordinates = currentCoordinates, cell = materialAtTargetCoordinates)
        this.setCell(coordinates = targetCoordinates, cell = materialAtCurrentCoordinates)
        return true
    }

    /**
     * Move the material right if it is possible.
     */
    private fun MutableBoard.tryMoveMaterialRight(
        materialAtCurrentCoordinates: Material,
        currentCoordinates: RectCoordinates,
    ): Boolean {
        val targetCoordinates = currentCoordinates.copy(
            x = currentCoordinates.x + GravityOffsetUnits.RIGHT,
        )
        if (targetCoordinates.x !in 0..<this.width) return false
        val materialAtTargetCoordinates =
            this.getCell(coordinates = targetCoordinates) as Material
        if (materialAtCurrentCoordinates.density <= materialAtTargetCoordinates.density) {
            return false
        }

        // Swap cells.
        this.setCell(coordinates = currentCoordinates, cell = materialAtTargetCoordinates)
        this.setCell(coordinates = targetCoordinates, cell = materialAtCurrentCoordinates)
        return true
    }
}