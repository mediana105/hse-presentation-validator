package org.hse.validator.dsl

// structure section
class StructureRules {
    var slidesCountRange: Pair<Int, Int>? = null
    var requiredFormats: Set<String>? = null
    var mandatorySlidesList: List<String>? = null
    var limitColors: Int? = null

    fun limitSlidesCount(min: Int, max: Int) {
        slidesCountRange = min to max
    }

    fun requireSlideFormat(formats: Set<String>) {
        requiredFormats = formats
    }

    fun requireMandatorySlides(names: List<String>) {
        mandatorySlidesList = names
    }

    fun limitColors(maxColor: Int) {
        limitColors = maxColor
    }
}

fun PresentationRulesBuilder.structure(block: StructureRules.() -> Unit) {
    val structureRules = StructureRules().apply(block)
    this.structureRules = structureRules
}

// style and design section
class StyleRules {
    var fontsRules: FontsRules? = null
    var colorsRules: ColorsRules? = null

    var limitFontVariety: Int? = null

    fun limitFontVariety(maxFonts: Int) {
        limitFontVariety = maxFonts
    }

    fun fonts(block: FontsRules.() -> Unit) {
        fontsRules = FontsRules().apply(block)
    }

    fun colors(block: ColorsRules.() -> Unit) {
        colorsRules = ColorsRules().apply(block)
    }
}

fun SlideRules.style(block: StyleRules.() -> Unit) {
    styleRules = StyleRules().apply(block)
}

// fonts subsection
class FontsRules {
    var sansSerifRequired: Boolean = false
    var fontSizeRuleParams: FontSizeRuleParams? = null
    var fontStyleCountRuleParams: FontStyleCountRuleParams? = null

    fun requireSansSerifMainText() {
        sansSerifRequired = true
    }

    fun fontSizeRule(
        bodyMin: Double = 14.0,
        bodyMax: Double = 22.0,
        titleMin: Double = 28.0,
        titleMax: Double = 36.0
    ) {
        fontSizeRuleParams = FontSizeRuleParams(bodyMin, bodyMax, titleMin, titleMax)
    }

    fun fontStyleCountRule(maxBold: Int, maxItalic: Int, maxUnderline: Int) {
        fontStyleCountRuleParams = FontStyleCountRuleParams(maxBold, maxItalic, maxUnderline)
    }
}

data class FontSizeRuleParams(
    val bodyMin: Double, val bodyMax: Double,
    val titleMin: Double, val titleMax: Double
)

data class FontStyleCountRuleParams(
    val maxBold: Int, val maxItalic: Int, val maxUnderline: Int
)

// colors subsection
class ColorsRules {
    var contrastRuleParams: ContrastRuleParams? = null

    fun requireContrast(minContrastForText: Double = 4.5, minContrastForLargeText: Double = 3.0) {
        contrastRuleParams = ContrastRuleParams(minContrastForText, minContrastForLargeText)
    }
}

data class ContrastRuleParams(
    val minContrastForText: Double,
    val minContrastForLargeText: Double
)

// headers section
class HeaderRules {
    var headerFormatRequired: Boolean = false
    var maxWordsCount: Int = 10

    fun requireHeaderFormat(maxWords: Int = 10) {
        headerFormatRequired = true
        maxWordsCount = maxWords
    }
}

fun SlideRules.header(block: HeaderRules.() -> Unit) {
    headerRules = HeaderRules().apply(block)
}

// lists section
class ListsRules {
    var listSizeRange: Pair<Int, Int>? = null
    var uniformCapitalizationRequired: Boolean = false
    var forbidEndPunctuation: Boolean = false
    var forbidSingleItems: Boolean = false

    fun requireListsHaveBetween(min: Int, max: Int) {
        listSizeRange = min to max
    }

    fun requireUniformListCapitalization() {
        uniformCapitalizationRequired = true
    }

    fun forbidListEndPunctuation() {
        forbidEndPunctuation = true
    }

    fun forbidSingleItemLists() {
        forbidSingleItems = true
    }
}

fun SlideRules.lists(block: ListsRules.() -> Unit) {
    listsRules = ListsRules().apply(block)
}

class NumberingRules {
    var slideNumberingRequired: Boolean = false

    fun requireSlideNumbering() {
        slideNumberingRequired = true
    }
}

fun SlideRules.numbering(block: NumberingRules.() -> Unit) {
    numberingRules = NumberingRules().apply(block)
}

// graphics section
class GraphicsRules {
    var maxTextPercentage: Int? = null

    fun requireTextToImageAreaRatio(maxTextPercent: Int = 70) {
        maxTextPercentage = maxTextPercent
    }
}

fun SlideRules.graphics(block: GraphicsRules.() -> Unit) {
    graphicsRules = GraphicsRules().apply(block)
}

// content section
class ContentRules {
    var maxLinesPerSlide: Int? = null
    var maxWordsPerSlide: Int? = null

    fun limitTextPerSlide(maxLines: Int = 10, maxWords: Int = 10) {
        maxLinesPerSlide = maxLines
        maxWordsPerSlide = maxWords
    }
}

fun SlideRules.content(block: ContentRules.() -> Unit) {
    contentRules = ContentRules().apply(block)
}

// SlideRules additions for internal state
class SlideRules {
    var styleRules: StyleRules? = null
    var headerRules: HeaderRules? = null
    var listsRules: ListsRules? = null
    var numberingRules: NumberingRules? = null
    var graphicsRules: GraphicsRules? = null
    var contentRules: ContentRules? = null
    fun requireTitleSlideFields() = {
    }
}
