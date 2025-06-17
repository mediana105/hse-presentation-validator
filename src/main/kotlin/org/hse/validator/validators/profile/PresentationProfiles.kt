package org.hse.validator.validators.profile

import org.hse.validator.dsl.*
import org.hse.validator.validators.rules.Rule

object PresentationProfiles {
    fun rulesFor(type: String): List<Rule> = when (type.lowercase()) {
        "выпускная квалификационная работа (вкр)" -> finalQualificationWorkRules()
        "курсовая работа" -> courseWorkRules()
        else -> throw IllegalArgumentException("Unknown type: $type")
    }

    private fun finalQualificationWorkRules() = presentationRules {
        structure {
            slidesCountRange = 10 to 18
            requiredFormats(setOf("16:9", "4:3"))
            mandatorySlides(listOf("Введение", "Заключение", "Список литературы"))
            limitColors = 3
        }
        slide {
            requireTitleSlideFields()
            style {
                fonts {
                    sansSerifRequired = true
                    fontSizeRange(bodyMin = 18.0, bodyMax = 24.0, titleMin = 28.0, titleMax = 32.0)
                    maxFontStyle(maxBold = 10, maxItalic = 10, maxUnderline = 3)
                }
                limitFontVariety = 3
                colors {
                    contrastThresholds(4.5, 3.0)
                }
            }
            header {
                requireHeaderFormat(10)
            }
            lists {
                listSizeRange = 3 to 7
                uniformCapitalizationRequired = true
                singleItemsForbidden = true
                endPunctuationForbidden = true
            }
            numbering {
                slideNumberingRequired = true
            }
            graphics {
                maxTextPercentage = 70
            }
            content {
                limitTextPerSlide(maxLines = 10, maxWords = 40)
            }
        }
    }


    private fun courseWorkRules() = presentationRules {
        structure {
            slidesCountRange = 10 to 15
            requiredFormats(setOf("16:9", "4:3"))
            mandatorySlides(listOf("Введение", "Заключение"))
            limitColors = 3
        }
        slide {
            style {
                fonts {
                    sansSerifRequired = true
                    fontSizeRange(bodyMin = 18.0, bodyMax = 24.0, titleMin = 28.0, titleMax = 32.0)
                    maxFontStyle(maxBold = 10, maxItalic = 10, maxUnderline = 3)
                }
                limitFontVariety = 3
                colors {
                    contrastThresholds(4.5, 3.0)
                }
            }
            header {
                requireHeaderFormat(10)
            }
            lists {
                listSizeRange = 3 to 7
                uniformCapitalizationRequired = true
                singleItemsForbidden = true
                endPunctuationForbidden = true
            }
            numbering {
                slideNumberingRequired = true
            }
            graphics {
                maxTextPercentage = 70
            }
            content {
                limitTextPerSlide(maxLines = 10, maxWords = 40)
            }
        }
    }
}
