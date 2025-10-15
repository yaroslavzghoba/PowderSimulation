package space.zghoba.powdersimulation.core.model.materials

import space.zghoba.powdersimulation.core.model.Color
import space.zghoba.powdersimulation.core.model.Material
import space.zghoba.powdersimulation.core.model.MaterialRule

object Void : Material() {

    override val color: Color
        get() = Color.fromArgb(alpha = 0f, red = 0, green = 0, blue = 0)

    override val rules: List<MaterialRule>
        get() = emptyList()
}