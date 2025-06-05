package org.hse.validator.rules

import org.hse.validator.model.Presentation

// base interface for checks
interface Rule {
    fun message(msg: String? = null): String
    fun validate(presentation: Presentation): Boolean
}