package org.hse.validator.validators.rules

import org.hse.validator.model.Slide
import org.hse.validator.model.Text
import org.hse.validator.model.TextType
import org.hse.validator.util.getContrastRatio

/**
 * Rule to check if the slide does not contain too much text:
 * maximum allowed lines and words.
 */
class TooMuchTextRule(
    private val maxLines: Int = 10,
    private val maxWords: Int = 40
) : SlideRule() {
    override fun message(slide: Slide?): String {
        return "Превышен рекомендуемый объем текста на слайде (максимум $maxLines строк, $maxWords слов)"
    }

    override fun validateSlide(slide: Slide): Boolean {
        val texts = slide.texts ?: return true
        val currentLines = texts.size
        val currentWords = texts.sumOf { countWords(it.content) }
        return currentLines <= maxLines && currentWords <= maxWords
    }

    fun getInfo(slide: Slide): String {
        val texts = slide.texts ?: return "Нет текста"
        val currentLines = texts.size
        val currentWords = texts.sumOf { countWords(it.content) }
        return "Текущий объем: $currentLines строк, $currentWords слов"
    }

    private fun countWords(text: String): Int {
        return text.trim().split("\\s+".toRegex()).count { it.isNotEmpty() }
    }
}


/**
 * Rule forbidding lists that contain only one item.
 */
class ForbidSingleItemListRule : SlideRule() {
    private var violatingLists: List<List<Text>> = emptyList()

    override fun message(slide: Slide?): String {
        return "Найдены списки с одним элементом"
    }

    override fun validateSlide(slide: Slide): Boolean {
        violatingLists = slide.listGroups.filter { it.size == 1 }
        return violatingLists.isEmpty()
    }

    fun getInfo(): String {
        val contents = violatingLists.mapNotNull { it.firstOrNull()?.content }
        return contents.joinToString(", ")
    }
}


/**
 * Rule to check that the title slide contains:
 * full student name, supervisor name, project title, and university name.
 */
class TitleSlideContentRule : SlideRule() {
    private var missingItems: List<String> = emptyList()

    override fun message(slide: Slide?): String {
        return "Отсутствуют рекомендованные элементы титульного слайда"
    }

    override fun validateSlide(slide: Slide): Boolean {
        missingItems = emptyList()
        if (!slide.isTitleSlide) return true

        val textContent = slide.texts?.mapNotNull { it.content.trim() } ?: return false
        val joined = textContent.joinToString(" ")
        val missing = mutableListOf<String>()
        if (!containsFullName(joined)) missing.add("полное имя студента")
        if (!containsSupervisor(joined)) missing.add("имя руководителя")
        if (!containsUniversityName(joined)) missing.add("название университета")

        missingItems = missing
        return missing.isEmpty()
    }

    fun getInfo(): String {
        return missingItems.joinToString(", ")
    }

    private fun containsFullName(text: String): Boolean {
        return Regex("""([А-ЯЁ][а-яё]+(?:\s+[А-ЯЁ][а-яё]+){2})""").containsMatchIn(text)
    }

    private fun containsSupervisor(text: String): Boolean {
        return text.contains("научн", ignoreCase = true) &&
                Regex("""([А-ЯЁ][а-яё]+(?:\s+[А-ЯЁ][а-яё]+){2})""").containsMatchIn(text)
    }

    private fun containsUniversityName(text: String): Boolean {
        return text.contains("университет", ignoreCase = true) ||
                text.contains("институт", ignoreCase = true) ||
                text.contains("школа", ignoreCase = true)
    }
}


/**
 * Rule that checks if lists have a number of items within
 * specified minimum and maximum bounds.
 */
class ListSizeRule(
    private val maxItems: Int = 7
) : SlideRule() {
    private var violatingLists: List<List<Text>> = emptyList()

    override fun message(slide: Slide?): String {
        return "Обнаружены списки с более чем $maxItems пунктами"
    }

    override fun validateSlide(slide: Slide): Boolean {
        violatingLists = slide.listGroups.filter { it.size > maxItems }
        return violatingLists.isEmpty()
    }

    fun getInfo(): String {
        return violatingLists.joinToString("; ") { list ->
            val size = list.size
            val preview = list.take(2).joinToString(", ") { it.content }
            "[$size]: $preview..."
        }
    }
}

/**
 * Rule that validates header formatting: should not end with a dot and have limited number of words.
 */
