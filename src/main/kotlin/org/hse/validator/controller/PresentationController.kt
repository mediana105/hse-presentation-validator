package org.hse.validator.controller

import org.hse.validator.parser.PptxParser
import org.hse.validator.rules.profile.PresentationProfiles
import org.hse.validator.validators.ValidationResult
import org.hse.validator.validators.Validator
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api")
class PresentationController(
    val pptxParser: PptxParser,
    val validator: Validator
) {

    @PostMapping("/validate")
    fun validate(
        @RequestParam("type") type: String,
        @RequestPart("file") file: MultipartFile,
        @RequestParam("dsl", required = false) dsl: String?
    ): List<ValidationResult> {
        val tempFile = kotlin.io.path.createTempFile(suffix = ".pptx").toFile()
        file.inputStream.use { input ->
            tempFile.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        val presentation = pptxParser.parse(tempFile.absolutePath)
        val rules = if (!dsl.isNullOrBlank()) {
            throw NotImplementedError()
        } else {
            PresentationProfiles.rulesFor(type)
        }

        val results = validator.validate(presentation, rules)
        return results
    }
}
