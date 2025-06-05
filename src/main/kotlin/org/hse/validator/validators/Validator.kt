package org.hse.validator.validators

import org.hse.validator.model.Presentation
import org.hse.validator.rules.PresentationRule
import org.hse.validator.rules.Rule
import org.hse.validator.rules.SlideRule
import org.hse.validator.rules.TextRule

data class ValidationResult(
    val rule: Rule,
    val message: String,
    val slideNumber: Int? = null
)

class Validator(private val rules: List<Rule>) {
    fun validate(presentation: Presentation): List<ValidationResult> {
        val results = mutableListOf<ValidationResult>()
        for (rule in rules) {
            when (rule) {
                is PresentationRule -> {
                    if (!rule.validate(presentation)) {
                        results += ValidationResult(rule, rule.message)
                    }
                }

                is SlideRule -> {
                    presentation.slides.forEach { slide ->
                        if (!rule.validateSlide(slide)) {
                            results += ValidationResult(rule, rule.message, slide.number)
                        }
                    }
                }

                is TextRule -> {
                    presentation.slides.forEach { slide ->
                        slide.texts?.forEach { text ->
                            if (!rule.validateText(text)) {
                                results += ValidationResult(rule, rule.message, slide.number)
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
