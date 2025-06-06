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
                val slideHeight = presentationSize.height
                return Presentation(
                    slides = pptx.slides.map { convertSlide(it, slideHeight) },
                    width = presentationSize.width,
                    height = presentationSize.height
                )
            }
        }
    }

    private fun convertSlide(poiSlide: XSLFSlide, slideHeight: Int): Slide {
        val images = poiSlide.shapes.filterIsInstance<XSLFPictureShape>().map { convertImageElement(it) }
        val textElements = mutableListOf<Text>()
        val listGroups = mutableListOf<MutableList<Text>>() // all list groups on slide

        for (shape in poiSlide.shapes.filterIsInstance<XSLFTextShape>()) {
            val groupStack = ArrayDeque<MutableList<Text>>() // для вложенных списков внутри shape

            for (paragraph in shape.textParagraphs) {
                if (paragraph.text.isNullOrBlank()) continue

                // collect parameters to determine the content type text (TextType)
                var fontName: String? = null
                var fontSize: Double? = null
                var isBold = false
                var isItalic = false
                var textColor: Color? = null
                val bulletCharacter = paragraph.bulletCharacter
                val indentLevel = paragraph.indentLevel

                for (run in paragraph.textRuns) {
                    if (run.fontFamily != null && fontName == null) fontName = run.fontFamily
                    if (run.fontSize != null && fontSize == null) fontSize = run.fontSize
                    if (!isBold && run.isBold) isBold = true
                    if (!isItalic && run.isItalic) isItalic = true
                    if (textColor == null) textColor = extractSolidPaintColor(run.fontColor)
                }

                val contentType = detectContentType(
                    shape.textType, shape.anchor, fontSize, slideHeight, paragraph.isBullet
                )

                val text = Text(
                    content = paragraph.text,
                    fontFamily = fontName,
                    fontSize = fontSize,
                    isBold = isBold,
                    isItalic = isItalic,
                    textColor = textColor,
                    contentType = contentType,
                    bulletCharacter = bulletCharacter,
                    width = shape.anchor.width,
                    height = shape.anchor.height
                )
                textElements.add(text)

                // grouping lists
                if (contentType == TextType.LIST_ITEM) {
                    val indent = indentLevel
                    // remove all levels above the current one
                    while (groupStack.size > indent + 1) groupStack.removeLast()
                    // if there is no group for the current level, create one
                    if (groupStack.size <= indent) {
                        val newGroup = mutableListOf<Text>()
                        listGroups.add(newGroup)
                        groupStack.addLast(newGroup)
                    }
                    groupStack.last().add(text)
                } else {
                    // if not a list element, reset the stack
                    groupStack.clear()
                }
            }
        }

        logger.info("Slide number: ${poiSlide.slideNumber}")
        for (list in listGroups) {
            logger.info("List started\n")
            for (elem in list) {
                logger.info("List element: $elem")
            }
            logger.info("\nList ended\n")
        }
        return Slide(
            number = poiSlide.slideNumber,
            displayedNumber = slideNumberText(poiSlide),
            title = Slide().extractSlideTitle(poiSlide),
            texts = textElements,
            images = images,
            isTitleSlide = poiSlide.slideNumber == 1,
            listGroups = listGroups,
            backgroundColor = poiSlide.background?.fillColor
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


    private fun convertImageElement(pictureShape: XSLFPictureShape): Image {
        val pictureData = pictureShape.pictureData
        val anchor = pictureShape.anchor

        return Image(
            data = pictureData.data.toList(),
            fileName = pictureData.fileName,
            type = pictureData.type,
            width = anchor.width,
            height = anchor.height
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

    fun detectContentType(
        textType: Placeholder?,
        anchor: Rectangle2D,
        fontSize: Double?,
        slideHeight: Int,
        isBullet: Boolean
    ): TextType {
        return when {
            textType == Placeholder.TITLE || textType == Placeholder.CENTERED_TITLE -> TextType.TITLE
            textType == Placeholder.SUBTITLE -> TextType.SUBTITLE
            textType == Placeholder.HEADER -> TextType.HEADER
            textType == Placeholder.FOOTER -> TextType.FOOTER
            textType == Placeholder.SLIDE_NUMBER -> TextType.SLIDE_NUMBER
            textType == Placeholder.BODY &&
                    (anchor.y < slideHeight / 4 && (fontSize ?: 0.0) >= 24.0) -> TextType.TITLE

            textType == Placeholder.BODY || textType == null ->
                if (isBullet) TextType.LIST_ITEM else TextType.BODY

            else -> TextType.OTHER
        }
    }
}