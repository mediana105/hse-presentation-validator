package org.hse.validator.parser

import org.apache.poi.sl.usermodel.PaintStyle
import org.apache.poi.sl.usermodel.PaintStyle.SolidPaint
import org.apache.poi.sl.usermodel.Placeholder
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.apache.poi.xslf.usermodel.XSLFPictureShape
import org.apache.poi.xslf.usermodel.XSLFSlide
import org.apache.poi.xslf.usermodel.XSLFTextShape
import org.hse.validator.model.*
import java.awt.Color
import java.awt.geom.Rectangle2D
import java.io.FileInputStream
import java.util.logging.Level
import java.util.logging.Logger

class PptxParser {
    private val logger: Logger = Logger.getLogger(PptxParser::class.java.name)

    @Throws(Exception::class)
    fun parse(filePath: String): Presentation {
        FileInputStream(filePath).use { fis ->
            XMLSlideShow(fis).use { pptx ->
                val presentationSize = pptx.pageSize
                return Presentation(
                    slides = pptx.slides.map { convertSlide(it) },
                    width = presentationSize.width,
                    height = presentationSize.height
                )
            }
        }
    }

    private fun convertSlide(poiSlide: XSLFSlide): Slide {
        val images = poiSlide.shapes.filterIsInstance<XSLFPictureShape>().map { convertImageElement(it) }
        val textElements = poiSlide.shapes.filterIsInstance<XSLFTextShape>().flatMap { convertTextElements(it) }

        val listGroups = detectListGroups(textElements)
        for (listGroup in listGroups) {
            logger.info("List group started: ")
            for (elem in listGroup) {
                logger.info("Item: $elem")
            }
            logger.info("List group ended\n")
        }
        logger.info("Slide number text: ${slideNumberText(poiSlide)}")
        return Slide(
            number = poiSlide.slideNumber,
            displayedNumber = slideNumberText(poiSlide),
            title = Slide().extractSlideTitle(poiSlide),
            texts = textElements,
            images = images,
            isTitleSlide = poiSlide.slideNumber == 1,
            listGroups = listGroups
        )
    }

    private fun slideNumberText(poiSlide: XSLFSlide): String? {
        val placeholderNumber = poiSlide.shapes
            .filterIsInstance<XSLFTextShape>()
            .find { it.textType == Placeholder.SLIDE_NUMBER }
            ?.text
        if (!placeholderNumber.isNullOrBlank()) return placeholderNumber.trim()

        // try to find the lowest text shape corresponding to pattern "X/N"
        val regex = Regex("""\b\d+\s*/\s*\d+\b""")
        val manualNumber = findBottomMostNumberText(poiSlide, regex, poiSlide.slideShow.pageSize.height)

        return manualNumber?.let { regex.find(it)?.value?.trim() }
    }


    private fun convertTextElements(poiText: XSLFTextShape): List<Text> {
        val paragraphs = mutableListOf<Text>()
        for (paragraph in poiText.textParagraphs) {
            var fontName: String? = null
            var fontSize: Double? = null
            var isBold = false
            var isItalic = false
            var textColor: Color? = null
            val isBullet = paragraph.isBullet
            val bulletCharacter = paragraph.bulletCharacter
            val indentLevel = paragraph.indentLevel
            for (run in paragraph.textRuns) {
                if (run.fontFamily != null && fontName == null) fontName = run.fontFamily
                if (run.fontSize != null && fontSize == null) fontSize = run.fontSize
                if (!isBold && run.isBold) isBold = true
                if (!isItalic && run.isItalic) isItalic = true
                if (textColor == null) textColor = extractSolidPaintColor(run.fontColor)
            }

            val contentType = when (poiText.textType) {
                Placeholder.CENTERED_TITLE -> TextType.CENTERED_TITLE
                Placeholder.TITLE -> TextType.TITLE
                Placeholder.SUBTITLE -> TextType.SUBTITLE
                Placeholder.HEADER, Placeholder.FOOTER -> TextType.HEADER
                Placeholder.BODY -> TextType.BODY
                else -> TextType.BODY
            }
            paragraphs.add(
                Text(
                    content = paragraph.text,
                    fontFamily = fontName,
                    fontSize = fontSize,
                    isBold = isBold,
                    isItalic = isItalic,
                    textColor = textColor,
                    contentType = contentType,
                    isBullet = isBullet,
                    bulletCharacter = bulletCharacter,
                    indentLevel = indentLevel
                )
            )
        }
        return paragraphs
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

    private fun extractSolidPaintColor(paintStyle: PaintStyle): Color? {
        try {
            if (paintStyle is SolidPaint) {
                return paintStyle.solidColor.color
            }
            logger.warning("Unsupported paint style: " + paintStyle.javaClass.simpleName)
        } catch (e: Exception) {
            logger.log(Level.WARNING, "Error getting color", e)
        }
        return null
    }

    private fun detectListGroups(texts: List<Text>): List<List<Text>> {
        val listGroups = mutableListOf<MutableList<Text>>()
        val groupStack = ArrayDeque<MutableList<Text>>()
        var previousIndent: Int? = null

        for (text in texts) {
            if (isListItem(text)) {
                val indent = text.indentLevel ?: 0
                while (groupStack.size > 1 && (previousIndent ?: 0) > indent) {

                    logger.info("List item: $text, indent: $indent, previous indent: $previousIndent, group stack: ${groupStack}")
                    groupStack.removeLast()
                }
                if (groupStack.isEmpty() || indent > (previousIndent ?: 0)) {
                    val newGroup = mutableListOf<Text>()
                    listGroups.add(newGroup)
                    groupStack.addLast(newGroup)
                }
                groupStack.last().add(text)
                previousIndent = indent
            } else {
                groupStack.clear()
                previousIndent = null
                continue
            }
        }
        return listGroups
    }

    private fun isListItem(text: Text): Boolean {
        // if the text is bulleted
        if (text.isBullet) {
            return true
        }

        // if the text custom numbering
//        val trimmed = text.content?.trim()
//        val numberedPattern = Regex("""^\(?\d+[.)]""")
//
//        return numberedPattern.containsMatchIn(trimmed.toString())
        return false
    }

    fun getTextShapeVerticalPosition(textShape: XSLFTextShape, slideHeight: Int): String {
        val anchor: Rectangle2D = textShape.anchor
        val y = anchor.y
        return when {
            y < slideHeight / 3 -> "top"
            y < slideHeight * 2 / 3 -> "middle"
            else -> "bottom"
        }
    }

    private fun findBottomMostNumberText(
        poiSlide: XSLFSlide,
        regex: Regex,
        slideHeight: Int
    ): String? {
        // collect all (number text, y coordinate) pairs for text shapes matching the regex
        val candidates = poiSlide.shapes
            .filterIsInstance<XSLFTextShape>()
            .mapNotNull { shape ->
                val text = shape.text ?: return@mapNotNull null
                val match = regex.find(text)?.value?.trim() ?: return@mapNotNull null
                val y = shape.anchor.y
                match to y
            }
        if (candidates.isEmpty()) return null
        val (number, _) = candidates.maxByOrNull { it.second }!!
        return number
    }
}