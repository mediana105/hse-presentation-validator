package org.hse.validator.validators.rules

import org.hse.validator.model.Presentation
import org.hse.validator.model.Slide

sealed class SlideRule() : Rule {
    final override fun validate(presentation: Presentation): Boolean {
        return presentation.slides.all { validateSlide(it) }
    }

    abstract fun validateSlide(slide: Slide): Boolean
}
