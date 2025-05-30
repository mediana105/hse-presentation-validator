package org.hse.validator


import org.hse.validator.dsl.*
import org.hse.validator.parser.PptxParser
import java.io.File

fun main() {
    val parser = PptxParser()
    val workingDir = System.getProperty("user.dir")
    val separator = File.separator
    val presentation = parser.parse("$workingDir${separator}presentations${separator}Example.pptx")

    val rules = presentationRules {
        structure {
            limitSlidesCount(10, 15)
            requireSlideFormat(setOf("16:9", "4:3"))
            requireMandatorySlides(listOf("Введение", "Заключение", "Список литературы"))
        }
        slide {
            style {
                fonts {
                    requireSansSerifMainText()
                    fontSizeRule(bodyMin = 14.0, bodyMax = 22.0, titleMin = 28.0, titleMax = 36.0)
                }
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
                limitTextPerSlide(10, 40)
            }
        }
    }


    println("Configured rules:")
    rules.forEach { println(it.message) }


    File("rules_debug.txt").printWriter().use { out ->
        rules.forEach { out.println(it.message) }
    }

    println("Parsed presentation: ${presentation.slides.size} slides")
}
