package org.hse.validator.validators.rules.builder

import org.hse.validator.dsl.PresentationRulesBuilder
import org.hse.validator.validators.rules.*

object RulesBuilder {

    fun build(dsl: PresentationRulesBuilder): List<Rule> {
        val rules = mutableListOf<Rule>()

        dsl.structureRules?.let { struct ->
            struct.slidesCountRange?.let { (min, max) ->
                rules.add(SlideCountRule(min, max))
            }
            struct.requiredFormats?.let { formats ->
                rules.add(SlideFormatRule(formats))
            }
            struct.mandatorySlidesList?.let { names ->
                rules.add(MandatorySlidesRule(names))
            }
            struct.limitColors?.let { maxColor ->
                rules.add(MaxColorVarietyRule(maxColor))
            }
        }

        dsl.slideRulesList.forEach { slideRules ->
            slideRules.requireTitleSlideFields().let {
                rules.add(TitleSlideContentRule())
            }
            slideRules.styleRules?.let { style ->
                // Fonts rules
                style.fontsRules?.let { fonts ->
                    if (fonts.sansSerifRequired) rules.add(SansSerifFontRule())
                    fonts.fontSizeRuleParams?.let { params ->
                        rules.add(FontSizeRule(params.bodyMin, params.bodyMax, params.titleMin, params.titleMax))
                    }
                    fonts.fontStyleCountRuleParams?.let { params ->
                        rules.add(TextStyleCountRule(params.maxBold, params.maxItalic, params.maxUnderline))
                    }
                }

                // Colors rules
                style.colorsRules?.let { colors ->
                    colors.contrastRuleParams?.let { params ->
                        rules.add(ContrastRatioRule(params.minContrastForText, params.minContrastForLargeText))
                    }
                }

                style.limitFontVariety?.let { maxFonts ->
                    rules.add(MaxFontVarietyRule(maxFonts))
                }
            }

            // Header rules
            slideRules.headerRules?.let { header ->
                if (header.headerFormatRequired) {
                    rules.add(HeaderFormatRule(header.maxWordsCount))
                }
            }

            // Lists rules
            slideRules.listsRules?.let { lists ->
                lists.listSizeRange?.let { (min, max) ->
                    rules.add(ListSizeRule(min, max))
                }
                if (lists.uniformCapitalizationRequired) rules.add(UniformListCapitalizationRule())
                if (lists.forbidEndPunctuation) rules.add(ForbidListEndPunctuationRule())
                if (lists.forbidSingleItems) rules.add(ForbidSingleItemListRule())
            }

            // Numbering rules
            slideRules.numberingRules?.let { numbering ->
                if (numbering.slideNumberingRequired) {
                    rules.add(SlideNumberFormatRule())
                }
            }

            // Graphics rules
            slideRules.graphicsRules?.let { graphics ->
                graphics.maxTextPercentage?.let { maxPercent ->
                    rules.add(TextToImageAreaRatioRule(maxPercent))
                }
            }

            // Content rules
            slideRules.contentRules?.let { content ->
                val maxLines = content.maxLinesPerSlide ?: 0
                val maxWords = content.maxWordsPerSlide ?: 0
                rules.add(TooMuchTextRule(maxLines, maxWords))
            }
        }

        return rules
    }
}
