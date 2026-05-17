package com.renpe.intellij.magma.run

/**
 * Parses user-friendly memory size strings into the byte value that
 * `MAGMA_MEMORY_LIMIT` expects. Magma takes a raw byte count, but typing
 * `2147483648` for 2 GiB is unfriendly.
 *
 * Accepted forms: `1024`, `1024B`, `512K`, `512KB`, `2M`, `2MB`, `4G`, `4GB`.
 * Case-insensitive. Whitespace is tolerated. Returns null for blank or
 * malformed input. Suffixes are binary (1024-based), since that matches what
 * magma itself uses internally for memory accounting.
 */
object MagmaMemoryLimit {
    private val PATTERN = Regex("""^\s*(\d+)\s*([kKmMgG]?)[bB]?\s*$""")

    fun parseBytes(input: String): Long? {
        if (input.isBlank()) return null
        val match = PATTERN.matchEntire(input) ?: return null
        val value = match.groupValues[1].toLongOrNull() ?: return null
        val multiplier = when (match.groupValues[2].lowercase()) {
            "k" -> 1024L
            "m" -> 1024L * 1024
            "g" -> 1024L * 1024 * 1024
            else -> 1L
        }
        if (value < 0) return null
        val product = value * multiplier
        if (multiplier != 0L && product / multiplier != value) return null  // overflow guard
        return product
    }
}
