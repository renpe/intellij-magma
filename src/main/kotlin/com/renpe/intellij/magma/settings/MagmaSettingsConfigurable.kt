package com.renpe.intellij.magma.settings

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.Configurable
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.SystemInfo
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.renpe.intellij.magma.run.MagmaInputValidation
import com.renpe.intellij.magma.run.wsl.MagmaWslSupport
import javax.swing.JComboBox
import javax.swing.JComponent

class MagmaSettingsConfigurable : Configurable {

    private val interpreterField = TextFieldWithBrowseButton().apply {
        addBrowseFolderListener(
            null,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
                .withTitle("Select Magma Executable")
                .withDescription("Path to the magma binary"),
        )
    }
    private val wslCombo: JComboBox<String> = JComboBox<String>().apply {
        addItem("")
        MagmaWslSupport.listDistributions().forEach { addItem(it) }
    }
    private val threadsField = JBTextField()
    private val seedField = JBTextField()
    private val startupFileField = TextFieldWithBrowseButton().apply {
        addBrowseFolderListener(
            null,
            FileChooserDescriptorFactory.createSingleFileDescriptor()
                .withTitle("Select Magma Startup File")
                .withDescription("Magma script to run before user code (-s)"),
        )
    }
    private val ignoreStartupBox = JBCheckBox("Ignore startup file (-n)")
    private val memoryLimitField = JBTextField()
    private val extraArgsField = JBTextField()

    override fun getDisplayName(): String = "Magma"

    override fun createComponent(): JComponent = panel {
        row("Magma interpreter:") {
            cell(interpreterField).align(AlignX.FILL)
                .comment(
                    "Absolute path to the <code>magma</code> binary. When a WSL distribution " +
                        "is selected, this must be the path <i>inside</i> WSL " +
                        "(e.g. <code>/usr/local/bin/magma</code>)."
                )
        }
        if (SystemInfo.isWindows) {
            row("Run via WSL:") {
                cell(wslCombo)
                    .comment("Optional. Pick a WSL distribution to run magma there instead of natively.")
            }
        }
        row("Threads:") {
            cell(threadsField).align(AlignX.FILL)
                .comment("POSIX thread count (<code>-t N</code>). Leave empty for magma's default.")
        }
        row("Random seed:") {
            cell(seedField).align(AlignX.FILL)
                .comment("RNG seed (<code>-S</code>), integer in 0…2³²-1. Leave empty for a fresh seed.")
        }
        row("Startup file:") {
            cell(startupFileField).align(AlignX.FILL)
                .comment(
                    "Magma script run before user code (<code>-s</code>). Overrides " +
                        "<code>MAGMA_STARTUP_FILE</code>. Empty = use the env-var as-is."
                )
        }
        row("") {
            cell(ignoreStartupBox)
                .comment("Disable any startup file, including <code>MAGMA_STARTUP_FILE</code> (<code>-n</code>).")
        }
        row("Memory limit:") {
            cell(memoryLimitField).align(AlignX.FILL)
                .comment(
                    "Sets <code>MAGMA_MEMORY_LIMIT</code>. Accepts <code>512K</code>, " +
                        "<code>256M</code>, <code>2G</code>, or a plain byte count. " +
                        "Empty = unlimited."
                )
        }
        row("Extra arguments:") {
            cell(extraArgsField).align(AlignX.FILL)
                .comment(
                    "Passed verbatim to magma after <code>-b</code>. Useful for ad-hoc " +
                        "variable assignments like <code>N:=42</code>."
                )
        }
    }

    override fun isModified(): Boolean {
        val s = MagmaSettings.getInstance().state
        return interpreterField.text != s.interpreterPath ||
            (wslCombo.selectedItem as? String ?: "") != s.defaultWslDistribution ||
            threadsField.text != s.defaultThreads ||
            seedField.text != s.defaultSeed ||
            startupFileField.text != s.defaultStartupFile ||
            ignoreStartupBox.isSelected != s.defaultIgnoreStartupFile ||
            memoryLimitField.text != s.defaultMemoryLimit ||
            extraArgsField.text != s.defaultExtraArgs
    }

    override fun apply() {
        val threadsInput = threadsField.text.trim()
        val seedInput = seedField.text.trim()
        val memInput = memoryLimitField.text.trim()
        MagmaInputValidation.validateThreads(threadsInput)
        MagmaInputValidation.validateSeed(seedInput)
        MagmaInputValidation.validateMemoryLimit(memInput)
        val s = MagmaSettings.getInstance().state
        s.interpreterPath = interpreterField.text.trim()
        s.defaultWslDistribution = (wslCombo.selectedItem as? String ?: "").trim()
        s.defaultThreads = threadsInput
        s.defaultSeed = seedInput
        s.defaultStartupFile = startupFileField.text.trim()
        s.defaultIgnoreStartupFile = ignoreStartupBox.isSelected
        s.defaultMemoryLimit = memInput
        s.defaultExtraArgs = extraArgsField.text.trim()
    }

    override fun reset() {
        val s = MagmaSettings.getInstance().state
        interpreterField.text = s.interpreterPath
        wslCombo.selectedItem = s.defaultWslDistribution
        threadsField.text = s.defaultThreads
        seedField.text = s.defaultSeed
        startupFileField.text = s.defaultStartupFile
        ignoreStartupBox.isSelected = s.defaultIgnoreStartupFile
        memoryLimitField.text = s.defaultMemoryLimit
        extraArgsField.text = s.defaultExtraArgs
    }
}
