package org.hse.validator.validators

import org.hse.validator.model.Presentation
import org.hse.validator.rules.PresentationRule
import org.hse.validator.rules.Rule
import org.hse.validator.rules.SlideRule
import org.hse.validator.rules.TextRule
import org.springframework.stereotype.Component

data class ValidationResult(
    val ruleName: String,
    val message: String,
    val details: List<ViolationDetail>
)

data class ViolationDetail(
    val slideNumber: Int?
)

@Component
class Validator() {
    fun validate(presentation: Presentation, rules: List<Rule>): List<ValidationResult> {
        val raw = mutableListOf<Triple<String, String, Int?>>()
        for (rule in rules) {
            when (rule) {
                is PresentationRule -> {
                    if (!rule.validate(presentation)) {
                        raw += Triple(rule.javaClass.simpleName, rule.message(), null)
                    }
                }

                is SlideRule -> {
                    presentation.slides.forEach { slide ->
                        if (!rule.validateSlide(slide)) {
                            raw += Triple(rule.javaClass.simpleName, rule.message(), slide.number)
                        }
                    }
                }

                is TextRule -> {
                    presentation.slides.forEach { slide ->
                        slide.texts?.forEach { text ->
                            if (!rule.validateText(text)) {
                                raw += Triple(rule.javaClass.simpleName, rule.message(""), slide.number)
                            }
                        }
                    }
                }

                else -> {
                }
            }
        }
        return raw
            .groupBy { it.first to it.second }
            .map { (key, group) ->
                val (ruleName, message) = key
                ValidationResult(
                    ruleName = ruleName,
                    message = message,
                    details = group.map { ViolationDetail(it.third) }
                )
            }
    }
}
