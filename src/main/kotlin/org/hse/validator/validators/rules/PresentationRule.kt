package org.hse.validator.validators.rules

import org.hse.validator.model.Presentation

/**
 * Base abstract rule class that defines validation logic at the presentation level.
 * All presentation-level rules should inherit from this class and implement the validate method.
 */
sealed class PresentationRule : Rule {
    abstract override fun validate(presentation: Presentation): Boolean
}
