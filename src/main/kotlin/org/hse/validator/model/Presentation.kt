package org.hse.validator.model

data class Presentation(
    val slides: List<Slide> = emptyList()
) {
    private val titleSlidesCount: Int
        get() = slides.count { it.isTitleSlide }
}