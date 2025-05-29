package org.hse.validator.rules

import org.hse.validator.model.Text
import org.hse.validator.model.TextType
import org.hse.validator.util.FontUtils

// check body text in sans serif fonts
class SansSerifFontRule : TextRule() {
    override val message: String = "The main text should be sans serif."

    override fun validateText(text: Text): Boolean {
        if (text.contentType != TextType.BODY) return true
        return FontUtils.isSansSerif(text.fontFamily)
    }
}

class FontSizeRule(private val bodyMin: Double = 14.0,
                   private val bodyMax: Double = 22.0,
                   private val titleMin: Double = 28.0,
                   private val titleMax: Double = 36.0) : TextRule() {
    override val message = "Incorrect font size: headings $titleMin-$titleMax pt, main text $bodyMin-$bodyMax pt"
    override fun validateText(text: Text): Boolean {
        val size = text.fontSize ?: return false
        return when (text.contentType) {
            TextType.TITLE -> size in titleMin..titleMax
            TextType.BODY -> size in bodyMin..bodyMax
            else -> true
        }
    }
}