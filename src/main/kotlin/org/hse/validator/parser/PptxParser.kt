package org.hse.validator.parser

import org.apache.poi.sl.usermodel.PaintStyle
import org.apache.poi.sl.usermodel.PaintStyle.SolidPaint
import org.apache.poi.sl.usermodel.Placeholder
import org.apache.poi.xslf.usermodel.XMLSlideShow
import org.apache.poi.xslf.usermodel.XSLFPictureShape
import org.apache.poi.xslf.usermodel.XSLFSlide
import org.apache.poi.xslf.usermodel.XSLFTextShape
import org.hse.validator.model.*
import org.hse.validator.util.TextUtils
import org.openxmlformats.schemas.drawingml.x2006.main.CTGradientFillProperties
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide
import org.springframework.stereotype.Component
import java.awt.Color
import java.io.FileInputStream
import java.util.logging.Level
import java.util.logging.Logger

@Component
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

        val textShapes = poiSlide.shapes.filterIsInstance<XSLFTextShape>()

        val minY = textShapes.minOfOrNull { it.anchor.y } ?: 0.0
        val maxY = textShapes.maxOfOrNull { it.anchor.y } ?: 0.0

        for (shape in poiSlide.shapes.filterIsInstance<XSLFTextShape>()) {
            val groupStack = ArrayDeque<MutableList<Text>>()
            val largestFontSize = findLargestFontSize(poiSlide.shapes.filterIsInstance<XSLFTextShape>())

            for (paragraph in shape.textParagraphs) {
                if (paragraph.text.isNullOrBlank()) continue

                // collect parameters to determine the content type text (TextType)
                var bulletCharacter: Any? = paragraph.bulletCharacter
                val indentLevel = paragraph.indentLevel

                val textRuns = mutableListOf<TextRun>()
                for (run in paragraph.textRuns) {
                    textRuns.add(
                        TextRun(
                            content = run.rawText,
                            fontFamily = run.fontFamily,
                            fontSize = run.fontSize,
                            isBold = run.isBold,
                            isItalic = run.isItalic,
                            isUnderlined = run.isUnderlined,
                            textColor = extractSolidPaintColor(run.fontColor)
                        )
                    )
                }

                val contentType = getContentType(
                    shape, textRuns, slideHeight, paragraph.isBullet, largestFontSize, minY, maxY
                )
                bulletCharacter =
                    (if (bulletCharacter == null) TextUtils.extractManualListIndex(paragraph.text) else null) as String?

                val text = Text(
                    width = shape.anchor.width,
                    height = shape.anchor.height,
                    runs = textRuns,
                    contentType = contentType,
                    bulletCharacter = bulletCharacter
                )

                textElements.add(text)

                // grouping lists
                getLists(contentType, indentLevel, groupStack, listGroups, text)
            }
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

    fun getLists(
        contentType: TextType,
        indentLevel: Int,
        groupStack: ArrayDeque<MutableList<Text>>,
        listGroups: MutableList<MutableList<Text>>,
        text: Text
    ) {
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

    fun slideNumberText(poiSlide: XSLFSlide): String? {
        val placeholderNumber = poiSlide.shapes
            .filterIsInstance<XSLFTextShape>()
            .find { it.textType == Placeholder.SLIDE_NUMBER }
            ?.text
        if (!placeholderNumber.isNullOrBlank()) return placeholderNumber.trim()

        // try to find the lowest text shape corresponding to pattern "X/N"
        val regex = Regex("""\b\d+\s*/\s*\d+\b""")
        val manualNumber = findBottomMostNumberText(poiSlide, regex)

        return manualNumber?.let { regex.find(it)?.value?.trim() }
    }


    fun convertImageElement(pictureShape: XSLFPictureShape): Image {
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

    private fun findBottomMostNumberText(
        poiSlide: XSLFSlide,
        regex: Regex,
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

    fun getContentType(
        shape: XSLFTextShape,
        textRuns: List<TextRun>,
        slideHeight: Int,
        isBullet: Boolean,
        largestFontSizeInSlide: Double,
        minY: Double,
        maxY: Double
    ): TextType {
        val textType = shape.textType
        val anchor = shape.anchor
        val fontSize = textRuns.mapNotNull { it.fontSize }.maxOrNull()
        return when {
            textType == Placeholder.TITLE || textType == Placeholder.CENTERED_TITLE ||
                    fontSize != null && fontSize == largestFontSizeInSlide && anchor.y == minY -> TextType.TITLE

            textType == Placeholder.FOOTER || anchor.y >= 0.9 * maxY -> TextType.FOOTER
            textType == Placeholder.SUBTITLE -> TextType.SUBTITLE
            textType == Placeholder.HEADER -> TextType.HEADER
            textType == Placeholder.SLIDE_NUMBER -> TextType.SLIDE_NUMBER
            textType == Placeholder.BODY &&
                    (anchor.y < slideHeight / 4 && (fontSize ?: 0.0) >= 24.0) -> TextType.TITLE

            textType == Placeholder.BODY || textType == null ->
                if (isBullet) TextType.LIST_ITEM else TextType.BODY

            else -> TextType.OTHER
        }
    }

    fun findLargestFontSize(textShapes: List<XSLFTextShape>): Double {
        return textShapes
            .flatMap { it.textParagraphs }
            .flatMap { it.textRuns }
            .mapNotNull { it.fontSize }
            .maxOrNull() ?: 0.0
    }
}