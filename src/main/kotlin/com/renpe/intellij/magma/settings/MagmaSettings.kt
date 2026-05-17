package com.renpe.intellij.magma.settings

import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.components.PersistentStateComponent
import com.intellij.openapi.components.Service
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.util.xmlb.XmlSerializerUtil

@State(
    name = "MagmaSettings",
    storages = [Storage("magma.xml")],
)
@Service(Service.Level.APP)
class MagmaSettings : PersistentStateComponent<MagmaSettings.State> {

    data class State(
        var interpreterPath: String = "",
        var defaultWslDistribution: String = "",
        var defaultThreads: String = "",
        var defaultSeed: String = "",
        var defaultStartupFile: String = "",
        var defaultIgnoreStartupFile: Boolean = false,
        var defaultMemoryLimit: String = "",
        var defaultExtraArgs: String = "",
    )

    // `@Volatile` because the state is read from the EDT (Settings UI) and
    // from pool threads (run-config resolution at run start). IntelliJ's
    // PersistentStateComponent already publishes via loadState, but making
    // the publication explicit keeps the intent obvious.
    @Volatile
    private var state = State()

    override fun getState(): State = state

    override fun loadState(s: State) {
        XmlSerializerUtil.copyBean(s, state)
    }

    companion object {
        fun getInstance(): MagmaSettings =
            ApplicationManager.getApplication().getService(MagmaSettings::class.java)
    }
}
