import java.net.URL
import java.nio.file.Files
import java.nio.file.Paths
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

fun main() {
    val apiKey = ""
    val url = "https://www.googleapis.com/webfonts/v1/webfonts?key=$apiKey"
    val json = java.net.URI(url).toURL().readText()
    val mapper = jacksonObjectMapper()
    val data = mapper.readTree(json)

    var sansSerif = mutableSetOf<String>()
    var serif = mutableSetOf<String>()

    data["items"].forEach { item ->
        val family = item["family"].asText()
        val category = item["category"].asText()
        when (category) {
            "sans-serif" -> sansSerif.add(family)
            "serif" -> serif.add(family)
        }
    }
    sansSerif += listOf("Arial", "Helvetica", "Verdana", "Tahoma", "Trebuchet MS", "Segoe UI", "Calibri", "Geneva")
    serif += listOf("Times New Roman", "Georgia", "Garamond", "Cambria", "Palatino Linotype", "Book Antiqua", "Didot", "Baskerville")

    val fontsJson = mapOf(
        "sansSerif" to sansSerif.sorted(),
        "serif" to serif.sorted()
    )
    val outputPath = Paths.get("src/main/resources/fonts.json")
    Files.write(outputPath, mapper.writerWithDefaultPrettyPrinter().writeValueAsBytes(fontsJson))
}
