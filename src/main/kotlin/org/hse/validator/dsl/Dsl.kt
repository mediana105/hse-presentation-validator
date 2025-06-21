package org.hse.validator.dsl

// Structure section: rules related to slide deck structure
class StructureRules {
    var slidesCountRange: Pair<Int, Int>? = null // allowed slide count range (min, max)
    var requiredFormats: Set<String>? = null // allowed slide formats
    var mandatorySlides: List<String>? = null // list of mandatory slide names
    var limitColors: Int? = null // max number of colors allowed

    fun requiredFormats(formats: Set<String>) {
        requiredFormats = formats
    }

    fun mandatorySlides(names: List<String>) {
        mandatorySlides = names
    }
}

fun PresentationRulesBuilder.structure(block: StructureRules.() -> Unit) {
    val structureRules = StructureRules().apply(block)
    this.structureRules = structureRules
}

// Style and design section: visual style related rules
class StyleRules {
    var fonts: FontsRules? = null
    var colors: ColorsRules? = null

    var limitFontVariety: Int? = null // max number of different fonts

    fun fonts(block: FontsRules.() -> Unit) {
        fonts = FontsRules().apply(block)
    }

    fun colors(block: ColorsRules.() -> Unit) {
        colors = ColorsRules().apply(block)
    }
}

fun SlideRules.style(block: StyleRules.() -> Unit) {
    styleRules = StyleRules().apply(block)
}

// Fonts subsection: font-related constraints
class FontsRules {
    var sansSerifRequired: Boolean = false // whether sans-serif font is mandatory
    var fontSizeRange: FontSizeRuleParams? = null // allowed font size ranges
    var fontStyleRange: FontStyleCountRuleParams? = null // max count for bold, italic, underline styles

    fun fontSizeRange(
        bodyMin: Double = 14.0,
        bodyMax: Double = 22.0,
        titleMin: Double = 28.0,
        titleMax: Double = 36.0
    ) {
        fontSizeRange = FontSizeRuleParams(bodyMin, bodyMax, titleMin, titleMax)
    }

    fun maxFontStyle(maxBold: Int, maxItalic: Int, maxUnderline: Int) {
        fontStyleRange = FontStyleCountRuleParams(maxBold, maxItalic, maxUnderline)
    }
}

data class FontSizeRuleParams(
    val bodyMin: Double, val bodyMax: Double,
    val titleMin: Double, val titleMax: Double
)

data class FontStyleCountRuleParams(
    val maxBold: Int, val maxItalic: Int, val maxUnderline: Int
)

// Colors subsection: color contrast rules
class ColorsRules {
    var contrastThresholds: ContrastRuleParams? = null // contrast thresholds for text and large text

    fun contrastThresholds(minContrastForText: Double = 4.5, minContrastForLargeText: Double = 3.0) {
        contrastThresholds = ContrastRuleParams(minContrastForText, minContrastForLargeText)
    }
}

data class ContrastRuleParams(
    val minContrastForText: Double,
    val minContrastForLargeText: Double
)

// Headers section: rules for slide headers
class HeaderRules {
    var headerFormatRequired: Boolean = false // is header format mandatory?
    var maxWordsCount: Int = 10 // max allowed words in header

    fun requireHeaderFormat(maxWords: Int = 10) {
        headerFormatRequired = true
        maxWordsCount = maxWords
    }
}

fun SlideRules.header(block: HeaderRules.() -> Unit) {
    headerRules = HeaderRules().apply(block)
}

// Lists section: rules for lists formatting
class ListsRules {
    var listSizeRange: Int? = null // allowed size range for lists
    var uniformCapitalizationRequired: Boolean = false // uniform capitalization required?
    var endPunctuationForbidden: Boolean = false // forbid punctuation at end of list items
    var singleItemsForbidden: Boolean = false // forbid single-item lists
}

fun SlideRules.lists(block: ListsRules.() -> Unit) {
    listsRules = ListsRules().apply(block)
}

// Numbering section: slide numbering rules
class NumberingRules {
    var slideNumberingRequired: Boolean = false // is slide numbering required?
}

fun SlideRules.numbering(block: NumberingRules.() -> Unit) {
    numberingRules = NumberingRules().apply(block)
}

// Graphics section: rules about images and text ratio
class GraphicsRules {
    var maxTextPercentage: Int? = null // max allowed text area percentage on images
}

fun SlideRules.graphics(block: GraphicsRules.() -> Unit) {
    graphicsRules = GraphicsRules().apply(block)
}

// Content section: rules about slide content limits
class ContentRules {
    var maxLinesPerSlide: Int? = null // max lines per slide
    var maxWordsPerSlide: Int? = null // max words per slide

    fun limitTextPerSlide(maxLines: Int = 10, maxWords: Int = 10) {
        maxLinesPerSlide = maxLines
        maxWordsPerSlide = maxWords
    }
}

fun SlideRules.content(block: ContentRules.() -> Unit) {
    contentRules = ContentRules().apply(block)
}

// SlideRules: container for all slide-specific rules
class SlideRules {
    var styleRules: StyleRules? = null
    var headerRules: HeaderRules? = null
    var listsRules: ListsRules? = null
    var numberingRules: NumberingRules? = null
    var graphicsRules: GraphicsRules? = null
    var contentRules: ContentRules? = null
    fun requireTitleSlideFields() = {} // placeholder for title slide checks
}
