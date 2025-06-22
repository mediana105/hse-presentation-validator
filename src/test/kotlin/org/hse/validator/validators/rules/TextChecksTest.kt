package org.hse.validator.validators.rules

import org.hse.validator.model.TextType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class TextChecksTest {

    @Test
    fun `SansSerifFontRule passes for sans serif fonts`() {
        val run = createTextRun(fontFamily = "Arial")
        val text = createText(content = "Example text", runs = listOf(run), contentType = TextType.BODY)
        val rule = SansSerifFontRule()

        assertTrue(rule.validateText(text))
        assertNull(rule.getInfo(text))
    }

    @Test
    fun `SansSerifFontRule fails for serif fonts`() {
        val run = createTextRun(fontFamily = "Times New Roman")
        val text = createText(content = "Example text", runs = listOf(run), contentType = TextType.BODY)
        val rule = SansSerifFontRule()

        assertFalse(rule.validateText(text))
        assertTrue(rule.getInfo(text)?.contains("Times New Roman") == true)
    }

    @Test
    fun `SansSerifFontRule ignores non-body text`() {
        val run = createTextRun(fontFamily = "Times New Roman")
        val text = createText(content = "Title text", runs = listOf(run), contentType = TextType.TITLE)
        val rule = SansSerifFontRule()

        assertTrue(rule.validateText(text))
        assertTrue(rule.getInfo(text)?.contains("Times New Roman") == true)
    }

    @Test
    fun `FontSizeRule passes for valid body font size`() {
        val run = createTextRun(fontSize = 16.0)
        val text = createText(content = "Body text", runs = listOf(run), contentType = TextType.BODY)
        val rule = FontSizeRule()

        assertTrue(rule.validateText(text))
        assertNull(rule.getInfo(text))
    }

    @Test
    fun `FontSizeRule fails for too small body font size`() {
        val run = createTextRun(fontSize = 10.0)
        val text = createText(content = "Body text", runs = listOf(run), contentType = TextType.BODY)
        val rule = FontSizeRule()

        assertFalse(rule.validateText(text))
        assertTrue(rule.getInfo(text)?.contains("10.0") == true)
    }

    @Test
    fun `FontSizeRule passes for valid title font size`() {
        val run = createTextRun(fontSize = 30.0)
        val text = createText(content = "Title text", runs = listOf(run), contentType = TextType.TITLE)
        val rule = FontSizeRule()

        assertTrue(rule.validateText(text))
        assertNull(rule.getInfo(text))
    }

    @Test
    fun `FontSizeRule fails for too large title font size`() {
        val run = createTextRun(fontSize = 40.0)
        val text = createText(content = "Title text", runs = listOf(run), contentType = TextType.TITLE)
        val rule = FontSizeRule()

        assertFalse(rule.validateText(text))
        assertTrue(rule.getInfo(text)?.contains("40.0") == true)
    }

    @Test
    fun `FontSizeRule ignores other text types`() {
        val run = createTextRun(fontSize = 8.0)
        val text = createText(content = "Footnote text", runs = listOf(run), contentType = TextType.FOOTER)
        val rule = FontSizeRule()

        assertTrue(rule.validateText(text))
        assertNull(rule.getInfo(text))
    }

}
