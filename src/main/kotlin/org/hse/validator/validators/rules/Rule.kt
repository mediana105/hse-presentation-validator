package org.hse.validator.validators.rules

import org.hse.validator.model.Presentation
import org.hse.validator.model.Slide

/**
 * Base interface for all validation rules.
 * Defines a contract for rules to provide a validation method and an optional message.
 */
sealed interface Rule {
    fun validate(presentation: Presentation): Boolean
    fun message(slide: Slide?): String
}