package org.hse.validator.model

import org.apache.poi.xslf.usermodel.XSLFShape
import org.apache.poi.xslf.usermodel.XSLFSlide
import org.apache.poi.xslf.usermodel.XSLFTextShape
import org.jetbrains.annotations.Contract

data class Slide(
    var number: Int = 0,
    var displayedNumber: String? = null,
    var title: String? = null,
    var texts: List<Text>? = null,
    var images: List<Image>? = null,
    var isTitleSlide: Boolean = false,
    val listGroups: List<List<Text>> = emptyList()
) {
    @Contract("_ -> !null")
    fun extractSlideTitle(slide: XSLFSlide): String {
        return slide.shapes
            .stream()
            .filter { shape: XSLFShape? -> shape is XSLFTextShape }
            .map { shape: XSLFShape ->
                (shape as XSLFTextShape)
                    .text
            }.findFirst().orElse("")
    }
}