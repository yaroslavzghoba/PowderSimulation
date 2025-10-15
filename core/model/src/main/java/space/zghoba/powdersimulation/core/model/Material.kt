package space.zghoba.powdersimulation.core.model


/**
 * Material in powder simulation such as sand or water.
 */
abstract class Material : Cell {

    /**
     * A list of rules that apply to the material. The rules should be in order of priority.
     * The first rule on the list has the highest priority.
     */
    abstract val rules: List<MaterialRule>
}