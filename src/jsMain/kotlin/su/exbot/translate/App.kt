package su.exbot.translate

import kotlinx.browser.window
import su.exbot.translate.callbacks.*

val languagesPlayerIds = mutableMapOf<Int, String?>()
val translatedText = mutableMapOf<String, String>()

val onChatMessage = window.asDynamic().onChatMessage as (String, Any) -> Unit
val sendChatInput = window.asDynamic().sendChatInput as (String) -> Unit
val functionInterface = window.asDynamic().`interface` as (String) -> dynamic

const val PREFIX: String = "{0087b0}[translate] {ffffff}"
const val mainLanguage: String = "ru"

fun main() {
    println("[translate] script loaded")

    window.asDynamic().onChatMessage = ::onReceiveMessages
    window.asDynamic().sendChatInput = ::onSendChatMessage
    window.asDynamic().`interface` = ::onInterface
}

external fun sendClientEventHandle(eventId: Int, vararg args: dynamic)