package space.zghoba.powdersimulation.core.model.materials

import space.zghoba.powdersimulation.core.model.Color
import space.zghoba.powdersimulation.core.model.Material
import space.zghoba.powdersimulation.core.model.MaterialRule

object Stone : Material() {

    override val color: Color
        get() = Color.fromArgb(alpha = 1f, red = 136, green = 140, blue = 141)

    override val rules: List<MaterialRule>
        get() = emptyList()
}