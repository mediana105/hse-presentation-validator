package org.hse.validator.model

import org.apache.poi.sl.usermodel.ColorStyle

data class Text(
    var content: String? = null,
    var fontName: String? = null,
    var fontSize: Double? = null,
    var isBold: Boolean = false,
    var isItalic: Boolean = false,
    var isUnderline: Boolean = false,
    var textColor: ColorStyle? = null,
    var isListItem: Boolean = false,
    var contentType: TYPE? = null
) {
    override fun toString(): String {
        return content ?: ""
    }
}