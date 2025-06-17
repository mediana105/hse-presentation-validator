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
            limitSlidesCount(10, 18)
            requireSlideFormat(setOf("16:9", "4:3"))
            requireMandatorySlides(listOf("Введение", "Заключение", "Список литературы"))
            limitColors(3)
        }
        slide {
            requireTitleSlideFields()
            style {
                fonts {
                    requireSansSerifMainText()
                    fontSizeRule(bodyMin = 18.0, bodyMax = 24.0, titleMin = 28.0, titleMax = 32.0)
                    fontStyleCountRule(maxBold = 10, maxItalic = 10, maxUnderline = 3)
                }
                limitFontVariety(3)
                colors {
                    requireContrast(4.5, 3.0)
                }
            }
            header {
                requireHeaderFormat(10)
            }
            lists {
                requireListsHaveBetween(3, 7)
                requireUniformListCapitalization()
                forbidListEndPunctuation()
                forbidSingleItemLists()
            }
            numbering {
                requireSlideNumbering()
            }
            graphics {
                requireTextToImageAreaRatio(70)
            }
            content {
                limitTextPerSlide(maxLines = 10, maxWords = 40)
            }
        }
    }


    private fun courseWorkRules() = presentationRules {
        structure {
            limitSlidesCount(10, 15)
            requireSlideFormat(setOf("16:9", "4:3"))
            requireMandatorySlides(listOf("Введение", "Заключение"))
            limitColors(3)
        }
        slide {
            style {
                fonts {
                    requireSansSerifMainText()
                    fontSizeRule(bodyMin = 18.0, bodyMax = 24.0, titleMin = 28.0, titleMax = 32.0)
                    fontStyleCountRule(maxBold = 10, maxItalic = 10, maxUnderline = 3)
                }
                limitFontVariety(3)
                colors {
                    requireContrast(4.5, 3.0)
                }
            }
            header {
                requireHeaderFormat(10)
            }
            lists {
                requireListsHaveBetween(3, 7)
                requireUniformListCapitalization()
                forbidListEndPunctuation()
                forbidSingleItemLists()
            }
            numbering {
                requireSlideNumbering()
            }
            graphics {
                requireTextToImageAreaRatio(70)
            }
            content {
                limitTextPerSlide(maxLines = 10, maxWords = 40)
            }
        }
    }
}
