package org.hse.validator.dsl

import org.hse.validator.rules.*

// structure section
class StructureRules(val presentationRulesBuilder: PresentationRulesBuilder) {
    fun limitSlidesCount(min: Int, max: Int) = presentationRulesBuilder.limitSlidesCount(min, max)
    fun requireSlideFormat(formats: Set<String>) = presentationRulesBuilder.requireSlideFormat(formats)
    fun requireMandatorySlides(names: List<String>) = presentationRulesBuilder.requireMandatorySlides(names)
}

fun PresentationRulesBuilder.structure(block: StructureRules.() -> Unit) {
    StructureRules(this).apply(block)
}

// style and design section
class StyleRules(internal val slideRules: SlideRules)

fun SlideRules.style(block: StyleRules.() -> Unit) {
    StyleRules(this).apply(block)
}

// fonts subsection
class FontsRules(private val slideRules: SlideRules) {
    fun requireSansSerifMainText() = slideRules.requireSansSerifMainText()
    fun fontSizeRule(bodyMin: Double = 14.0, bodyMax: Double = 22.0, titleMin: Double = 28.0, titleMax: Double = 36.0) =
        slideRules.fontSizeRule(bodyMin, bodyMax, titleMin, titleMax)
    fun SlideRules.fontStyleCountRule(maxBold: Int, maxItalic: Int, maxUnderline: Int) {
        rule(TextStyleCountRule(maxBold, maxItalic, maxUnderline))
    }
}

fun StyleRules.fonts(block: FontsRules.() -> Unit) {
    FontsRules(slideRules).apply(block)
}

// colors subsection
class ColorsRules(private val slideRules: SlideRules) {
    fun requireContrast(minContrastForText: Double = 4.5, minContrastForLargeText: Double = 3.0) =
        slideRules.requireContrast(minContrastForText, minContrastForLargeText)
}

fun StyleRules.colors(block: ColorsRules.() -> Unit) {
    ColorsRules(slideRules).apply(block)
}

// headers section
class HeaderRules(internal val slideRules: SlideRules) {
    fun requireHeaderFormat() = slideRules.requireHeaderFormat()
}

fun SlideRules.header(block: HeaderRules.() -> Unit) {
    HeaderRules(this).apply(block)
}

// lists section
class ListsRules(private val slideRules: SlideRules) {
    fun requireListsHaveBetween(min: Int, max: Int) = slideRules.requireListsHaveBetween(min, max)
    fun requireUniformListCapitalization() = slideRules.requireUniformListCapitalization()
    fun forbidListEndPunctuation() = slideRules.forbidListEndPunctuation()
    fun forbidSingleItemLists() = slideRules.forbidSingleItemLists()
}

fun SlideRules.lists(block: ListsRules.() -> Unit) {
    ListsRules(this).apply(block)
}

class NumberingRules(private val slideRules: SlideRules) {
    fun requireSlideNumbering() = slideRules.requireSlideNumbering()
}

// numbering section
fun SlideRules.numbering(block: NumberingRules.() -> Unit) {
    NumberingRules(this).apply(block)
}

// graphics section
class GraphicsRules(private val slideRules: SlideRules) {
    fun requireTextToImageAreaRatio(maxTextPercent: Int = 70) = slideRules.requireTextToImageAreaRatio(maxTextPercent)
}

fun SlideRules.graphics(block: GraphicsRules.() -> Unit) {
    GraphicsRules(this).apply(block)
}

// content section
class ContentRules(private val slideRules: SlideRules) {
    fun limitTextPerSlide(maxLines: Int = 10, maxWords: Int = 10) = slideRules.limitTextPerSlide(maxLines, maxWords)
}

fun SlideRules.content(block: ContentRules.() -> Unit) {
    ContentRules(this).apply(block)
}

// check for the number of slides
fun PresentationRulesBuilder.limitSlidesCount(min: Int, max: Int) {
    collectedRules += SlideCountRule(min, max)
}

// check for the number of fonts
fun PresentationRulesBuilder.limitFontVariety(maxFonts: Int) {
    collectedRules += MaxFontVarietyRule(maxFonts)
}


// check for the number of colors used in a presentation
fun PresentationRulesBuilder.limitColorsUsed(maxColor: Int) {
    collectedRules += MaxColorVarietyRule(maxColor)
}

// check for slide format: 16:9 (recommended for final qualification work) or 4:3
fun PresentationRulesBuilder.requireSlideFormat(formats: Set<String>) {
    collectedRules += SlideFormatRule(formats)
}

// check for the presence of mandatory slides for the final qualifying work
fun PresentationRulesBuilder.requireMandatorySlides(names: List<String>) {
    collectedRules += MandatorySlidesRule(names.toList())
}

// check the title slide for required fields
fun SlideRules.requireTitleSlideFields() {
    rule(TitleSlideContentRule())
}

// check the font of the main text (should be sans serif)
fun SlideRules.requireSansSerifMainText() {
    rule(SansSerifFontRule())
}

// font size check: 18-24 pt for body text, 28-32 pt for headings
fun SlideRules.fontSizeRule(
    bodyMin: Double = 14.0, bodyMax: Double = 22.0, titleMin: Double = 28.0, titleMax: Double = 36.0
) {
    rule(FontSizeRule(bodyMin, bodyMax, titleMin, titleMax))
}

// check the amount of text on the slide: recommended no more than 10 lines, 40 words
fun SlideRules.limitTextPerSlide(maxLines: Int = 10, maxWords: Int = 40) {
    rule(TooMuchTextRule(maxLines, maxWords))
}

// prohibition of use single-item lists
fun SlideRules.forbidSingleItemLists() {
    rule(ForbidSingleItemListRule())
}

// check lists for content of 3-7 items
fun SlideRules.requireListsHaveBetween(min: Int = 3, max: Int = 7) {
    rule(ListSizeRule(min, max))
}

// check titles for the absence of dots at the end and for containing no more than 6-10 words
fun SlideRules.requireHeaderFormat(maxWords: Int = 10) {
    rule(HeaderFormatRule(maxWords))
}

// check for displaying slide number in X/N format
// (exception: slide number should not be displayed on the title (first) slide)
fun SlideRules.requireSlideNumbering() {
    rule(SlideNumberFormatRule())
}

// prohibition of use of ; and . at the end of the list items
fun SlideRules.forbidListEndPunctuation() {
    rule(ForbidListEndPunctuationRule())
}

// check for consistency of style: all items start with a capital or lowercase letter
fun SlideRules.requireUniformListCapitalization() {
    rule(UniformListCapitalizationRule())
}

// contrast ratio test 4.5:1 for normal text and 3:1 for large text
fun SlideRules.requireContrast(
    minContrastForText: Double = 4.5,
    minContrastForLargeText: Double = 3.0
) {
    rule(ContrastRatioRule(minContrastForText, minContrastForLargeText))
}

// check for the text/image ratio
fun SlideRules.requireTextToImageAreaRatio(maxTextPercent: Int = 70) {
    rule(TextToImageAreaRatioRule(maxTextPercent))
}
