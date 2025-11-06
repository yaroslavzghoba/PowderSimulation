package space.zghoba.powdersimulation.core.mappers

import space.zghoba.powdersimulation.core.model.Board
import space.zghoba.powdersimulation.core.model.MutableBoard

/**
 * Convert a read-only board to a mutable board.
 */
fun Board.toMutableBoard() = MutableBoard(
    width = this.width,
    height = this.height,
    init = { coordinates -> this.getCell(coordinates) }
)

/**
 * Convert a mutable board to a read-only board.
 */
fun MutableBoard.toBoard() = Board(
    width = this.width,
    height = this.height,
    init = { coordinates -> this.getCell(coordinates) }
)