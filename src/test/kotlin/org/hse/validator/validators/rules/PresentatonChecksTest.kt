package org.hse.validator.validators.rules

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.awt.Color

class PresentatonChecksTest {

    @Test
    fun `SlideCountRule passes when slides within range`() {
        val slides = List(12) { createSlide() }
        val presentation = createPresentation(slides)
        val rule = SlideCountRule(minSlides = 10, maxSlides = 18)

        assertTrue(rule.validate(presentation))
        assertEquals("Получено: 12", rule.getInfo(presentation))
    }

    @Test
    fun `SlideCountRule fails when slides out of range`() {
        val slides = List(9) { createSlide() }
        val presentation = createPresentation(slides)
        val rule = SlideCountRule(minSlides = 10, maxSlides = 18)

        assertFalse(rule.validate(presentation))
    }

    @Test
    fun `MaxFontVarietyRule passes when fonts within limit`() {
        val texts = listOf(
            createText("test", runs = listOf(createTextRun(content = "test", fontFamily = "Arial"))),
            createText("Times", runs = listOf(createTextRun(content = "Times", fontFamily = "Times New Roman"))),
            createText("Courier", runs = listOf(createTextRun(content = "Courier", fontFamily = "Courier")))
        )
        val slide = createSlide(texts = texts)
        val presentation = createPresentation(listOf(slide))
        val rule = MaxFontVarietyRule(maxFonts = 3)

        assertTrue(rule.validate(presentation))
        assertEquals("Получено: 3", rule.getInfo())
    }

    @Test
    fun `MaxFontVarietyRule fails when fonts exceed limit`() {
        val texts = listOf(
            createText("Arial", runs = listOf(createTextRun(content = "Arial", fontFamily = "Arial"))),
            createText("Times", runs = listOf(createTextRun(content = "Times", fontFamily = "Times New Roman"))),
            createText("Courier", runs = listOf(createTextRun(content = "Courier", fontFamily = "Courier"))),
            createText("Verdana", runs = listOf(createTextRun(content = "Verdana", fontFamily = "Verdana")))
        )
        val slide = createSlide(texts = texts)
        val presentation = createPresentation(listOf(slide))
        val rule = MaxFontVarietyRule(maxFonts = 3)

        assertFalse(rule.validate(presentation))
        assertEquals("Получено: 4", rule.getInfo())
    }

    @Test
    fun `MaxColorVarietyRule passes when colors within limit`() {
        val texts = listOf(
            createText("Red", runs = listOf(createTextRun(content = "Red", textColor = Color.RED))),
            createText("Blue", runs = listOf(createTextRun(content = "Blue", textColor = Color.BLUE))),
            createText("Green", runs = listOf(createTextRun(content = "Green", textColor = Color.GREEN)))
        )
        val slide = createSlide(texts = texts)
        val presentation = createPresentation(listOf(slide))
        val rule = MaxColorVarietyRule(maxColor = 3)

        assertTrue(rule.validate(presentation))
        assertEquals("Получено: 3", rule.getInfo())
    }

    @Test
    fun `MaxColorVarietyRule fails when colors exceed limit`() {
        val texts = listOf(
            createText("Red", runs = listOf(createTextRun(content = "Red", textColor = Color.RED))),
            createText("Blue", runs = listOf(createTextRun(content = "Blue", textColor = Color.BLUE))),
            createText("Green", runs = listOf(createTextRun(content = "Green", textColor = Color.GREEN))),
            createText("Black", runs = listOf(createTextRun(content = "Black", textColor = Color.BLACK)))
        )
        val slide = createSlide(texts = texts)
        val presentation = createPresentation(listOf(slide))
        val rule = MaxColorVarietyRule(maxColor = 3)

        assertFalse(rule.validate(presentation))
        assertEquals("Получено: 4", rule.getInfo())
    }

    @Test
    fun `SlideFormatRule detects 16_9 format`() {
        val slides = listOf(createSlide())
        val presentation = createPresentation(slides, width = 1600, height = 900)
        val rule = SlideFormatRule(allowedFormats = setOf("16:9", "4:3"))

        assertTrue(rule.validate(presentation))
        assertEquals("Получено: 16:9", rule.getInfo())
    }

    @Test
    fun `SlideFormatRule detects unknown format`() {
        val slides = listOf(createSlide())
        val presentation = createPresentation(slides, width = 1000, height = 1000)
        val rule = SlideFormatRule(allowedFormats = setOf("16:9", "4:3"))

        assertFalse(rule.validate(presentation))
        assertEquals("Получено: Other", rule.getInfo())
    }

    @Test
    fun `MandatorySlidesRule passes when all required slides present`() {
        val slides = listOf(
            createSlide(texts = emptyList(), displayedNumber = "Введение"),
            createSlide(texts = emptyList(), displayedNumber = "Задачи"),
            createSlide(texts = emptyList(), displayedNumber = "Результаты")
        )
        val presentation = createPresentation(slides)
        val rule = MandatorySlidesRule(listOf("Введение", "Результаты"))

        assertTrue(rule.validate(presentation))
        assertTrue(rule.getInfo().isEmpty())
    }

    @Test
    fun `MandatorySlidesRule fails when some required slides missing`() {
        val slides = listOf(
            createSlide(texts = emptyList(), displayedNumber = "Введение"),
            createSlide(texts = emptyList(), displayedNumber = "Заключение")
        )
        val presentation = createPresentation(slides)
        val rule = MandatorySlidesRule(listOf("Введение", "Результаты"))

        assertFalse(rule.validate(presentation))
        assertEquals(listOf("Результаты"), rule.getInfo())
    }
}
