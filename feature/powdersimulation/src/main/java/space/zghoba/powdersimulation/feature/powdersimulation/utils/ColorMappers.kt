package space.zghoba.powdersimulation.feature.powdersimulation.utils

import kotlin.math.round


/**
 * Converts the simulation's [space.zghoba.powdersimulation.core.model.Color]
 * to a Jetpack Compose [androidx.compose.ui.graphics.Color].
 *
 * RGB components are mapped directly (0-255). The Alpha value (0f-255f) is normalized to 0f-1f
 * and then aggressively rounded to an Int (0 or 1), making the resulting Compose color
 * either fully transparent or fully opaque.
 *
 * @receiver The source simulation color object.
 * @return The corresponding Compose Color.
 */
fun space.zghoba.powdersimulation.core.model.Color.toColor() =
    androidx.compose.ui.graphics.Color(
        red = this.red,
        green = this.green,
        blue = this.blue,
        alpha = round(this.alpha.denormaliseTo(min = 0f, max = 255f)).toInt()
    )