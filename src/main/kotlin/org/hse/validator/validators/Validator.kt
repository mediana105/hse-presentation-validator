package org.hse.validator.validators

import org.hse.validator.model.Presentation
import org.hse.validator.model.Slide
import org.hse.validator.model.Text
import org.hse.validator.validators.rules.PresentationRule
import org.hse.validator.validators.rules.Rule
import org.hse.validator.validators.rules.SlideRule
import org.hse.validator.validators.rules.TextRule
import org.springframework.stereotype.Component

data class RawViolation(
    val rule: Rule,
    val slide: Slide? = null,
    val text: Text? = null
)

@Component
class Validator() {
    fun validate(presentation: Presentation, rules: List<Rule>): List<RawViolation> {
        val raw = mutableListOf<RawViolation>()
        for (rule in rules) {
            when (rule) {
                is PresentationRule -> {
                    if (!rule.validate(presentation)) {
                        raw += RawViolation(rule)
                    }
                }

                is SlideRule -> {
                    presentation.slides.forEach { slide ->
                        if (!rule.validateSlide(slide)) {
                            raw += RawViolation(rule, slide)
                        }
                    }
                }

                is TextRule -> {
                    presentation.slides.forEach { slide ->
                        slide.texts?.forEach { text ->
                            if (!rule.validateText(text)) {
                                raw += RawViolation(rule, slide, text)
                            }
                        }
                    }
                }

                else -> {
                }
            }
        }
        return raw
    }
}
