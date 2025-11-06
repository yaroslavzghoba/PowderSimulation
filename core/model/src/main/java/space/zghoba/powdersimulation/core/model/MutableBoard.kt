package space.zghoba.powdersimulation.core.model

class MutableBoard(
    width: Int,
    height: Int,
    init: (coordinates: RectCoordinates) -> Cell = { _ -> Material.Void },
) : Board(
    width = width,
    height = height,
    init = init,
) {

    fun setCell(coordinates: RectCoordinates, cell: Cell) {
        this.validateCoordinates(coordinates)
        val index = this.getIndex(coordinates)
        this.cells[index] = cell
    }
}

/**
 * Create a copy of the mutable board.
 */
fun MutableBoard.copy(
    width: Int = this.width,
    height: Int = this.height,
    init: (coordinates: RectCoordinates) -> Cell = { coordinates -> this.getCell(coordinates) }
) = MutableBoard(
    width = width,
    height = height,
    init = init,
)