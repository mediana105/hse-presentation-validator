package org.hse.validator.dsl

import org.hse.validator.rules.Rule

class PresentationRulesBuilder {
    val collectedRules = mutableListOf<Rule>()

    fun slide(block: SlideRules.() -> Unit) {
        val slideRules = SlideRules().apply(block)
        collectedRules += slideRules.rules
    }
}

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
