package org.hse.validator.validators

import io.mockk.every
import io.mockk.mockk
import org.hse.validator.model.Presentation
import org.hse.validator.model.Slide
import org.hse.validator.model.Text
import org.hse.validator.validators.rules.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class ValidatorTest {
    private val validator = Validator()

    //Presentation-level rules

    @Test
    fun `MandatorySlidesRule violation`() {
        val presentation = mockk<Presentation>()
        val rule = mockk<MandatorySlidesRule>()
        every { rule.validate(presentation) } returns false
        every { rule.message(null) } returns "Missing mandatory slides"
        every { rule.getInfo() } returns listOf("Intro", "Conclusion")
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
        with(violations[0]) {
            assertEquals(rule, rule)
            assertNull(slide)
            assertNull(text)
            assertEquals("Missing mandatory slides", message)
            assertEquals("Intro, Conclusion", extraInfo)
        }
    }

    @Test
    fun `SlideFormatRule violation`() {
        val presentation = mockk<Presentation>()
        val rule = mockk<SlideFormatRule>()
        every { rule.validate(presentation) } returns false
        every { rule.message(null) } returns "Invalid slide format"
        every { rule.getInfo() } returns "Expected PDF"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
        with(violations[0]) {
            assertEquals(rule, rule)
            assertNull(slide)
            assertNull(text)
            assertEquals("Invalid slide format", message)
            assertEquals("Expected PDF", extraInfo)
        }
    }

    @Test
    fun `SlideCountRule violation`() {
        val presentation = mockk<Presentation>()
        val rule = mockk<SlideCountRule>()
        every { rule.validate(presentation) } returns false
        every { rule.message(null) } returns "Slide count incorrect"
        every { rule.getInfo(presentation) } returns "Must have 5 to 10 slides"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
        with(violations[0]) {
            assertEquals(rule, rule)
            assertNull(slide)
            assertNull(text)
            assertEquals("Slide count incorrect", message)
            assertEquals("Must have 5 to 10 slides", extraInfo)
        }
    }

    @Test
    fun `MaxFontVarietyRule violation`() {
        val presentation = mockk<Presentation>()
        val rule = mockk<MaxFontVarietyRule>()
        every { rule.validate(presentation) } returns false
        every { rule.message(null) } returns "Too many fonts"
        every { rule.getInfo() } returns "Max 3 fonts allowed"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
        with(violations[0]) {
            assertEquals(rule, rule)
            assertNull(slide)
            assertNull(text)
            assertEquals("Too many fonts", message)
            assertEquals("Max 3 fonts allowed", extraInfo)
        }
    }

    @Test
    fun `MaxColorVarietyRule violation`() {
        val presentation = mockk<Presentation>()
        val rule = mockk<MaxColorVarietyRule>()
        every { rule.validate(presentation) } returns false
        every { rule.message(null) } returns "Too many colors"
        every { rule.getInfo() } returns "Max 5 colors allowed"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
        with(violations[0]) {
            assertEquals(rule, rule)
            assertNull(slide)
            assertNull(text)
            assertEquals("Too many colors", message)
            assertEquals("Max 5 colors allowed", extraInfo)
        }
    }


    //Slide-level rules

    private fun prepareSlideAndPresentation(): Pair<Presentation, Slide> {
        val slide = mockk<Slide>()
        val presentation = mockk<Presentation> {
            every { slides } returns listOf(slide)
        }
        return presentation to slide
    }

    @Test
    fun `TooMuchTextRule violation`() {
        val (presentation, slide) = prepareSlideAndPresentation()
        val rule = mockk<TooMuchTextRule>()
        every { rule.validateSlide(slide) } returns false
        every { rule.message(slide) } returns "Too much text"
        every { rule.getInfo(slide) } returns "Max 10 lines"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
        with(violations[0]) {
            assertEquals(rule, rule)
            assertEquals(slide, slide)
            assertNull(text)
            assertEquals("Too much text", message)
            assertEquals("Max 10 lines", extraInfo)
        }
    }

    @Test
    fun `ForbidSingleItemListRule violation`() {
        val (presentation, slide) = prepareSlideAndPresentation()
        val rule = mockk<ForbidSingleItemListRule>()
        every { rule.validateSlide(slide) } returns false
        every { rule.message(slide) } returns "Single item list forbidden"
        every { rule.getInfo() } returns "Lists must have 2+ items"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
        with(violations[0]) {
            assertEquals(rule, rule)
            assertEquals(slide, slide)
            assertNull(text)
            assertEquals("Single item list forbidden", message)
            assertEquals("Lists must have 2+ items", extraInfo)
        }
    }

    @Test
    fun `TitleSlideContentRule violation`() {
        val (presentation, slide) = prepareSlideAndPresentation()
        val rule = mockk<TitleSlideContentRule>()
        every { rule.validateSlide(slide) } returns false
        every { rule.message(slide) } returns "Title slide content missing"
        every { rule.getInfo() } returns "Title and subtitle required"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
        with(violations[0]) {
            assertEquals(rule, rule)
            assertEquals(slide, slide)
            assertNull(text)
            assertEquals("Title slide content missing", message)
            assertEquals("Title and subtitle required", extraInfo)
        }
    }

    @Test
    fun `ListSizeRule violation`() {
        val (presentation, slide) = prepareSlideAndPresentation()
        val rule = mockk<ListSizeRule>()
        every { rule.validateSlide(slide) } returns false
        every { rule.message(slide) } returns "List size invalid"
        every { rule.getInfo() } returns "List must be 3-5 items"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
    }

    @Test
    fun `HeaderFormatRule violation`() {
        val (presentation, slide) = prepareSlideAndPresentation()
        val rule = mockk<HeaderFormatRule>()
        every { rule.validateSlide(slide) } returns false
        every { rule.message(slide) } returns "Header format invalid"
        every { rule.getInfo() } returns "Headers must be bold"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
    }

    @Test
    fun `SlideNumberFormatRule violation`() {
        val (presentation, slide) = prepareSlideAndPresentation()
        val rule = mockk<SlideNumberFormatRule>()
        every { rule.validateSlide(slide) } returns false
        every { rule.message(slide) } returns "Slide number format invalid"
        every { rule.getInfo() } returns "Slide number must be 1-10"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
    }

    @Test
    fun `ForbidListEndPunctuationRule violation`() {
        val (presentation, slide) = prepareSlideAndPresentation()
        val rule = mockk<ForbidListEndPunctuationRule>()
        every { rule.validateSlide(slide) } returns false
        every { rule.message(slide) } returns "List end punctuation forbidden"
        every { rule.getInfo() } returns "No periods at end"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
    }

    @Test
    fun `UniformListCapitalizationRule violation`() {
        val (presentation, slide) = prepareSlideAndPresentation()
        val rule = mockk<UniformListCapitalizationRule>()
        every { rule.validateSlide(slide) } returns false
        every { rule.message(slide) } returns "List capitalization inconsistent"
        every { rule.getInfo() } returns "All list items must be capitalized"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
    }

    @Test
    fun `ContrastRatioRule violation`() {
        val (presentation, slide) = prepareSlideAndPresentation()
        val rule = mockk<ContrastRatioRule>()
        every { rule.validateSlide(slide) } returns false
        every { rule.message(slide) } returns "Contrast ratio too low"
        every { rule.getInfo() } returns "Ratio must be >= 4.5"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
    }

    @Test
    fun `TextToImageAreaRatioRule violation`() {
        val (presentation, slide) = prepareSlideAndPresentation()
        val rule = mockk<TextToImageAreaRatioRule>()
        every { rule.validateSlide(slide) } returns false
        every { rule.message(slide) } returns "Text to image area ratio invalid"
        every { rule.getInfo() } returns "Text area must be <= 30%"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
    }

    @Test
    fun `TextStyleCountRule violation`() {
        val (presentation, slide) = prepareSlideAndPresentation()
        val rule = mockk<TextStyleCountRule>()
        every { rule.validateSlide(slide) } returns false
        every { rule.message(slide) } returns "Too many text styles"
        every { rule.getInfo() } returns "Max 3 text styles"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
    }


    //Text-level rules

    private fun preparePresentationSlideText(): Triple<Presentation, Slide, Text> {
        val text = mockk<Text>()
        val slide = mockk<Slide> {
            every { texts } returns listOf(text)
        }
        val presentation = mockk<Presentation> {
            every { slides } returns listOf(slide)
        }
        return Triple(presentation, slide, text)
    }

    @Test
    fun `SansSerifFontRule violation`() {
        val (presentation, slide, text) = preparePresentationSlideText()
        val rule = mockk<SansSerifFontRule>()
        every { rule.validateText(text) } returns false
        every { rule.message(slide) } returns "Font must be sans serif"
        every { rule.getInfo(text) } returns "Use sans serif fonts only"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
        with(violations[0]) {
            assertEquals(rule, rule)
            assertEquals(slide, slide)
            assertEquals(text, text)
            assertEquals("Font must be sans serif", message)
            assertEquals("Use sans serif fonts only", extraInfo)
        }
    }

    @Test
    fun `FontSizeRule violation`() {
        val (presentation, slide, text) = preparePresentationSlideText()
        val rule = mockk<FontSizeRule>()
        every { rule.validateText(text) } returns false
        every { rule.message(slide) } returns "Font size invalid"
        every { rule.getInfo(text) } returns "12 <= size <= 24"
        val violations = validator.validate(presentation, listOf(rule))
        assertEquals(1, violations.size)
        with(violations[0]) {
            assertEquals(rule, rule)
            assertEquals(slide, slide)
            assertEquals(text, text)
            assertEquals("Font size invalid", message)
            assertEquals("12 <= size <= 24", extraInfo)
        }
    }
}
