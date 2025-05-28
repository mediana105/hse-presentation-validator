package org.hse.validator.rules

import org.hse.validator.model.Presentation

// base interface for checks
interface Rule {
    val message: String
    fun validate(presentation: Presentation): Boolean
}