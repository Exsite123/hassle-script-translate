package su.exbot.translate.callbacks

import su.exbot.translate.*
import web.location.location

fun onSendChatMessage(text: String) {
    val args = text.split(" ")
    when(val command = args[0]) {
        "/tpm" -> {
            if(args.size < 3)
                return onChatMessage("Используйте: $command [id игрока] [текст]", "00BEBEBE")
            val playerId = args[1].toIntOrNull()
                ?: return onChatMessage("ID игрока должно быть цифровым.", "00BEBEBE")

            val message = args.drop(2).joinToString(" ")

            val language = languagesPlayerIds[playerId]
            return if(language != null) {
                translateTextAsync(message, mainLanguage, language) { response ->
                    val translatedText = response?.first
                        ?: return@translateTextAsync onChatMessage(
                            "$PREFIX Не удалось получить перевод сообщения", "00BEBEBE"
                        )
                    sendChatInput("/pm $playerId $translatedText")
                }
            } else {
                sendChatInput("/pm $playerId $message")
            }
        }
        "/tn" -> {
            if(args.size < 3)
                return onChatMessage("Используйте: $command [id игрока] [текст]", "00BEBEBE")
            val playerId = args[1].toIntOrNull()
                ?: return onChatMessage("ID игрока должно быть цифровым.", "00BEBEBE")

            val message = args.drop(2).joinToString(" ")
            val language = languagesPlayerIds[playerId]
            return if(language != null) {
                translateTextAsync(message, mainLanguage, language) { response ->
                    val translatedText = response?.first
                        ?: return@translateTextAsync onChatMessage(
                            "$PREFIX Не удалось получить перевод сообщения", "00BEBEBE"
                        )
                    sendChatInput("/n $translatedText")
                }
            } else {
                sendChatInput("/n $message")
            }
        }
        "//treload" -> return location.reload()
    }
    Regex("^!(\\d+)$").find(args[0])?.also { match ->
        return onSendChatMessage(
            text = "/tn ${match.groupValues[1]} ${args.drop(1).joinToString(" ")}"
        )
    }
    return sendChatInput(text)
}