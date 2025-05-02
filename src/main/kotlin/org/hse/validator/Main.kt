package org.hse.validator

import org.hse.validator.parser.PptxParser
import java.io.File
import java.io.PrintWriter
import java.nio.charset.StandardCharsets
import java.util.logging.FileHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter


fun main() {
    val logger = Logger.getLogger(PptxParser::class.java.name)
    logger.level = Level.ALL

    val fileHandler = FileHandler("parser.log")
    fileHandler.level = Level.ALL
    fileHandler.formatter = SimpleFormatter()
    logger.addHandler(fileHandler)

    try {
        val parser = PptxParser()
        val workingDir = System.getProperty("user.dir")
        val separator = File.separator
        val presentation = parser.parse("$workingDir${separator}presentations${separator}Example.pptx")

        try {
            PrintWriter("output.txt", StandardCharsets.UTF_8).use { writer ->
                for (slide in presentation.slides) {
                    writer.println("Slide number: ${slide.number}")
                    for (text in slide.texts!!) {
                        writer.println("Text font: ${text.fontName}")
                        writer.println("Text type: ${text.contentType}")
                        writer.println("Text color: ${text.textColor}")
                        writer.println(text)
                    }
                    writer.println()
                }
            }
        } catch (e: Exception) {
            System.err.println("Error inside: ${e.message}")
            logger.severe("Error inside: ${e.message}")
        }
    } catch (e: Exception) {
        System.err.println("Error in parsing: ${e.message}")
        logger.severe("Error in parsing: ${e.message}")
    }
}
