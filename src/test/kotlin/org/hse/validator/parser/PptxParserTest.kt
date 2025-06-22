package org.hse.validator.parser

import io.mockk.every
import io.mockk.mockk
import org.apache.poi.sl.usermodel.PictureData.PictureType
import org.apache.poi.sl.usermodel.Placeholder
import org.apache.poi.xslf.usermodel.*
import org.hse.validator.model.TextType
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.awt.Rectangle

class PptxParserTest {

    private lateinit var parser: PptxParser

    @BeforeEach
    fun setup() {
        parser = PptxParser()
    }

    @Test
    fun `findLargestFontSize returns max font size among all text runs`() {
        val run1 = mockk<XSLFTextRun>()
        every { run1.fontSize } returns 10.0
        val run2 = mockk<XSLFTextRun>()
        every { run2.fontSize } returns 20.0
        val para1 = mockk<XSLFTextParagraph>()
        every { para1.textRuns } returns listOf(run1)
        val para2 = mockk<XSLFTextParagraph>()
        every { para2.textRuns } returns listOf(run2)
        val shape = mockk<XSLFTextShape>()
        every { shape.textParagraphs } returns listOf(para1, para2)
        val maxFont = parser.findLargestFontSize(listOf(shape))

        Assertions.assertEquals(20.0, maxFont)
    }

    @Test
    fun `getContentType detects TITLE by placeholder and font size`() {
        val shape = mockk<XSLFTextShape>()
        every { shape.textType } returns Placeholder.TITLE
        every { shape.anchor } returns Rectangle(0, 0, 100, 50)
        val runs = listOf(
            org.hse.validator.model.TextRun("text", null, 24.0, false, false, false, null)
        )

        val type = parser.getContentType(
            shape, runs,
            slideHeight = 400,
            isBullet = false,
            largestFontSizeInSlide = 24.0,
            minY = 0.0,
            maxY = 350.0
        )

        Assertions.assertEquals(TextType.TITLE, type)
    }

    @Test
    fun `getContentType detects FOOTER by placeholder and position`() {
        val shape = mockk<XSLFTextShape>()
        every { shape.textType } returns Placeholder.FOOTER
        every { shape.anchor } returns Rectangle(0, 360, 100, 50)
        val runs = listOf(
            org.hse.validator.model.TextRun("footer", null, 12.0, false, false, false, null)
        )

        val type = parser.getContentType(
            shape, runs,
            slideHeight = 400,
            isBullet = false,
            largestFontSizeInSlide = 24.0,
            minY = 0.0,
            maxY = 350.0
        )

        Assertions.assertEquals(TextType.FOOTER, type)
    }

    @Test
    fun `getContentType detects SUBTITLE by placeholder`() {
        val shape = mockk<XSLFTextShape>()
        every { shape.textType } returns Placeholder.SUBTITLE
        every { shape.anchor } returns Rectangle(0, 100, 100, 50)
        val runs = listOf(
            org.hse.validator.model.TextRun("subtitle", null, 18.0, false, false, false, null)
        )

        val type = parser.getContentType(
            shape, runs,
            slideHeight = 400,
            isBullet = false,
            largestFontSizeInSlide = 24.0,
            minY = 0.0,
            maxY = 350.0
        )

        Assertions.assertEquals(TextType.SUBTITLE, type)
    }

    @Test
    fun `getContentType detects HEADER by placeholder`() {
        val shape = mockk<XSLFTextShape>()
        every { shape.textType } returns Placeholder.HEADER
        every { shape.anchor } returns Rectangle(0, 50, 100, 50)
        val runs = listOf(
            org.hse.validator.model.TextRun("header", null, 16.0, false, false, false, null)
        )

        val type = parser.getContentType(
            shape, runs,
            slideHeight = 400,
            isBullet = false,
            largestFontSizeInSlide = 24.0,
            minY = 0.0,
            maxY = 350.0
        )

        Assertions.assertEquals(TextType.HEADER, type)
    }

    @Test
    fun `getContentType detects SLIDE_NUMBER by placeholder`() {
        val shape = mockk<XSLFTextShape>()
        every { shape.textType } returns Placeholder.SLIDE_NUMBER
        every { shape.anchor } returns Rectangle(0, 20, 100, 50)
        val runs = listOf(
            org.hse.validator.model.TextRun("1", null, 14.0, false, false, false, null)
        )

        val type = parser.getContentType(
            shape, runs,
            slideHeight = 400,
            isBullet = false,
            largestFontSizeInSlide = 24.0,
            minY = 0.0,
            maxY = 350.0
        )

        Assertions.assertEquals(TextType.SLIDE_NUMBER, type)
    }

    @Test
    fun `getContentType detects TITLE by font size and position`() {
        val shape = mockk<XSLFTextShape>()
        every { shape.textType } returns Placeholder.BODY
        every { shape.anchor } returns Rectangle(0, 50, 100, 50)
        val runs = listOf(
            org.hse.validator.model.TextRun("big title", null, 24.0, false, false, false, null)
        )

        val type = parser.getContentType(
            shape, runs,
            slideHeight = 400,
            isBullet = false,
            largestFontSizeInSlide = 30.0,
            minY = 0.0,
            maxY = 350.0
        )

        Assertions.assertEquals(TextType.TITLE, type)
    }

