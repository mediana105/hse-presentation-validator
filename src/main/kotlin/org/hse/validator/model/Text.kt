package org.hse.validator.model

import org.hse.validator.util.FontUtils
import java.awt.Color

enum class FontType { SERIF, SANS_SERIF, UNKNOWN }
enum class TextType {
    TITLE,
    SUBTITLE,
    HEADER,
    FOOTER,
    SLIDE_NUMBER,
    BODY,
    LIST_ITEM,
    OTHER
}

data class Text(
    var content: String? = null,
    var fontFamily: String? = null,
    var fontSize: Double? = null,
    var isBold: Boolean = false,
    var isItalic: Boolean = false,
    var isUnderline: Boolean = false,
    var textColor: Color? = null,
    var contentType: TextType? = null,
    val bulletCharacter: String? = null,
    val width: Double = 0.0,
    val height: Double = 0.0,
) {
    val fontType: FontType
        get() = when {
            FontUtils.isSerif(fontFamily) -> FontType.SERIF
            FontUtils.isSansSerif(fontFamily) -> FontType.SANS_SERIF
            else -> FontType.UNKNOWN
        }

    override fun toString(): String {
        return content ?: ""
    }
}