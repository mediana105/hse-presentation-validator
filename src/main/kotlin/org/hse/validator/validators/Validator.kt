package org.hse.validator.validators

import org.hse.validator.model.Presentation
import org.hse.validator.rules.PresentationRule
import org.hse.validator.rules.Rule
import org.hse.validator.rules.SlideRule
import org.hse.validator.rules.TextRule
import org.springframework.stereotype.Component

data class ValidationResult(
    val rule: Rule,
    val message: String,
    val slideNumber: Int? = null
)

@Component
class Validator() {
    fun validate(presentation: Presentation, rules: List<Rule>): List<ValidationResult> {
        val results = mutableListOf<ValidationResult>()
        for (rule in rules) {
            when (rule) {
                is PresentationRule -> {
                    if (!rule.validate(presentation)) {
                        results += ValidationResult(rule, rule.message())
                    }
                }

                is SlideRule -> {
                    presentation.slides.forEach { slide ->
                        if (!rule.validateSlide(slide)) {
                            results += ValidationResult(rule, rule.message(), slide.number)
                        }
                    }
                }

                is TextRule -> {
                    presentation.slides.forEach { slide ->
                        slide.texts?.forEach { text ->
                            if (!rule.validateText(text)) {
                                results += ValidationResult(
                                    rule,
                                    rule.message(text.contentType.toString() + " " + text.fontSize),
                                    slide.number
                                )
                            }
                        }
                    }
                }

                else -> {
                }
            }
        }
        return results
    }

}
