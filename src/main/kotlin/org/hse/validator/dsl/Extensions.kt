package org.hse.validator.dsl

import org.hse.validator.rules.*

fun PresentationRulesBuilder.limitSlidesCount(min: Int, max: Int) {
    collectedRules += SlideCountRule(min, max)
}

fun PresentationRulesBuilder.limitFontVariety(maxFonts: Int) {
    collectedRules += MaxFontVarietyRule(maxFonts)
}

fun PresentationRulesBuilder.limitTextStyles(maxBold: Int, maxItalic: Int, maxUnderline: Int) {
    collectedRules += TextStyleCountRule(maxBold, maxItalic, maxUnderline)
}

fun PresentationRulesBuilder.limitColorsUsed(maxColor: Int) {
    collectedRules += MaxColorVarietyRule(maxColor)
}

fun PresentationRulesBuilder.requireSlideFormat(formats: Set<String>) {
    collectedRules += SlideFormatRule(formats)
}

fun PresentationRulesBuilder.requireMandatorySlides(vararg names: String) {
    collectedRules += MandatorySlidesRule(names.toList())
}

fun SlideRules.requireTitleSlideFields() {
    rule(
        TitleSlideContentRule(
        )
    )
}