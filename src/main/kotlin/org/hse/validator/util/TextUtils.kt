package org.hse.validator.util

object TextUtils {
    private val romanNumeralRegex = Regex("""^(?i)([ivxlcdm]+)[.)\-–:]?\s+""")

    fun extractManualListIndex(text: String): Any? {
        val trimmed = text.trim()

        val numberMatch = Regex("""^(\d+)[.)\-–:]?\s+""").find(trimmed)
        if (numberMatch != null) return numberMatch.groupValues[1].toIntOrNull()

        val letterMatch = Regex("""^([a-zA-Z])[.)\-–:]?\s+""").find(trimmed)
        if (letterMatch != null) return letterMatch.groupValues[1].lowercase().first()

        return null
    }

    fun isOrderedList(indices: List<Any?>): Boolean {
        if (indices.isEmpty()) return false
        return when {
            indices.all { it is Int } -> {
                val intIndices = indices.filterIsInstance<Int>()
                intIndices.zipWithNext().all { (a, b) -> b == a + 1 }
            }
            indices.all { it is Char } -> {
                val charIndices = indices.filterIsInstance<Char>()
                charIndices.zipWithNext().all { (a, b) -> b == a + 1 }
            }
            else -> false
        }
    }
}
