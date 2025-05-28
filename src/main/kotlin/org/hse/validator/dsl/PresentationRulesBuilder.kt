package org.hse.validator.dsl

import org.hse.validator.rules.Rule

class PresentationRulesBuilder {
    // all collected rules are passed to the validator
    val collectedRules = mutableListOf<Rule>()

    // allows to add rules specific to each slide
    fun slide(block: SlideRules.() -> Unit) {
        val slideRules = SlideRules().apply(block)
        collectedRules += slideRules.rules
    }
}

// builder for slide level rules
class SlideRules {
    val rules = mutableListOf<Rule>()

    fun rule(r: Rule) {
        rules += r
    }
}

fun presentationRules(block: PresentationRulesBuilder.() -> Unit): List<Rule> {
    val builder = PresentationRulesBuilder().apply(block)
    return builder.collectedRules
}
