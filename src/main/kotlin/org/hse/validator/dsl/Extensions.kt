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
    rule(TitleSlideContentRule())
}

fun SlideRules.requireSansSerifMainText() {
    rule(SansSerifFontRule())
}

fun SlideRules.requireFontSizeForHeadingsAndBody() {
    rule(FontSizeRule())
}

fun SlideRules.fontSizeRule(
    bodyMin: Double = 14.0, bodyMax: Double = 22.0, titleMin: Double = 28.0, titleMax: Double = 36.0
) {
    rule(FontSizeRule(bodyMin, bodyMax, titleMin, titleMax))
}

fun SlideRules.limitTextPerSlide(maxLines: Int = 10, maxWords: Int = 40) {
    rule(TooMuchTextRule(maxLines, maxWords))
}

fun SlideRules.forbidSingleItemLists() {
    rule(ForbidSingleItemListRule())
}

fun SlideRules.requireListsHaveBetween(min: Int = 3, max: Int = 7) {
    rule(ListSizeRule(min, max))
}

fun SlideRules.forbidDotInHeader(maxWords: Int = 10) {
    rule(HeaderFormatRule(maxWords))
}

fun SlideRules.requireSlideNumbering() {
    rule(SlideNumberFormatRule())
}

fun SlideRules.forbidListEndPunctuation() {
    rule(ForbidListEndPunctuationRule())
}

fun SlideRules.requireUniformListCapitalization() {
    rule(UniformListCapitalizationRule())
}

