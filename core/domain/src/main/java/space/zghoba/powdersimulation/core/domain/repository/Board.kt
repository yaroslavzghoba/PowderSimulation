package space.zghoba.powdersimulation.core.domain.repository

import space.zghoba.powdersimulation.core.model.Cell
import space.zghoba.powdersimulation.core.model.RectCoordinates

abstract class Board(
    val width: Int,
    val height: Int,
    private val init: (coordinates: RectCoordinates) -> Cell,
) {
    private val size = width * height
    protected var cells: MutableList<Cell> = List(size = size) { index ->
        init(getCoordinates(index))
    }.toMutableList()

    fun getCell(coordinates: RectCoordinates): Cell {
        validateCoordinates(coordinates)

        val index = getIndex(coordinates)
        return cells[index]
    }

    protected fun setCell(cell: Cell, coordinates: RectCoordinates) {
        validateCoordinates(coordinates)

        val index = getIndex(coordinates)
        cells[index] = cell
    }

    private fun validateCoordinates(coordinates: RectCoordinates) {
        require(coordinates.x in 0..<width) {
            "The x-coordinate is out of bounds. The value must be in the range [0, ${width - 1}]."
        }
        require(coordinates.y in 0..<height) {
            "The y-coordinate is out of bounds. The value must be in the range [0, ${height - 1}]."
        }
    }

    protected fun getIndex(coordinates: RectCoordinates) =
        coordinates.y * width + coordinates.x

    protected fun getCoordinates(index: Int) = RectCoordinates(
        x = index % width,
        y = index / width,
    )

    abstract fun update()
}