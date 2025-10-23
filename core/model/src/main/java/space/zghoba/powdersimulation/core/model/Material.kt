package space.zghoba.powdersimulation.core.model


/**
 * Material in powder simulation such as sand or water.
 *
 * @param rules A list of rules that apply to the material. Rules order defines their priority.
 * The first rule on the list has the highest priority.
 */
sealed class Material(val rules: List<MaterialRule>, color: Color) : Cell(color) {

    data object Stone : Material(
        rules = emptyList(),
        color = Color.fromArgb(alpha = 1f, red = 136, green = 140, blue = 141),
    )

    data object Sand : Material(
        rules = listOf(
            MaterialRule.FALL_STRAIGHT,
            MaterialRule.SLIDE_DIAGONALLY,
            MaterialRule.SLIDE_LEFT,
            MaterialRule.SLIDE_RIGHT,
        ),
        color = Color.fromArgb(alpha = 1f, red = 242, green = 210, blue = 169),
    )

    data object Water : Material(
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

    data object Void : Material(
        rules = emptyList(),
        color = Color.fromArgb(alpha = 0f, red = 0, green = 0, blue = 0),
    )
}