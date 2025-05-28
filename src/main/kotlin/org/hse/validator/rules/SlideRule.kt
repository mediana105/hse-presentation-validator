package org.hse.validator.rules

import org.hse.validator.model.Presentation
import org.hse.validator.model.Slide

sealed class SlideRule : Rule {
    final override fun validate(presentation: Presentation): Boolean {
        return presentation.slides.all { validateSlide(it) }
    }

    abstract fun validateSlide(slide: Slide): Boolean

    protected fun isTitleSlide(slide: Slide): Boolean {
        return slide.isTitleSlide
    }
}

