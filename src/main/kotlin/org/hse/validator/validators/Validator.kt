package org.hse.validator.validators

import org.hse.validator.model.Presentation
import org.hse.validator.model.Slide
import org.hse.validator.model.Text
import org.hse.validator.validators.rules.*
import org.springframework.stereotype.Component

data class RawViolation(
    val rule: Rule,
    val slide: Slide? = null,
    val text: Text? = null,
    val message: String,
    val extraInfo: String? = null
)

@Component
class Validator() {
    fun validate(presentation: Presentation, rules: List<Rule>): List<RawViolation> {
        val raw = mutableListOf<RawViolation>()
        fun validatePresentationRule(rule: Rule, getInfo: (() -> String)? = null) {
            if (!rule.validate(presentation)) {
                val msg = rule.message(null)
                val info = getInfo?.invoke()
                raw += RawViolation(rule, message = msg, extraInfo = info)
            }
        }

        fun validateSlideRule(rule: SlideRule, getInfo: (Slide) -> String = { "" }) {
            presentation.slides.forEach { slide ->
                if (!rule.validateSlide(slide)) {
                    val msg = rule.message(slide)
                    val info = getInfo(slide)
                    raw += RawViolation(rule, slide, message = msg, extraInfo = info)
                }
            }
        }

        for (rule in rules) {
            when (rule) {
                is MandatorySlidesRule -> validatePresentationRule(rule) { rule.getInfo().joinToString(", ") }
                is SlideFormatRule -> validatePresentationRule(rule) { rule.getInfo() }
                is SlideCountRule -> validatePresentationRule(rule) { rule.getInfo(presentation) }
                is MaxFontVarietyRule -> validatePresentationRule(rule) { rule.getInfo() }
                is MaxColorVarietyRule -> validatePresentationRule(rule) { rule.getInfo() }

                is TooMuchTextRule -> validateSlideRule(rule) { rule.getInfo(it) }
                is ForbidSingleItemListRule -> validateSlideRule(rule) { rule.getInfo() }
                is TitleSlideContentRule -> validateSlideRule(rule) { rule.getInfo() }
                is ListSizeRule -> validateSlideRule(rule) { rule.getInfo() }
                is HeaderFormatRule -> validateSlideRule(rule) { rule.getInfo() }
                is SlideNumberFormatRule -> validateSlideRule(rule) { rule.getInfo() }
                is ForbidListEndPunctuationRule -> validateSlideRule(rule) { rule.getInfo() }
                is UniformListCapitalizationRule -> validateSlideRule(rule) { rule.getInfo() }
                is ContrastRatioRule -> validateSlideRule(rule) { rule.getInfo() }
                is TextToImageAreaRatioRule -> validateSlideRule(rule) { rule.getInfo() }
                is TextStyleCountRule -> validateSlideRule(rule) { rule.getInfo() }

                is SansSerifFontRule -> {
                    presentation.slides.forEach { slide ->
                        slide.texts?.forEach { text ->
                            if (!rule.validateText(text)) {
                                val msg = rule.message(slide)
                                val info = rule.getInfo(text)
                                raw += RawViolation(rule, slide, text, message = msg, extraInfo = info)
                            }
                        }
                    }
                }

                is FontSizeRule -> {
                    presentation.slides.forEach { slide ->
                        slide.texts?.forEach { text ->
                            if (!rule.validateText(text)) {
                                val msg = rule.message(slide)
                                val info = rule.getInfo(text)
                                raw += RawViolation(rule, slide, text, message = msg, extraInfo = info)
                            }
                        }
                    }
                }
            }
        }
        return raw
    }
}