    @Test
    fun `getContentType detects LIST_ITEM if bullet true`() {
        val shape = mockk<XSLFTextShape>()
        every { shape.textType } returns Placeholder.BODY
        every { shape.anchor } returns Rectangle(0, 150, 100, 50)
        val runs = listOf(
            org.hse.validator.model.TextRun("list item", null, 14.0, false, false, false, null)
        )

        val type = parser.getContentType(
            shape, runs,
            slideHeight = 400,
            isBullet = true,
            largestFontSizeInSlide = 24.0,
            minY = 0.0,
            maxY = 350.0
        )

        Assertions.assertEquals(TextType.LIST_ITEM, type)
    }

    @Test
    fun `getContentType detects BODY if no bullet and placeholder body`() {
        val shape = mockk<XSLFTextShape>()
        every { shape.textType } returns Placeholder.BODY
        every { shape.anchor } returns Rectangle(0, 200, 100, 50)
        val runs = listOf(
            org.hse.validator.model.TextRun("body text", null, 14.0, false, false, false, null)
        )

        val type = parser.getContentType(
            shape, runs,
            slideHeight = 400,
            isBullet = false,
            largestFontSizeInSlide = 24.0,
            minY = 0.0,
            maxY = 350.0
        )

        Assertions.assertEquals(TextType.BODY, type)
    }

    @Test
    fun `getContentType detects OTHER for unknown placeholder`() {
        val shape = mockk<XSLFTextShape>()
        every { shape.textType } returns null
        every { shape.anchor } returns Rectangle(0, 200, 100, 50)
        val runs = listOf(
            org.hse.validator.model.TextRun("other", null, 14.0, false, false, false, null)
        )

        val type = parser.getContentType(
            shape, runs,
            slideHeight = 400,
            isBullet = false,
            largestFontSizeInSlide = 24.0,
            minY = 0.0,
            maxY = 350.0
        )

        Assertions.assertEquals(TextType.BODY, type)
    }


    @Test
    fun `convertImageElement converts pictureShape to Image`() {
        val pictureData = mockk<XSLFPictureData>()
        every { pictureData.data } returns byteArrayOf(1, 2, 3)
        every { pictureData.fileName } returns "image.png"
        every { pictureData.type } returns PictureType.PNG


        val anchor = Rectangle(100, 200, 300, 400)

        val pictureShape = mockk<XSLFPictureShape>()
        every { pictureShape.pictureData } returns pictureData
        every { pictureShape.anchor } returns anchor

        val image = parser.convertImageElement(pictureShape)

        val expectedData = listOf<Byte>(1, 2, 3)
        Assertions.assertEquals(expectedData, image.data)
        Assertions.assertEquals("image.png", image.fileName)
        Assertions.assertEquals(PictureType.PNG, image.type)
        Assertions.assertEquals(anchor.width, image.width.toInt())
        Assertions.assertEquals(anchor.height, image.height.toInt())
    }

    @Test
    fun `getLists correctly groups list items by indent level`() {
        val listGroups = mutableListOf<MutableList<org.hse.validator.model.Text>>()
        val groupStack = ArrayDeque<MutableList<org.hse.validator.model.Text>>()

        val text1 = org.hse.validator.model.Text(
            width = 100.0,
            height = 20.0,
            runs = emptyList(),
            contentType = TextType.LIST_ITEM
        )
        val text2 = org.hse.validator.model.Text(
            width = 100.0,
            height = 20.0,
            runs = emptyList(),
            contentType = TextType.LIST_ITEM
        )
        val text3 =
            org.hse.validator.model.Text(width = 100.0, height = 20.0, runs = emptyList(), contentType = TextType.BODY)

        parser.getLists(TextType.LIST_ITEM, 0, groupStack, listGroups, text1)
        parser.getLists(TextType.LIST_ITEM, 1, groupStack, listGroups, text2)
        parser.getLists(TextType.BODY, 0, groupStack, listGroups, text3)

        Assertions.assertEquals(2, listGroups.size)
        Assertions.assertTrue(listGroups[0].contains(text1))
        Assertions.assertTrue(listGroups[1].contains(text2))
        Assertions.assertTrue(groupStack.isEmpty())
    }

    @Test
    fun `slideNumberText returns placeholder text if exists`() {
        val slide = mockk<XSLFSlide>()
        val textShape = mockk<XSLFTextShape>()
        every { textShape.textType } returns Placeholder.SLIDE_NUMBER
        every { textShape.text } returns "abc"
        every { slide.shapes } returns listOf(textShape)

        val result = parser.slideNumberText(slide)

        Assertions.assertEquals("abc", result)
    }

    @Test
    fun `slideNumberText returns manual number matching regex if placeholder missing`() {
        val slide = mockk<XSLFSlide>()
        val shape1 = mockk<XSLFTextShape>()
        val shape2 = mockk<XSLFTextShape>()
        every { shape1.text } returns "Slide 2/10"
        every { shape1.anchor } returns Rectangle(0, 100, 100, 20)
        every { shape1.textType } returns null
        every { shape2.text } returns "Page 3/15"
        every { shape2.anchor } returns Rectangle(0, 300, 100, 20)
        every { shape2.textType } returns null
        every { slide.shapes } returns listOf(shape1, shape2)

        val result = parser.slideNumberText(slide)

        Assertions.assertEquals("3/15", result)
    }
}
