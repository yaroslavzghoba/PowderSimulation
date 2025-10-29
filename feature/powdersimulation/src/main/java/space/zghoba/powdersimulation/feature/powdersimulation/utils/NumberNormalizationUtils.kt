package space.zghoba.powdersimulation.feature.powdersimulation.utils

import androidx.annotation.FloatRange

/**
 * Normalizes this [Float] value from its source range [[min], [max]] to a
 * fractional value in the range [0.0, 1.0].
 *
 * This function calculates where the receiver value sits proportionally between the
 * minimum and maximum boundaries.
 *
 * @receiver The source value to normalize. The value should ideally be in the range [[min], [max]].
 * @param min The minimum boundary of the source range.
 * @param max The maximum boundary of the source range.
 * @return The normalized [Float] value within the [[0.0, 1.0]] range.
 *
 * @sample normaliseFromSample
 */
internal fun Float.normaliseFrom(min: Float, max: Float): Float {
    return (this - min) / (max - min)
}

/**
 * Scales a fractional value in the range [[0.0, 1.0]] back to a value within
 * the specified range [[min], [max]]. This is the inverse of the normalisation process.
 *
 * @receiver The fractional value to scale. The value should be in the range [[0.0, 1.0]].
 * @param min The minimum value of the target range.
 * @param max The maximum value of the target range.
 * @return The scaled [Float] value within the [[min], [max]] range.
 *
 * @sample denormaliseToSample
 */
internal fun @receiver:FloatRange(from = 0.0, to = 1.0) Float.denormaliseTo(
    min: Float,
    max: Float
): Float {
    return min + (this * (max - min))
}

@Suppress("unused")
private fun normaliseFromSample() {
    // absolute values: 1f        2f                           5f
    //                  +---------+----------------------------+
    // relative values: 0f        0.25f                        1f
    println(2f.normaliseFrom(min = 1f, max = 5f))  // 0.25f

    // absolute values: 0f                 4.5f                9f
    //                  +------------------+-------------------+
    // relative values: 0f                 0.5f                1f
    println(4.5f.normaliseFrom(min = 0f, max = 9f))  // 0.5f

    // absolute values: 10f                            18f     20f
    //                  +------------------------------+-------+
    // relative values: 0f                             0.8f    1f
    println(18f.normaliseFrom(min = 10f, max = 20f))  // 0.8f
}

@Suppress("unused")
private fun denormaliseToSample() {
    // absolute values: 1f        2f                           5f
    //                  +---------+----------------------------+
    // relative values: 0f        0.25f                        1f
    println(0.25f.denormaliseTo(min = 1f, max = 5f))  // 2f

    // absolute values: 0f                 4.5f                9f
    //                  +------------------+-------------------+
    // relative values: 0f                 0.5f                1f
    println(0.50f.denormaliseTo(min = 0f, max = 9f))  // 4.5f

    // absolute values: 10f                            18f     20f
    //                  +------------------------------+-------+
    // relative values: 0f                             0.8f    1f
    println(0.80f.denormaliseTo(min = 10f, max = 20f))  // 18f
}
