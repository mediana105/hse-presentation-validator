package org.hse.validator.validators.rules

import org.hse.validator.model.Text
import org.hse.validator.model.TextType
import org.hse.validator.util.FontUtils

// check body text in sans serif fonts
class SansSerifFontRule() : TextRule() {
    override fun message(font: String?): String = "The main text should be sans serif"

    override fun validateText(text: Text): Boolean {
        if (text.contentType != TextType.BODY) return true
        return text.runs.all { run ->
            run.fontFamily?.let { FontUtils.isSansSerif(it) } != false
        }
    }
}

class FontSizeRule(
    private val bodyMin: Double = 14.0,
    private val bodyMax: Double = 22.0,
    private val titleMin: Double = 28.0,
    private val titleMax: Double = 36.0
) : TextRule() {
    override fun message(msg: String?): String =
        "Incorrect font size: headings $titleMin-$titleMax pt, main text $bodyMin-$bodyMax pt"

    override fun validateText(text: Text): Boolean {
        return when (text.contentType) {
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
    }
}
