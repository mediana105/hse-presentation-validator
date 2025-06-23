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
        val byRule = violations.groupBy { it.message }
            .map { (ruleMessage, ruleViolations) ->
                GroupedByRuleError(
                    ruleMessage = ruleMessage,
                    errorFragments = ruleViolations.map { violation ->
                        val content = violation.text?.content ?: ""
                        val extra = violation.extraInfo ?: ""
                        content + extra
                    }
                )
            }
        SlideErrorsByRule(
            slideNumber = slide?.number,
            groups = byRule
        )
    }.sortedBy { it.slideNumber ?: -1 }
