package org.hse.validator.rules

import org.hse.validator.model.Presentation
import org.hse.validator.model.Text

// checks at the level of one text
sealed class TextRule : Rule {
    abstract fun validateText(text: Text): Boolean

    final override fun validate(presentation: Presentation): Boolean {
        return presentation.slides.all { slide ->
            slide.texts?.all { text -> validateText(text) } ?: true
        }
    }
}
