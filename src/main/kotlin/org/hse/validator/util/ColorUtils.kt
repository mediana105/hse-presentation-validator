package org.hse.validator.util

import java.awt.Color
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow

// WCAG 2.1 standard
fun getContrastRatio(color1: Color, color2: Color): Double {
    fun toLinear(c: Int): Double {
        val sRGB = c / 255.0
        return if (sRGB <= 0.03928) sRGB / 12.92 else ((sRGB + 0.055) / 1.055).pow(2.4)
    }

    fun luminance(c: Color): Double {
        val r = toLinear(c.red)
        val g = toLinear(c.green)
        val b = toLinear(c.blue)
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    val lum1 = luminance(color1)
    val lum2 = luminance(color2)
    val lighter = max(lum1, lum2)
    val darker = min(lum1, lum2)
    return (lighter + 0.05) / (darker + 0.05)
}