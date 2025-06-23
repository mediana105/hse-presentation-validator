package org.hse.validator.dsl

// PresentationRulesBuilder additions for internal state
class PresentationRulesBuilder {
    var structureRules: StructureRules? = null
    val slideRulesList = mutableListOf<SlideRules>()

    fun slide(block: SlideRules.() -> Unit) {
        val slide = SlideRules().apply(block)
        slideRulesList.add(slide)
    }
}

fun presentationRules(block: PresentationRulesBuilder.() -> Unit): PresentationRulesBuilder {
    return PresentationRulesBuilder().apply(block)
}