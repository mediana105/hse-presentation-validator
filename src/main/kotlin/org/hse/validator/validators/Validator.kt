package org.hse.validator.validators

import org.hse.validator.model.Presentation
import org.hse.validator.model.Slide
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
                                results += ValidationResult(rule, rule.message(text.content), slide.number)
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
fun printValidationReport(
    results: List<ValidationResult>,
    slides: List<Slide>
) {
    // Presentation-level errors (где slideNumber == null)
    val globalErrors = results.filter { it.slideNumber == null }
        .groupingBy { it.message }.eachCount()
    if (globalErrors.isNotEmpty()) {
        println("Presentation-level errors:")
        globalErrors.forEach { (msg, count) ->
            println("- $msg${if (count > 1) " ($count times)" else ""}")
        }
        println()
    }

    // Слайды по возрастанию
    val slidesByNumber = slides.associateBy { it.number }
    val slideErrors = results.filter { it.slideNumber != null }
        .groupBy { it.slideNumber }
        .toSortedMap(compareBy { it })

    for ((slideNumber, errs) in slideErrors) {
        val slide = slidesByNumber[slideNumber]
        println("Slide $slideNumber (${slide?.title ?: "no title"}):")
        val textSummary = slide?.texts?.joinToString(" ") { it.content ?: "" }?.take(80)
        if (!textSummary.isNullOrBlank()) println("  Text: $textSummary")
        val grouped = errs.groupingBy { it.message }.eachCount()
        for ((msg, count) in grouped) {
            println("  - $msg${if (count > 1) " ($count times)" else ""}")
        }
        println()
    }
}
