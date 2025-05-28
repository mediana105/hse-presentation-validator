package org.hse.validator.rules

import org.hse.validator.model.Text
import org.hse.validator.model.TextType
import org.hse.validator.util.FontUtils


class SansSerifFontRule : TextRule() {
    override val message: String = "The main text should be sans serif."

    override fun validateText(text: Text): Boolean {
        if (text.contentType != TextType.BODY) return true
        return FontUtils.isSansSerif(text.fontFamily)
    }
}

class FontSizeRule : TextRule() {
    override val message = "Incorrect font size: headings 28-36pt, main text 14-22pt"
    override fun validateText(text: Text): Boolean {
        val size = text.fontSize ?: return false
        return when (text.contentType) {
            TextType.TITLE -> size in 28.0..36.0
            TextType.BODY -> size in 14.0..22.0
            else -> true
        }
    }
}