class HeaderFormatRule(
    private val maxWords: Int = 10
) : SlideRule() {

    private var reasons: List<String> = emptyList()
    private var headerContent: String? = null

    override fun message(slide: Slide?): String =
        "Заголовок нарушает формат"

    override fun validateSlide(slide: Slide): Boolean {
        val header = slide.texts?.firstOrNull { it.contentType == TextType.TITLE } ?: return true

        val content = header.content.trim()
        val endsWithDot = content.endsWith(".")
        val tooLong = content.split("\\s+".toRegex()).size > maxWords

        val currentReasons = mutableListOf<String>()
        if (endsWithDot) currentReasons.add("заканчивается точкой")
        if (tooLong) currentReasons.add("слишком длинный (${content.split("\\s+".toRegex()).size} слов)")

        reasons = currentReasons
        headerContent = content

        return reasons.isEmpty()
    }

    fun getInfo(): String {
        return headerContent?.let { content ->
            if (reasons.isNotEmpty())
                "Заголовок: \"$content\" — ${reasons.joinToString(", ")}"
            else ""
        } ?: ""
    }
}

/**
 * Rule that validates slide numbering format.
 * Title slide should not have a slide number displayed.
 * All other slides must have a slide number in the format "X / N".
 */
class SlideNumberFormatRule : SlideRule() {
    private var info: String? = null

    override fun validateSlide(slide: Slide): Boolean {
        info = null
        return if (slide.isTitleSlide) {
            val valid = slide.displayedNumber == null
            if (!valid) info = "Титульный слайд содержит номер: ${slide.displayedNumber}"
            valid
        } else {
            val valid = slide.displayedNumber != null
            if (!valid) info = "Слайд без номера"
            valid
        }
    }

    override fun message(slide: Slide?): String {
        return if (slide!!.isTitleSlide) {
            "Нумерация на титульном слайде запрещена"
        } else {
            "Нумерация обязательна на всех слайдах, кроме титульного"
        }
    }

    fun getInfo(): String = info ?: "Сообщение не задано"
}

/**
 * Rule that prohibits punctuation marks from being placed at the end of a list item on a slide.
 * Checks that no list item ends with ".", "," or ";".
 */
class ForbidListEndPunctuationRule(
    private val maxItemsToShow: Int = 3
) : SlideRule() {

    private var invalidItems: List<String> = emptyList()

    override fun validateSlide(slide: Slide): Boolean {
        val violations = mutableListOf<String>()

        slide.listGroups.forEach { group ->
            group.forEach { item ->
                val content = item.content.trim()
                if (content.isNotEmpty()) {
                    val endMark = content.last()
                    if (endMark == ';') {
                        violations.add(content)
                    }
                }
            }
        }
        invalidItems = violations.take(maxItemsToShow)
        return violations.isEmpty()
    }

    override fun message(slide: Slide?): String {
        return "В элементах списка рекомендуется не ставить знаки препинания в конце"
    }

    fun getInfo(): String = if (invalidItems.isEmpty()) {
        ""
    } else {
        invalidItems.joinToString(separator = "; ") { "\"$it\"" }
    }
}


/**
 * Rule that checks that all list items in a group start with the same case:
 * either all capitalized, or all lowercase.
 */
class UniformListCapitalizationRule(
    private val maxItemsToShow: Int = 3
) : SlideRule() {

    private var invalidItems: List<String> = emptyList()

    override fun validateSlide(slide: Slide): Boolean {
        val violations = mutableListOf<String>()

        slide.listGroups.forEach { group ->
            if (group.isNotEmpty()) {
                val firstCases = group.mapNotNull {
                    it.content.trim().firstOrNull()?.isUpperCase()
                }
                val allUpper = firstCases.all { it }
                val allLower = firstCases.all { !it }
                if (!allUpper && !allLower) {
                    val firstIsUpper = firstCases.first()
                    group.forEach { item ->
                        val content = item.content.trim()
                        val startsUpper = content.firstOrNull()?.isUpperCase() == true
                        if (startsUpper != firstIsUpper) {
                            violations.add(content)
                        }
                    }
                }
            }
        }
        invalidItems = violations.take(maxItemsToShow)
        return violations.isEmpty()
    }

    override fun message(slide: Slide?): String {
        return "Все пункты списка в группе должны начинаться с одинакового регистра — все с заглавной или все со строчной буквы"
    }

    fun getInfo(): String =
        if (invalidItems.isEmpty()) ""
        else invalidItems.joinToString(separator = "; ") { "\"$it\"" }
}

/**
 * Rule for checking the contrast of text relative to the background.
 * For regular text, the minimum contrast is $minContrastForText,
 * for large text (font size >= 18) — $minContrastForLargeText.
 */
