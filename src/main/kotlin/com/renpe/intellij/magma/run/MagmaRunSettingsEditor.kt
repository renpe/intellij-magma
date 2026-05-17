package com.renpe.intellij.magma.run

import com.intellij.openapi.fileChooser.FileChooserDescriptorFactory
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.TextFieldWithBrowseButton
import com.intellij.openapi.util.SystemInfo
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.components.JBTextField
import com.intellij.ui.dsl.builder.AlignX
import com.intellij.ui.dsl.builder.panel
import com.renpe.intellij.magma.run.wsl.MagmaWslSupport
import javax.swing.JComboBox
import javax.swing.JComponent

class MagmaRunSettingsEditor(private val project: Project) : SettingsEditor<MagmaRunConfiguration>() {

    private val scriptField = TextFieldWithBrowseButton().apply {
        @Suppress("DEPRECATION")
        addBrowseFolderListener(
            "Magma Script",
            "Select the .m / .mag / .magma file to run",
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor(),
        )
    }
    private val scriptArgsField = JBTextField()
    private val workingDirField = TextFieldWithBrowseButton().apply {
        @Suppress("DEPRECATION")
        addBrowseFolderListener(
            "Working Directory",
            "Leave blank to use the script's directory",
            project,
            FileChooserDescriptorFactory.createSingleFolderDescriptor(),
        )
    }
    private val interpreterField = TextFieldWithBrowseButton().apply {
        @Suppress("DEPRECATION")
        addBrowseFolderListener(
            "Magma Interpreter",
            "Leave blank to use the path from Settings → Tools → Magma",
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor(),
        )
    }
    private val wslCombo: JComboBox<String> = JComboBox<String>().apply {
        addItem("")
        MagmaWslSupport.listDistributions().forEach { addItem(it) }
    }
    private val threadsField = JBTextField()
    private val seedField = JBTextField()
    private val startupFileField = TextFieldWithBrowseButton().apply {
        @Suppress("DEPRECATION")
        addBrowseFolderListener(
            "Magma Startup File",
            "Magma script to run before user code (-s)",
            project,
            FileChooserDescriptorFactory.createSingleFileDescriptor(),
        )
    }
    private val ignoreStartupBox = JBCheckBox("Ignore startup file (-n)")
    private val memoryLimitField = JBTextField()
    private val extraArgsField = JBTextField()

    override fun createEditor(): JComponent = panel {
        row("Script:") {
            cell(scriptField).align(AlignX.FILL)
                .comment("Path to the Magma script to run.")
        }
        row("Script arguments:") {
            cell(scriptArgsField).align(AlignX.FILL)
                .comment("Passed to the script after the script path.")
        }
        row("Working directory:") {
            cell(workingDirField).align(AlignX.FILL)
                .comment("Empty = use the script's directory.")
        }
        row("Interpreter override:") {
            cell(interpreterField).align(AlignX.FILL)
                .comment("Empty = use the path from <i>Settings → Tools → Magma</i>.")
        }
        if (SystemInfo.isWindows) {
            row("WSL distribution:") {
                cell(wslCombo)
                    .comment("Empty = use the global default. Blank entry = run natively on Windows.")
            }
        }
        row("Threads:") {
            cell(threadsField).align(AlignX.FILL)
                .comment("Override the global <code>-t N</code> setting. Empty = inherit.")
        }
        row("Random seed:") {
            cell(seedField).align(AlignX.FILL)
                .comment("Override <code>-S</code>. Empty = inherit.")
        }
        row("Startup file:") {
            cell(startupFileField).align(AlignX.FILL)
                .comment("Override <code>-s</code>. Empty = inherit.")
        }
        row("") {
            cell(ignoreStartupBox)
                .comment("Forces <code>-n</code> for this run, even if no startup file is set globally.")
        }
        row("Memory limit:") {
            cell(memoryLimitField).align(AlignX.FILL)
                .comment("Override <code>MAGMA_MEMORY_LIMIT</code> (e.g. <code>2G</code>). Empty = inherit.")
        }
        row("Extra arguments:") {
            cell(extraArgsField).align(AlignX.FILL)
                .comment("Appended to the magma command line, after the global extra arguments.")
        }
    }

    override fun resetEditorFrom(s: MagmaRunConfiguration) {
        scriptField.text = s.scriptPath
        scriptArgsField.text = s.scriptArgs
        workingDirField.text = s.workingDirectory
        interpreterField.text = s.interpreterPath
        wslCombo.selectedItem = s.wslDistribution
        threadsField.text = s.threads
        seedField.text = s.seed
        startupFileField.text = s.startupFile
        ignoreStartupBox.isSelected = s.ignoreStartupFile
        memoryLimitField.text = s.memoryLimit
        extraArgsField.text = s.extraArgs
    }

    override fun applyEditorTo(s: MagmaRunConfiguration) {
        val threadsInput = threadsField.text.trim()
        val seedInput = seedField.text.trim()
        val memInput = memoryLimitField.text.trim()
        MagmaInputValidation.validateThreads(threadsInput)
        MagmaInputValidation.validateSeed(seedInput)
        MagmaInputValidation.validateMemoryLimit(memInput)
        s.scriptPath = scriptField.text.trim()
        s.scriptArgs = scriptArgsField.text.trim()
        s.workingDirectory = workingDirField.text.trim()
        s.interpreterPath = interpreterField.text.trim()
        s.wslDistribution = (wslCombo.selectedItem as? String ?: "").trim()
        s.threads = threadsInput
        s.seed = seedInput
        s.startupFile = startupFileField.text.trim()
        s.ignoreStartupFile = ignoreStartupBox.isSelected
        s.memoryLimit = memInput
        s.extraArgs = extraArgsField.text.trim()
    }
}
