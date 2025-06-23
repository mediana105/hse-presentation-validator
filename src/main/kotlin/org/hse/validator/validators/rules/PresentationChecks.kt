package org.hse.validator.validators.rules

import org.hse.validator.model.Presentation
import org.hse.validator.model.Slide

/**
 * Rule to check if the number of slides in the presentation
 * is within the specified minimum and maximum bounds.
 */
class SlideCountRule(
    private val minSlides: Int = 10,
    private val maxSlides: Int = 18
) : PresentationRule() {
    override fun message(slide: Slide?): String = "Рекомендуемое количество слайдов должно быть от $minSlides до $maxSlides"

    override fun validate(presentation: Presentation): Boolean {
        return presentation.slides.size in minSlides..maxSlides
    }
    fun getInfo(presentation: Presentation): String = "Получено: ${presentation.slides.size}"

}

/**
 * Rule to check that the number of different fonts
 * used in the presentation does not exceed the allowed maximum.
 */
class MaxFontVarietyRule(private val maxFonts: Int = 3) : PresentationRule() {
    override fun message(slide: Slide?) = "Рекомендованное количество различных шрифтов не должно превышать $maxFonts"

    private var lastFontsCount: Int = 0
    override fun validate(presentation: Presentation): Boolean {
        val fonts = presentation.slides
            .flatMap { it.texts ?: emptyList() }
            .flatMap { it.runs }
            .mapNotNull { it.fontFamily }
            .toSet()
        lastFontsCount = fonts.size
        return lastFontsCount <= maxFonts
    }

    fun getInfo (): String = "Получено: $lastFontsCount"
}

/**
 * Rule to check that the number of different text colors
 * used in the presentation does not exceed the allowed maximum.
 */
class MaxColorVarietyRule(private val maxColor: Int) : PresentationRule() {
    private var lastCount: Int = 0

    override fun message(slide: Slide?): String = "Рекомендуемое количество различных цветов не должно превышать $maxColor"

    override fun validate(presentation: Presentation): Boolean {
        val allTexts = presentation.slides.flatMap { it.texts ?: emptyList() }
        val uniqueColors = allTexts.flatMap { it.runs }
            .mapNotNull { it.textColor }
            .toSet()
        lastCount = uniqueColors.size
        return lastCount <= maxColor
    }

    fun getInfo(): String {
        return "Получено: $lastCount"
    }
}

/**
 * Rule to check that the slide format matches one of the allowed formats.
 * Typically checks for aspect ratios like 16:9 or 4:3.
 */
class SlideFormatRule(private val allowedFormats: Set<String>) : PresentationRule() {
    private var lastDetectedFormat: String? = null
    override fun message(slide: Slide?): String = "Рекомендуется использовать один из следующих форматов слайдов: ${allowedFormats.joinToString(", ")}"
    override fun validate(presentation: Presentation): Boolean {
        lastDetectedFormat = detectFormat(presentation)
        return allowedFormats.contains(lastDetectedFormat)
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

    fun getInfo(): String = "Получено: " + (lastDetectedFormat ?: "Неизвестный формат")
}

/**
 * Rule to check that all required slides (by their titles) are present in the presentation.
 */
class MandatorySlidesRule(private val requiredTitles: List<String>) : PresentationRule() {
    private var missingTitles: List<String> = mutableListOf()

    override fun message(slide: Slide?): String =
        "В презентации отсутствуют рекомендованные слайды:"

    override fun validate(presentation: Presentation): Boolean {
        val titles = presentation.slides.mapNotNull { it.title?.trim() }
        missingTitles = requiredTitles.filter { required ->
            titles.none { it.contains(required, ignoreCase = true) }
        }
        println(missingTitles)
        return missingTitles.isEmpty()
    }
    fun getInfo(): List<String> = missingTitles
}
