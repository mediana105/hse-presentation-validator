package org.hse.validator.rules

import org.hse.validator.model.Presentation

interface Rule {
    val message: String
    fun validate(presentation: Presentation): Boolean
}