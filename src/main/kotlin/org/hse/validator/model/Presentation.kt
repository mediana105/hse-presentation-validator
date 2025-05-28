package org.hse.validator.model

data class Presentation(
    val slides: List<Slide> = emptyList(),
    val width: Int? = null,
    val height: Int? = null
) {
    private val titleSlidesCount: Int
        get() = slides.count { it.isTitleSlide }
}