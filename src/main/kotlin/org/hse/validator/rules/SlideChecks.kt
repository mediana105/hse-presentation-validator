package org.hse.validator.rules

import org.hse.validator.model.Slide
import org.hse.validator.model.TextType
import org.hse.validator.util.getContrastRatio

class TooMuchTextRule(
    private val maxLines: Int = 10,
    private val maxWords: Int = 40
) : SlideRule() {
    override fun message(msg: String?): String = "Too much text on slide (max $maxLines lines or $maxWords words)"
    override fun validateSlide(slide: Slide): Boolean {
        val texts = slide.texts ?: return true
        val lineCount = texts.size
        val wordCount = texts.sumOf { countWords(it.content ?: "") }
        return lineCount <= maxLines && wordCount <= maxWords
    }

    private fun countWords(text: String): Int {
        return text.trim().split("\\s+".toRegex()).count { it.isNotEmpty() }
    }
}

class ForbidSingleItemListRule : SlideRule() {
    override fun message(msg: String?): String = "Avoid lists with only one item"

    override fun validateSlide(slide: Slide): Boolean {
        return slide.listGroups.all { it.size != 1 }
    }
}

class TitleSlideContentRule : SlideRule() {
    override fun message(msg: String?): String =
        "Title slide must contain full student name, supervisor name, project title, and university name"

    override fun validateSlide(slide: Slide): Boolean {
        if (!slide.isTitleSlide) return true

        val textContent = slide.texts?.mapNotNull { it.content?.trim() } ?: return false
        val joined = textContent.joinToString(" ").lowercase()

        val hasStudentName = containsFullName(joined)
        val hasSupervisor = containsSupervisor(joined)
        val hasProjectTitle = containsProjectTitle(joined)
        val hasUniversity = containsUniversityName(joined)

        return hasStudentName && hasSupervisor && hasProjectTitle && hasUniversity
    }

    private fun containsFullName(text: String): Boolean {
        return Regex("""\b[А-ЯЁ][а-яё]+\s+[А-ЯЁ][а-яё]+\s+[А-ЯЁ][а-яё]+\b""").containsMatchIn(text)
    }

    private fun containsSupervisor(text: String): Boolean {
        return text.contains("научн", ignoreCase = true) &&
                Regex("""\b[А-ЯЁ][а-яё]+\s+[А-ЯЁ][а-яё]+(\s+[А-ЯЁ][а-яё]+)?\b""").containsMatchIn(text)
    }

    private fun containsProjectTitle(text: String): Boolean {
        return text.contains("проект", ignoreCase = true) || text.contains("работа", ignoreCase = true)
    }

    private fun containsUniversityName(text: String): Boolean {
        return text.contains("университет", ignoreCase = true) ||
                text.contains("институт", ignoreCase = true)
    }
}

class ListSizeRule(
    private val minItems: Int = 3,
    private val maxItems: Int = 7
) : SlideRule() {
    override fun message(msg: String?): String = "List should have $minItems–$maxItems items"
    override fun validateSlide(slide: Slide): Boolean {
        return slide.listGroups.all { it.size in minItems..maxItems }
    }
}

class HeaderFormatRule(
    private val maxWords: Int = 10
) : SlideRule() {
    override fun message(msg: String?): String = "Header should not end with a dot and should not exceed $maxWords words"
    override fun validateSlide(slide: Slide): Boolean {
        val headers = slide.texts?.filter { it.contentType == TextType.TITLE } ?: return true
        return headers.all { text ->
            val content = text.content?.trim() ?: return@all true
            !content.endsWith(".") && content.split("\\s+".toRegex()).size <= maxWords
        }
    }
}

class SlideNumberFormatRule : SlideRule() {
    override fun message(msg: String?): String = "Slide numbering should be in format X / N (except the title slide)"
    override fun validateSlide(slide: Slide): Boolean {
        return if (slide.isTitleSlide) {
            slide.displayedNumber == null
        } else {
            slide.displayedNumber != null
        }
    }
}

class ForbidListEndPunctuationRule : SlideRule() {
    override fun message(msg: String?): String = "List items should not contain punctuation marks at the end"
    override fun validateSlide(slide: Slide): Boolean {
        return slide.listGroups.all { group ->
            group.all { item ->
                val content = item.content?.trim()
                if (content.isNullOrEmpty()) return@all true
                val endMark = content.last()
                endMark != '.' && endMark != ',' && endMark != ';'
            }
        }
    }
}

class UniformListCapitalizationRule : SlideRule() {
    override fun message(msg: String?): String =
        "All list items within the same group must start with the same case (all uppercase or all lowercase)"

    override fun validateSlide(slide: Slide): Boolean {
        return slide.listGroups.all { group ->
            val startsWithUpper = group
                .mapNotNull { it.content?.trim()?.firstOrNull()?.isUpperCase() }
            startsWithUpper.all { it } || startsWithUpper.all { !it }
        }
    }
}

class ContrastRatioRule(
    private val minContrastForText: Double = 4.5,
    private val minContrastForLargeText: Double = 3.0
) : SlideRule() {
    override fun message(msg: String?): String =
        "Text contrast must be at least $minContrastForText for normal text and $minContrastForLargeText for large text"

    override fun validateSlide(slide: Slide): Boolean {
        val bgColor = slide.backgroundColor ?: return true
        val texts = slide.texts ?: return true

        return texts.all { text ->
            val textColor = text.textColor ?: return@all true
            val fontSize = text.fontSize ?: 0.0
            val contrast = getContrastRatio(textColor, bgColor)
            if (fontSize >= 18.0) {
                contrast >= minContrastForLargeText
            } else {
                contrast >= minContrastForText
            }
        }
    }

}

class TextToImageAreaRatioRule(
    private val maxTextPercent: Int = 70 // например, не более 70% площади текста
) : SlideRule() {
    override fun message(msg: String?): String = "Text must not occupy more than $maxTextPercent% of the content area"

    override fun validateSlide(slide: Slide): Boolean {
        val textArea = slide.texts?.sumOf {
            it.width * it.height
        } ?: 0.0

        val imageArea = slide.images?.sumOf {
            it.width * it.height
        } ?: 0.0

        val totalArea = textArea + imageArea
        if (totalArea == 0.0) return true

        val textPercent = (textArea * 100.0) / totalArea
        return textPercent <= maxTextPercent
    }
}
