package org.hse.validator.rules

import org.hse.validator.model.Presentation

sealed class PresentationRule : Rule {
    abstract override fun validate(presentation: Presentation): Boolean
}
class SlideCountRule(
    private val minSlides: Int = 10,
    private val maxSlides: Int = 18
) : PresentationRule() {
    override val message = "Slide count should be between $minSlides and $maxSlides"

    override fun validate(presentation: Presentation): Boolean {
        return presentation.slides.size in minSlides..maxSlides
    }
}

class MaxFontVarietyRule : PresentationRule() {
    override val message = "The number of different fonts should not exceed 3"

    override fun validate(presentation: Presentation): Boolean {
        val fonts = presentation.slides
            .flatMap { it.texts ?: emptyList() }
            .mapNotNull { it.fontFamily }
            .toSet()
        return fonts.size <= 3
    }
}