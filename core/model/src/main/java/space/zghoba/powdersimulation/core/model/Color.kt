package space.zghoba.powdersimulation.core.model

class Color private constructor(
    val alpha: Float,
    val red: Int,
    val green: Int,
    val blue: Int,
) {

    companion object {
        /**
         * Define the color using the ARGB color model,
         * i.e., using the values of transparency (alpha), red, green, and blue.
         *
         * @param alpha The value of transparency. The value must be in the range from 0.0 to 1.0.
         * @param red The value of red. The value must be in the range from 0 to 255.
         * @param green The value of green. The value must be in the range from 0 to 255.
         * @param blue The value of blue. The value must be in the range from 0 to 255.
         *
         * @return Color initialized using corresponding values.
         *
         * @throws IllegalArgumentException If any of the passed values are outside
         * the specified range.
         */
        fun fromArgb(alpha: Float, red: Int, green: Int, blue: Int): Color {
            validateArgb(alpha, red, green, blue)
            return Color(alpha, red, green, blue)
        }

        private fun validateArgb(a: Float, r: Int, g: Int, b: Int) {
            require(a in 0f..1f) {
                "Invalid alpha value: $a. The value must be in the range [0.0, 1.0]."
            }
            require(r in 0..255) {
                "Invalid red value: $r. The value must be in the range [0, 255]."
            }
            require(g in 0..255) {
                "Invalid green value: $g. The value must be in the range [0, 255]."
            }
            require(b in 0..255) {
                "Invalid blue value: $b. The value must be in the range [0, 255]."
            }
        }
    }
}