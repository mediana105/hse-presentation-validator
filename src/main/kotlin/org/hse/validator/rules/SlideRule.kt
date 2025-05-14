package org.hse.validator.rules

import org.hse.validator.model.Presentation
import org.hse.validator.model.Slide

sealed class SlideRule : Rule {
    final override fun validate(presentation: Presentation): Boolean {
        return presentation.slides.all { validateSlide(it) }
    }

    abstract fun validateSlide(slide: Slide): Boolean

    protected fun isTitleSlide(slide: Slide): Boolean {
        return slide.isTitleSlide
    }
}

class TooMuchTextRule(
    private val maxLines: Int = 10,
    private val maxWords: Int = 40
) : SlideRule() {
    override val message = "Too much text on slide (max $maxLines lines or $maxWords words)"
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

class TooFewListItemsRule : SlideRule() {
    override val message = "Avoid lists with only one item"

    override fun validateSlide(slide: Slide): Boolean {
        return slide.listGroups.all { it.size != 1 }
    }
}

class TitleSlideContentRule : SlideRule() {
    override val message: String =
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