class ContrastRatioRule(
    private val minContrastForText: Double = 4.5,
    private val minContrastForLargeText: Double = 3.0
) : SlideRule() {

    private var minObservedContrast: Double? = null

    override fun validateSlide(slide: Slide): Boolean {
        val bgColor = slide.backgroundColor ?: return true
        val texts = slide.texts ?: return true

        var minContrastFound = Double.MAX_VALUE
        var violationFound = false

        texts.forEach { text ->
            text.runs.forEach { run ->
                if (text.contentType != TextType.BODY) return@forEach
                val textColor = run.textColor ?: return@forEach
                val fontSize = run.fontSize ?: 0.0
                val contrast = getContrastRatio(textColor, bgColor)
                val requiredContrast = if (fontSize >= 18.0) minContrastForLargeText else minContrastForText
                if (contrast < requiredContrast) {
                    violationFound = true
                    if (contrast < minContrastFound) {
                        minContrastFound = contrast
                        minContrastFound = contrast
                    }
                }
            }
        }

        minObservedContrast = if (violationFound) minContrastFound else null
        return !violationFound
    }

    override fun message(slide: Slide?): String {
        return "Контраст текста ниже нормы (минимально допустимый: $minContrastForText для обычного текста и $minContrastForLargeText для крупного)."
    }

    fun getInfo(): String =
        if (minObservedContrast == null) ""
        else "Минимально найденный коэффициент контрастности: ${"%.2f".format(minObservedContrast)}"
}

/**
 * A rule that checks that the text does not take up more than $maxTextPercent% of the slide content area.
 * The message displays the current percentage of area occupied by text.
 */
class TextToImageAreaRatioRule(
    private val maxTextPercent: Int = 70
) : SlideRule() {

    private var actualTextPercent: Double? = null

    override fun validateSlide(slide: Slide): Boolean {
        val textArea = slide.texts?.sumOf { it.width * it.height } ?: 0.0
        val imageArea = slide.images?.sumOf { it.width * it.height } ?: 0.0
        val totalArea = textArea + imageArea
        if (totalArea == 0.0) return true

        val textPercent = (textArea * 100.0) / totalArea
        actualTextPercent = textPercent
        return textPercent <= maxTextPercent
    }

    override fun message(slide: Slide?): String {
        return "Текст занимает слишком большую часть площади слайда (максимум $maxTextPercent%)."
    }

    fun getInfo(): String =
        actualTextPercent?.let { "Текущий процент текста: ${"%.1f".format(it)}%" } ?: ""
}

/**
 * A rule that checks the amount of highlighted text:
 * bold, italic, and underlined.
 * Limits their maximum number on a slide.
 */
class TextStyleCountRule(
    private val maxBold: Int,
    private val maxItalic: Int,
    private val maxUnderline: Int,
    private val maxSamples: Int = 5
) : SlideRule() {

    private var boldCount = 0
    private var italicCount = 0
    private var underlineCount = 0

    private var boldSamples: List<String> = emptyList()
    private var italicSamples: List<String> = emptyList()
    private var underlineSamples: List<String> = emptyList()

    override fun validateSlide(slide: Slide): Boolean {
        val texts = slide.texts ?: return true
        val runs = texts.flatMap { it.runs }

        val boldRuns = runs.filter { it.isBold }
        val italicRuns = runs.filter { it.isItalic }
        val underlineRuns = runs.filter { it.isUnderlined }

        boldCount = boldRuns.size
        italicCount = italicRuns.size
        underlineCount = underlineRuns.size

        boldSamples = boldRuns.take(maxSamples).map { it.content.toString() }
        italicSamples = italicRuns.take(maxSamples).map { it.content.toString() }
        underlineSamples = underlineRuns.take(maxSamples).map { it.content.toString() }

        return boldCount <= maxBold && italicCount <= maxItalic && underlineCount <= maxUnderline
    }

    override fun message(slide: Slide?): String {
        fun formatSamples(samples: List<String>) =
            samples.joinToString(", ") { "\"${it.trim().take(30)}${if (it.length > 30) "…" else ""}\"" }

        return buildString {
            append("Количество выделенного текста превышено:\n")
            append("Жирный — $boldCount (макс. $maxBold): ${formatSamples(boldSamples)}\n")
            append("Курсив — $italicCount (макс. $maxItalic): ${formatSamples(italicSamples)}\n")
            append("Подчеркнутый — $underlineCount (макс. $maxUnderline): ${formatSamples(underlineSamples)}")
        }
    }

    fun getInfo(): String {
        return "Жирный: $boldCount/$maxBold, Курсив: $italicCount/$maxItalic, Подчеркнутый: $underlineCount/$maxUnderline"
    }
}

