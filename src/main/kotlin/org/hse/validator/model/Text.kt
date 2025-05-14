package org.hse.validator.model

import org.apache.poi.sl.usermodel.ColorStyle
import org.hse.validator.util.FontUtils

enum class FontType { SERIF, SANS_SERIF, UNKNOWN }

data class Text(
    var content: String? = null,
    var fontFamily: String? = null,
    var fontSize: Double? = null,
    var isBold: Boolean = false,
    var isItalic: Boolean = false,
    var isUnderline: Boolean = false,
    var textColor: ColorStyle? = null,
    var isBullet: Boolean = false,
    var contentType: TYPE? = null
) {
    val fontType: FontType = when {
        FontUtils.isSerif(fontFamily) -> FontType.SERIF
        FontUtils.isSansSerif(fontFamily) -> FontType.SANS_SERIF
        else -> FontType.UNKNOWN
    }

    override fun toString(): String {
        return content ?: ""
    }
}