package space.zghoba.powdersimulation.core.model

/**
 * Defines the possible movement behaviors for materials in the simulation.
 * These rules dictate how a material attempts to move from its current position
 * during a simulation step.
 */
enum class MaterialMovementRule {

    /** Fall straight down. */
    FALL_STRAIGHT,

    /** Slide down to the left or right if both places are free. */
    SLIDE_DIAGONALLY,

    /** Slide down and to the left only. */
    SLIDE_LEFT,

    /** Slide down and to the right only. */
    SLIDE_RIGHT,

    /** Flow horizontally to the left or right if both places are free. */
    FLOW_HORIZONTAL,

    /** Flow horizontally to the left only. */
    FLOW_LEFT,

    /** Flow horizontally to the right only. */
    FLOW_RIGHT,
}