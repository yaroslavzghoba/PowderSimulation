package space.zghoba.powdersimulation.core.model


/**
 * Material in powder simulation such as sand or water.
 *
 * @param density The density of the material.
 * @param rules A list of rules that apply to the material. Rules order defines their priority.
 * The first rule on the list has the highest priority.
 * @param color The color in which the material can be displayed.
 */
sealed class Material(
    val density: Float,
    val rules: List<MaterialRule>,
    color: Color,
) : Cell(color) {

    @Suppress("unused")
    data object Iron : Material(
        density = 7.87f,
        rules = emptyList(),
        color = Color.fromArgb(alpha = 1f, red = 82, green = 89, blue = 95)
    )

    @Suppress("unused")
    data object Stone : Material(
        density = 2.8f,
        rules = listOf(
            MaterialRule.FALL_STRAIGHT,
        ),
        color = Color.fromArgb(alpha = 1f, red = 136, green = 140, blue = 141),
    )

    @Suppress("unused")
    data object Sand : Material(
        density = 2.6f,
        rules = listOf(
            MaterialRule.FALL_STRAIGHT,
            MaterialRule.SLIDE_DIAGONALLY,
            MaterialRule.SLIDE_LEFT,
            MaterialRule.SLIDE_RIGHT,
        ),
        color = Color.fromArgb(alpha = 1f, red = 242, green = 210, blue = 169),
    )

    @Suppress("unused")
    data object Water : Material(
        density = 1f,
        rules = listOf(
            MaterialRule.FALL_STRAIGHT,
            MaterialRule.SLIDE_DIAGONALLY,
            MaterialRule.SLIDE_LEFT,
            MaterialRule.SLIDE_RIGHT,
            MaterialRule.FLOW_HORIZONTAL,
            MaterialRule.FLOW_RIGHT,
            MaterialRule.FLOW_LEFT,
        ),
        color = Color.fromArgb(alpha = 1f, red = 35, green = 137, blue = 218),
    )

    @Suppress("unused")
    data object Void : Material(
        density = 0f,
        rules = emptyList(),
        color = Color.fromArgb(alpha = 0f, red = 0, green = 0, blue = 0),
    )

    companion object {
        val all: List<Material>
            get() = Material::class.nestedClasses
                .mapNotNull { it.objectInstance as? Material }
    }
}