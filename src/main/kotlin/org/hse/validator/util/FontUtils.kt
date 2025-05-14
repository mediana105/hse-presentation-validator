package org.hse.validator.util

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.nio.file.Files
import java.nio.file.Paths

data class FontClassification(
    val serif: List<String>, val sansSerif: List<String>
)

object FontUtils {
    private val fontMap: FontClassification by lazy {
        val filePath = Paths.get("src/main/resources/fonts.json")
        val json = String(Files.readAllBytes(filePath))
        val mapper = jacksonObjectMapper()
        mapper.readValue(json)
    }

    fun isSansSerif(fontName: String?): Boolean {
        return fontName != null && fontMap.sansSerif.any { it.equals(fontName, ignoreCase = true) }
    }

    fun isSerif(fontName: String?): Boolean {
        return fontName != null && fontMap.serif.any { it.equals(fontName, ignoreCase = true) }
    }
}
