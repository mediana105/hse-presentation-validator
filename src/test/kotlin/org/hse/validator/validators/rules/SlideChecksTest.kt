package org.hse.validator.validators.rules

import org.hse.validator.model.Image
import org.hse.validator.model.TextType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import java.awt.Color

class SlideChecksTest {

    @Test
    fun `TooMuchTextRule passes with acceptable lines and words`() {
        val texts = listOf(
            createText(content = "word1 word2"),
            createText(content = "word3 word4 word5")
        )
        val slide = createSlide(texts = texts)
        val rule = TooMuchTextRule(maxLines = 5, maxWords = 10)

        assertTrue(rule.validateSlide(slide))
        assertEquals("Текущий объем: 2 строк, 5 слов", rule.getInfo(slide))
    }

    @Test
    fun `TooMuchTextRule fails when too many lines`() {
        val texts = List(11) { createText(content = "word") }
        val slide = createSlide(texts = texts)
        val rule = TooMuchTextRule(maxLines = 10, maxWords = 100)

        assertFalse(rule.validateSlide(slide))
        assertEquals("Текущий объем: 11 строк, 11 слов", rule.getInfo(slide))
    }

    @Test
    fun `TooMuchTextRule fails when too many words`() {
        val longText = "word ".repeat(41).trim()
        val texts = listOf(createText(content = longText))
        val slide = createSlide(texts = texts)
        val rule = TooMuchTextRule(maxLines = 10, maxWords = 40)

        assertFalse(rule.validateSlide(slide))
        assertEquals("Текущий объем: 1 строк, 41 слов", rule.getInfo(slide))
    }

    @Test
    fun `ForbidSingleItemListRule passes when no single item lists`() {
        val list1 = listOf(createText(content = "item1"), createText(content = "item2"))
        val list2 = listOf(createText(content = "item3"), createText(content = "item4"))
        val slide = createSlide(listGroups = listOf(list1, list2))
        val rule = ForbidSingleItemListRule()

        assertTrue(rule.validateSlide(slide))
        assertEquals("", rule.getInfo())
    }

    @Test
    fun `ForbidSingleItemListRule fails when single item list present`() {
        val list1 = listOf(createText(content = "item1"))
        val slide = createSlide(listGroups = listOf(list1))
        val rule = ForbidSingleItemListRule()

        assertFalse(rule.validateSlide(slide))
        assertEquals("item1", rule.getInfo())
    }

    @Test
    fun `TitleSlideContentRule passes when all required elements present`() {
        val texts = listOf(
            createText(content = "Иванов Иван Иванович"),
            createText(content = "научный руководитель Петров Петр Петрович"),
            createText(content = "Мой Университет")
        )
        val slide = createSlide(texts = texts, isTitleSlide = true)
        val rule = TitleSlideContentRule()

        assertTrue(rule.validateSlide(slide))
        assertEquals("", rule.getInfo())
    }

    @Test
    fun `TitleSlideContentRule fails when elements missing`() {
        val texts = listOf(
            createText(content = "Иванов Иван Иванович"),
            createText(content = "Мой Институт")
        )
        val slide = createSlide(texts = texts, isTitleSlide = true)
        val rule = TitleSlideContentRule()

        assertFalse(rule.validateSlide(slide))
        assertEquals("имя руководителя", rule.getInfo())
    }

    @Test
    fun `ListSizeRule passes when lists within max size`() {
        val list1 = List(3) { createText(content = "item$it") }
        val slide = createSlide(listGroups = listOf(list1))
        val rule = ListSizeRule(maxItems = 5)

        assertTrue(rule.validateSlide(slide))
        assertEquals("", rule.getInfo())
    }

    @Test
    fun `ListSizeRule fails when list too long`() {
        val list1 = List(8) { createText(content = "item$it") }
        val slide = createSlide(listGroups = listOf(list1))
        val rule = ListSizeRule(maxItems = 7)

        assertFalse(rule.validateSlide(slide))
        assertTrue(rule.getInfo().contains("[8]: item0, item1"))
    }

    @Test
    fun `HeaderFormatRule passes with valid header`() {
        val header = createText(content = "Заголовок без точки", contentType = TextType.TITLE)
        val slide = createSlide(texts = listOf(header))
        val rule = HeaderFormatRule(maxWords = 10)

        assertTrue(rule.validateSlide(slide))
        assertEquals("", rule.getInfo())
    }

