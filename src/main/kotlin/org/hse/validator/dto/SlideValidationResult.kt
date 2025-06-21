package org.hse.validator.dto

import org.hse.validator.validators.RawViolation

data class GroupedByRuleError(
    val ruleMessage: String,
    val errorFragments: List<String?>
)

data class SlideErrorsByRule(
    val slideNumber: Int?,
    val groups: List<GroupedByRuleError>
)

fun List<RawViolation>.groupBySlideAndRule(): List<SlideErrorsByRule> =
    this.groupBy { it.slide }.map { (slide, violations) ->
        val byRule = violations.groupBy { it.rule.message(it.slide) }
            .map { (ruleMessage, ruleViolations) ->
                GroupedByRuleError(
                    ruleMessage = ruleMessage,
                    errorFragments = ruleViolations.map { v -> v.text?.content }
                )
            }
        SlideErrorsByRule(
            slideNumber = slide?.number,
            groups = byRule
        )
    }.sortedBy { it.slideNumber ?: -1 }