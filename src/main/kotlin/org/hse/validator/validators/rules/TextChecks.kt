package org.hse.validator.validators.rules

import org.hse.validator.model.Slide
import org.hse.validator.model.Text
import org.hse.validator.model.TextType
import org.hse.validator.util.FontUtils

/**
 * Rule that ensures that the body text is written in sans serif fonts.
 */
class SansSerifFontRule : SlideRule() {
    private val violations = mutableListOf<String>()

    override fun validateSlide(slide: Slide): Boolean {
        violations.clear()
        var valid = true
        slide.texts?.forEach { text ->
            if (!validateText(text)) valid = false
        }
        return valid
    }

    fun validateText(text: Text): Boolean {
        if (text.contentType != TextType.BODY) return true

        val badRuns = text.runs.filter { run ->
            run.fontFamily?.let { !FontUtils.isSansSerif(it) } == true
        }

        if (badRuns.isNotEmpty()) {
            badRuns.forEach { run ->
                val font = run.fontFamily ?: "неизвестный шрифт"
                val snippet = text.content.take(50).replace("\n", " ")
                violations.add("\"$snippet\" — шрифт: $font")
            }
            return false
        }
        return true
    }

    override fun message(slide: Slide?): String {
        if (violations.isEmpty()) return "Основной текст должен быть выполнен в шрифте без засечек"
        return buildString {
            append("Найдены тексты с шрифтами с засечками:\n")
            violations.forEach { append("- $it\n") }
        }
    }

    fun getInfo(text: Text): String? {
        val badRuns = text.runs.filter { run ->
            run.fontFamily?.let { !FontUtils.isSansSerif(it) } == true
        }
        if (badRuns.isEmpty()) return null
        val font = badRuns.first().fontFamily ?: "неизвестный шрифт"
        val snippet = text.content.take(50).replace("\n", " ")
        return "\"$snippet\" — шрифт: $font"
    }
}
/**
 * Rule that checks whether the font size for headings and body text is correct.
 */
class FontSizeRule(
    private val bodyMin: Double = 14.0,
    private val bodyMax: Double = 22.0,
    private val titleMin: Double = 28.0,
    private val titleMax: Double = 36.0
) : TextRule() {

    private val violations = mutableListOf<String>()

    override fun validateText(text: Text): Boolean {
        violations.clear()
        val valid = when (text.contentType) {
            TextType.TITLE -> text.runs.all { run ->
                val size = run.fontSize ?: return@all true
                size in titleMin..titleMax
            }
            TextType.BODY -> text.runs.all { run ->
                val size = run.fontSize ?: return@all true
                size in bodyMin..bodyMax
            }
            else -> true
        }

        if (!valid) {
            val expectedRange = when (text.contentType) {
                TextType.TITLE -> "$titleMin - $titleMax pt"
                TextType.BODY -> "$bodyMin - $bodyMax pt"
                else -> "любой размер"
            }
            text.runs.forEach { run ->
                val size = run.fontSize
                if (size != null) {
                    val snippet = text.content.take(50).replace("\n", " ")
                    if ((text.contentType == TextType.TITLE && size !in titleMin..titleMax) ||
                        (text.contentType == TextType.BODY && size !in bodyMin..bodyMax)
                    ) {
                        violations.add("\"$snippet\" — размер: $size pt, ожидается: $expectedRange")
                    }
                }
            }
        }
        return valid
    }

    override fun message(slide: Slide?): String {
        if (violations.isEmpty()) return "Некорректный размер шрифта: заголовки $titleMin-$titleMax pt, основной текст $bodyMin-$bodyMax pt"
        return buildString {
            append("Обнаружены тексты с неправильным размером шрифта:\n")
            violations.forEach { append("- $it\n") }
        }
    }

    override fun getInfo(text: Text): String? {
        return if (violations.isEmpty()) null else violations.joinToString(separator = "\n")
    }
}
