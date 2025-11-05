package space.zghoba.powdersimulation.core.model

/**
 * Coordinates in two-dimensional space.
 *
 * @param x Coordinate on the X axis.
 * @param y Coordinate on the Y axis.
 */
data class RectCoordinates(val x: Int, val y: Int) {

    override fun toString() = "($x; $y)"
}
