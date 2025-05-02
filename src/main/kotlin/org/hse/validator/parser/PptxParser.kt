package org.hse.validator.parser

import org.apache.poi.sl.usermodel.ColorStyle
import org.apache.poi.sl.usermodel.PaintStyle
import org.apache.poi.sl.usermodel.PaintStyle.SolidPaint
import org.apache.poi.sl.usermodel.Placeholder
import org.apache.poi.xslf.usermodel.*
import org.hse.validator.model.*
import java.io.FileInputStream
import java.util.logging.Level
import java.util.logging.Logger
import java.util.stream.Collectors

class PptxParser {
    private val logger: Logger = Logger.getLogger(PptxParser::class.java.name)

    @Throws(Exception::class)
    fun parse(filePath: String): Presentation {
        FileInputStream(filePath).use { fis ->
            XMLSlideShow(fis).use { pptx ->
                return Presentation(
                    slides = pptx.slides.map { poiSlide ->
                        convertSlide(poiSlide)
                    }
                )
            }
        }
    }

    private fun convertSlide(poiSlide: XSLFSlide): Slide {
        val textElements = poiSlide.shapes
            .stream()
            .filter { shape: XSLFShape? -> shape is XSLFTextShape }
            .map { shape: XSLFShape -> convertTextElement(shape as XSLFTextShape) }
            .toList()

        val images = poiSlide.shapes
            .stream()
            .filter { shape: XSLFShape? -> shape is XSLFPictureShape }
            .map { shape: XSLFShape -> convertImageElement(shape as XSLFPictureShape) }
            .collect(Collectors.toList())

        return Slide(
            number = poiSlide.slideNumber,
            title = Slide().extractSlideTitle(poiSlide),
            texts = textElements,
            images = images,
            isTitleSlide = poiSlide.slideNumber == 1
        )
    }


    private fun convertTextElement(poiText: XSLFTextShape): Text {
        // Устанавливаем значения по умолчанию
        var fontName: String? = null
        var fontSize: Double? = null
        var isBold = false
        var isItalic = false
        var textColor: ColorStyle? = null

        if (poiText.textParagraphs.isNotEmpty()) {
            val firstParagraph = poiText.textParagraphs[0]
            if (firstParagraph.textRuns.isNotEmpty()) {
                val firstRun = firstParagraph.textRuns[0]
                fontName = firstRun.fontFamily
                fontSize = firstRun.fontSize
                isBold = firstRun.isBold
                isItalic = firstRun.isItalic
                textColor = extractSolidPaintColor(firstRun.fontColor)
            }
        }

        val contentType = when (poiText.textType) {
            Placeholder.CENTERED_TITLE -> TYPE.CENTERED_TITLE
            Placeholder.TITLE -> TYPE.TITLE
            Placeholder.SUBTITLE -> TYPE.SUBTITLE
            Placeholder.HEADER, Placeholder.FOOTER -> TYPE.HEADER
            Placeholder.BODY -> TYPE.BODY
            else -> TYPE.BODY
        }

        logger.fine("Text: ${poiText.text}\n")

        return Text(
            content = poiText.text,
            fontName = fontName,
            fontSize = fontSize,
            isBold = isBold,
            isItalic = isItalic,
            textColor = textColor,
            contentType = contentType
        )
    }

    private fun convertImageElement(pictureShape: XSLFPictureShape): Image {
        val pictureData = pictureShape.pictureData
        val anchor = pictureShape.anchor

        return Image(
            data = pictureData.data.toList(),
            fileName = pictureData.fileName,
            type = pictureData.type,
            width = anchor.width.toInt(),
            height = anchor.height.toInt()
        )
    }

    private fun extractSolidPaintColor(paintStyle: PaintStyle): ColorStyle? {
        try {
            if (paintStyle is SolidPaint) {
                return paintStyle.solidColor
            }
            logger.warning("Unsupported paint style: " + paintStyle.javaClass.simpleName)
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Error getting color", e)
        }
        return null
    }
}