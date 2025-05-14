package org.hse.validator.parser

import org.apache.poi.sl.usermodel.ColorStyle
import org.apache.poi.sl.usermodel.PaintStyle
import org.apache.poi.sl.usermodel.PaintStyle.SolidPaint
import org.apache.poi.sl.usermodel.Placeholder
import org.apache.poi.xslf.usermodel.*
import org.hse.validator.model.*
import org.hse.validator.util.FontUtils
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

        val listGroups = mutableListOf<MutableList<Text>>()
        var currentList: MutableList<Text>? = null
        for (text in textElements) {
            if (text.isBullet) {
                if (currentList == null) {
                    currentList = mutableListOf()
                    listGroups.add(currentList)
                }
                currentList.add(text)
            } else {
                currentList = null
            }
        }

        return Slide(
            number = poiSlide.slideNumber,
            title = Slide().extractSlideTitle(poiSlide),
            texts = textElements,
            images = images,
            isTitleSlide = poiSlide.slideNumber == 1,
            listGroups = listGroups
        )
    }


    private fun convertTextElement(poiText: XSLFTextShape): Text {
        var fontName: String? = null
        var fontSize: Double? = null
        var isBold = false
        var isItalic = false
        var textColor: ColorStyle? = null
        val isBullet = poiText.textParagraphs.any { it.isBullet }


        for (paragraph in poiText.textParagraphs) {
            for (run in paragraph.textRuns) {
                if (run.fontFamily != null && fontName == null) fontName = run.fontFamily
                if (run.fontSize != null && fontSize == null) fontSize = run.fontSize
                if (!isBold && run.isBold) isBold = true
                if (!isItalic && run.isItalic) isItalic = true
                if (textColor == null) textColor = extractSolidPaintColor(run.fontColor)
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
            fontFamily = fontName,
            fontSize = fontSize,
            isBold = isBold,
            isItalic = isItalic,
            textColor = textColor,
            contentType = contentType,
            isBullet = isBullet,
            isSerif = FontUtils.isSerif(fontName),
            isSansSerif = FontUtils.isSansSerif(fontName)
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