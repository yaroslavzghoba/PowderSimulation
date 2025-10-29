package space.zghoba.powdersimulation.core.model

data class Board(
    val width: Int,
    val height: Int,
    private val init: (coordinates: RectCoordinates) -> Cell = { _ -> Material.Void },
) {
    private val size = width * height
    private val cells: List<Cell> = List(size = size) { index ->
        init(getCoordinates(index))
    }

    fun getCell(coordinates: RectCoordinates): Cell {
        validateCoordinates(coordinates)

        val index = getIndex(coordinates)
        return cells[index]
    }

    private fun validateCoordinates(coordinates: RectCoordinates) {
        require(coordinates.x in 0..<width) {
            "The x-coordinate is out of bounds. The value must be in the range [0, ${width - 1}]."
        }
        require(coordinates.y in 0..<height) {
            "The y-coordinate is out of bounds. The value must be in the range [0, ${height - 1}]."
        }
    }

    private fun getIndex(coordinates: RectCoordinates) =
        coordinates.y * width + coordinates.x

    private fun getCoordinates(index: Int) = RectCoordinates(
        x = index % width,
        y = index / width,
    )
}