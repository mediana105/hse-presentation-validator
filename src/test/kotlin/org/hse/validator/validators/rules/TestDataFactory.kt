package org.hse.validator.validators.rules

import org.hse.validator.model.*
import java.awt.Color

fun createText(
    content: String,
    contentType: TextType = TextType.BODY,
    runs: List<TextRun> = listOf(createTextRun(content = content)),
    width: Double? = null,
    height: Double? = null
): Text = Text(contentType = contentType, runs = runs)

fun createTextRun(
    content: String = "test",
    fontFamily: String? = null,
    textColor: Color? = null,
    fontSize: Double? = null,
    isBold: Boolean = false,
    isItalic: Boolean = false,
    isUnderlined: Boolean = false
): TextRun = TextRun(
    content = content,
    fontFamily = fontFamily,
    textColor = textColor,
    fontSize = fontSize,
    isBold = isBold,
    isItalic = isItalic,
    isUnderlined = isUnderlined
)

fun createImage(
    width: Double = 100.0,
    height: Double = 100.0
): Image = Image(
    data = listOf(1),
    width = width,
    height = height
)

fun createSlide(
    texts: List<Text>? = null,
    listGroups: List<List<Text>> = emptyList(),
    images: List<Image>? = null,
    isTitleSlide: Boolean = false,
    displayedNumber: String? = null,
    backgroundColor: Color? = null
): Slide = Slide(
    texts = texts,
    listGroups = listGroups,
    images = images,
    isTitleSlide = isTitleSlide,
    displayedNumber = displayedNumber,
    backgroundColor = backgroundColor
)

fun createPresentation(
    slides: List<Slide>,
    width: Int = 1600,
    height: Int = 900
): Presentation = Presentation(
    slides = slides,
    width = width,
    height = height
)
