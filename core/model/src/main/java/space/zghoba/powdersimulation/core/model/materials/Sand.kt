package space.zghoba.powdersimulation.core.model.materials

import space.zghoba.powdersimulation.core.model.Color
import space.zghoba.powdersimulation.core.model.Material
import space.zghoba.powdersimulation.core.model.MaterialRule

object Sand : Material() {

    override val color: Color
        get() = Color.fromArgb(alpha = 1f, red = 242, green = 210, blue = 169)

    override val rules: List<MaterialRule>
        get() = listOf(
            MaterialRule.FALL_STRAIGHT,
            MaterialRule.SLIDE_DIAGONALLY,
            MaterialRule.SLIDE_LEFT,
            MaterialRule.SLIDE_RIGHT,
        )
}