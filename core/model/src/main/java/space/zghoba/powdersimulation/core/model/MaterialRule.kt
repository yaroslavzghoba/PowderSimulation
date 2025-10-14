package space.zghoba.powdersimulation.core.model

/**
 * Rules that define the behavior of materials.
 */
enum class MaterialRule {
    FALL_STRAIGHT,
    SLIDE_DIAGONALLY,
    SLIDE_LEFT,
    SLIDE_RIGHT,
    FLOW_HORIZONTAL,
    FLOW_LEFT,
    FLOW_RIGHT,
}