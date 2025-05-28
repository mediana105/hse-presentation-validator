package org.hse.validator.rules

import org.hse.validator.model.Presentation

// checks at the level of presentation
sealed class PresentationRule : Rule {
    abstract override fun validate(presentation: Presentation): Boolean
}

