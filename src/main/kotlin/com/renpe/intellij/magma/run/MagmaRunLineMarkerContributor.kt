package com.renpe.intellij.magma.run

import com.intellij.execution.lineMarker.ExecutorAction
import com.intellij.execution.lineMarker.RunLineMarkerContributor
import com.intellij.icons.AllIcons
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiTreeUtil
import com.renpe.intellij.magma.lang.MagmaFileType

class MagmaRunLineMarkerContributor : RunLineMarkerContributor() {

    override fun getInfo(element: PsiElement): Info? {
        // Magma files are tokenised without a parser, so every leaf in the
        // file is a candidate. Only emit one marker per file, anchored on
        // the very first non-whitespace leaf, so the gutter doesn't fill
        // up with play icons.
        if (element.firstChild != null) return null
        val file = element.containingFile as? PsiFile ?: return null
        if (file.fileType !is MagmaFileType) return null
        if (firstMeaningfulLeaf(file) !== element) return null

        return Info(
            AllIcons.RunConfigurations.TestState.Run,
            ExecutorAction.getActions(0),
        ) { "Run Magma script" }
    }

    // Cached per file: the platform calls getInfo() once per PSI leaf, so
    // recomputing the leaf walk on every call is quadratic on large files.
    // The cache is keyed on the PsiFile and invalidated on any modification.
    private fun firstMeaningfulLeaf(file: PsiFile): PsiElement? =
        CachedValuesManager.getCachedValue(file) {
            var leaf: PsiElement? = PsiTreeUtil.getDeepestFirst(file)
            while (leaf != null && leaf.text.isBlank()) {
                leaf = PsiTreeUtil.nextLeaf(leaf)
            }
            CachedValueProvider.Result.create(leaf, file)
        }
}
