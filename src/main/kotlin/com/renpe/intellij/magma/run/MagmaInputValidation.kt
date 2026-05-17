package com.renpe.intellij.magma.run

import com.intellij.openapi.options.ConfigurationException

/**
 * Shared validation for run-config and global-settings input fields, so the
 * UI surfaces a clear error before magma is invoked with junk arguments.
 *
 * Memory-limit parsing already lived in [MagmaMemoryLimit]; this object adds
 * the same defensive front-door for the other free-text fields.
 */
object MagmaInputValidation {
    fun validateThreads(input: String) {
        if (input.isBlank()) return
        val n = input.toIntOrNull()
        if (n == null || n < 1) {
            throw ConfigurationException(
                "Invalid thread count '$input'. Use a positive integer."
            )
        }
    }

    fun validateSeed(input: String) {
        if (input.isBlank()) return
        val n = input.toLongOrNull()
        if (n == null || n < 0 || n > 0xFFFFFFFFL) {
            throw ConfigurationException(
                "Invalid seed '$input'. Use an integer in 0..2^32-1."
            )
        }
    }

    fun validateMemoryLimit(input: String) {
        if (input.isBlank()) return
        if (MagmaMemoryLimit.parseBytes(input) == null) {
            throw ConfigurationException(
                "Invalid memory limit '$input'. Use a number with optional K, M or G suffix."
            )
        }
    }
}