    @Test
    fun `HeaderFormatRule fails when header ends with dot or too long`() {
        val header = createText(
            content = "Это слишком длинный заголовок, который содержит более десяти слов.",
            contentType = TextType.TITLE
        )
        val slide = createSlide(texts = listOf(header))
        val rule = HeaderFormatRule(maxWords = 10)

        assertFalse(rule.validateSlide(slide))
        val info = rule.getInfo()
        assertTrue(info.contains("заканчивается точкой"))
        assertTrue(info.contains("слишком длинный"))
    }

    @Test
    fun `SlideNumberFormatRule passes for title slide without number`() {
        val slide = createSlide(isTitleSlide = true, displayedNumber = null)
        val rule = SlideNumberFormatRule()

        assertTrue(rule.validateSlide(slide))
    }

    @Test
    fun `SlideNumberFormatRule fails for title slide with number`() {
        val slide = createSlide(isTitleSlide = true, displayedNumber = "1")
        val rule = SlideNumberFormatRule()

        assertFalse(rule.validateSlide(slide))
        assertTrue(rule.getInfo().contains("Титульный слайд содержит номер"))
    }

    @Test
    fun `SlideNumberFormatRule passes for non-title slide with number`() {
        val slide = createSlide(isTitleSlide = false, displayedNumber = "2")
        val rule = SlideNumberFormatRule()

        assertTrue(rule.validateSlide(slide))
        assertEquals("Сообщение не задано", rule.getInfo())
    }

    @Test
    fun `SlideNumberFormatRule fails for non-title slide without number`() {
        val slide = createSlide(isTitleSlide = false, displayedNumber = null)
        val rule = SlideNumberFormatRule()

        assertFalse(rule.validateSlide(slide))
        assertTrue(rule.getInfo().contains("Слайд без номера"))
    }

    @Test
    fun `ForbidListEndPunctuationRule fails when list items end with semicolon`() {
        val list1 = listOf(
            createText(content = "item1;"),
            createText(content = "item2")
        )
        val slide = createSlide(listGroups = listOf(list1))
        val rule = ForbidListEndPunctuationRule()

        assertFalse(rule.validateSlide(slide))
        assertTrue(rule.getInfo().contains("item1;"))
    }

    @Test
    fun `UniformListCapitalizationRule fails with mixed capitalization`() {
        val list1 = listOf(
            createText(content = "item1"),
            createText(content = "Item2")
        )
        val slide = createSlide(listGroups = listOf(list1))
        val rule = UniformListCapitalizationRule()

        assertFalse(rule.validateSlide(slide))
    }

    @Test
    fun `ContrastRatioRule fails with insufficient contrast`() {
        val textColor = Color(200, 200, 200)
        val textRun = createTextRun(content = "text", textColor = textColor)
        val text = createText(content = "text", runs = listOf(textRun))
        val slide = createSlide(texts = listOf(text), backgroundColor = Color.WHITE)
        val rule = ContrastRatioRule(minContrastForText = 4.5)

        assertFalse(rule.validateSlide(slide))
    }

    @Test
    fun `TextToImageAreaRatioRule fails when text area exceeds ratio`() {
        val image = Image(width = 50.0, height = 50.0, data = listOf(0x10, 0x20))
        val text = createText(content = "long text", width = 60.0, height = 60.0)
        val slide = createSlide(texts = listOf(text), images = listOf(image))
        val rule = TextToImageAreaRatioRule(maxTextPercent = 90)

        assertTrue(rule.validateSlide(slide))
    }

    @Test
    fun `TextStyleCountRule fails when styles exceed limit`() {
        val runs = listOf(
            createTextRun(content = "a", isBold = true),
            createTextRun(content = "b", isItalic = true),
            createTextRun(content = "c", isUnderlined = true),
            createTextRun(content = "d", isBold = true, isItalic = true)
        )
        val text = createText(content = "abcd", runs = runs)
        val slide = createSlide(texts = listOf(text))
        val rule = TextStyleCountRule(maxBold = 3, maxItalic = 10, maxUnderline = 1)

        assertTrue(rule.validateSlide(slide))
    }
}
