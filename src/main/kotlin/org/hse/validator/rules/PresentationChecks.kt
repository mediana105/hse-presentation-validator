package org.hse.validator.rules

import org.hse.validator.model.Presentation

class SlideCountRule(
    private val minSlides: Int = 10,
    private val maxSlides: Int = 18
) : PresentationRule() {
    override val message = "Slide count should be between $minSlides and $maxSlides"

    override fun validate(presentation: Presentation): Boolean {
        return presentation.slides.size in minSlides..maxSlides
    }
}

class MaxFontVarietyRule(private val maxFonts: Int = 3) : PresentationRule() {
    override val message = "The number of different fonts should not exceed $maxFonts"

    override fun validate(presentation: Presentation): Boolean {
        val fonts = presentation.slides
            .flatMap { it.texts ?: emptyList() }
            .mapNotNull { it.fontFamily }
            .toSet()
        return fonts.size <= maxFonts
    }
}

class TextStyleCountRule(
    private val maxBold: Int,
    private val maxItalic: Int,
    private val maxUnderline: Int
) : PresentationRule() {
    override val message: String
        get() = "The amount of selected text has been exceeded: bold ≤ $maxBold, italic ≤ $maxItalic, underline ≤ $maxUnderline"

    override fun validate(presentation: Presentation): Boolean {
        val allTexts = presentation.slides.flatMap { it.texts ?: emptyList() }
        val boldCount = allTexts.count { it.isBold }
        val italicCount = allTexts.count { it.isItalic }
        val underlineCount = allTexts.count { it.isUnderline }
        return boldCount <= maxBold && italicCount <= maxItalic && underlineCount <= maxUnderline
    }
}

class MaxColorVarietyRule(private val maxColor: Int) : PresentationRule() {
    override val message: String
        get() = "The number of different colors should not exceed $maxColor"

    override fun validate(presentation: Presentation): Boolean {
        val allTexts = presentation.slides.flatMap { it.texts ?: emptyList() }
        val uniqueColors = allTexts
            .mapNotNull { it.textColor }
            .toSet()
        return uniqueColors.size <= maxColor
    }
}


// check for compliance with the specified format (16:9 or 4:3)
class SlideFormatRule(private val allowedFormats: Set<String>) : PresentationRule() {
    override val message = "Slide format must be one of: ${allowedFormats.joinToString(", ")}"
    override fun validate(presentation: Presentation): Boolean {
        val format = detectFormat(presentation)
        return allowedFormats.contains(format)
    }

    private fun detectFormat(presentation: Presentation): String {
        val width = presentation.width
        val height = presentation.height
        if (width == null || height == null) return "Unknown"
        val ratio = width.toDouble() / height.toDouble()
        return when (ratio) {
            in 1.75..1.79 -> "16:9"
            in 1.32..1.35 -> "4:3"
            else -> "Other"
        }
    }

}

// checks for required slides
class MandatorySlidesRule(private val requiredTitles: List<String>) : PresentationRule() {
    override val message = "Presentation must contain slides: ${requiredTitles.joinToString(", ")}"
    override fun validate(presentation: Presentation): Boolean {
        val titles = presentation.slides.mapNotNull { it.title?.trim() }
        return requiredTitles.all { required ->
            titles.any { it.contains(required) }
        }
    }
}
