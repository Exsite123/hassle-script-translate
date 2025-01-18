package su.exbot.translate.callbacks

import su.exbot.translate.*
import su.exbot.translate.translateTextAsync

val regularExpressions = listOf(
    Regex("""^(\{\w+\}\[\w+\] \{\w+\})?\[(RADMIR|HASSLE)\] (\S+)\[(\d+)\] : \{\w+\}(.+)"""), // /report
    Regex("""^(\{\w+\}\[A\]\{\w+\} )?\(\( (\S+)\[(\d+)\]: (.+) \)\)$"""), // OOC чат
    Regex("""^(\{\w+\}\[A\]\{\w+\} )?- (.+) (\{\w+\})\((\S+)\)\[(\d+)\]"""), // IC чат
)
val ignoreLanguages = listOf(mainLanguage, "sr", "bg", "kk", "mk", "be", "mn", "ky", "tg", "tt", "uk", "sah")

fun onReceiveMessages(text: String, color: Any) {
    for((index, expression) in regularExpressions.withIndex()) {
        expression.find(text)?.let { match ->
            val groups = match.groupValues

            return when(index) {
                0 -> {
                    val tag = groups[1]
                    val platform = groups[2]
                    val playerName = groups[3]
                    val playerId = groups[4].toInt()
                    val sourceText = groups[5]

                    translateTextAsync(query = sourceText, targetLanguage = mainLanguage) { result ->
                        if(result != null) {
                            val (translated, language) = result

                            if(ignoreLanguages.none { it == language }) {
                                languagesPlayerIds[playerId] = language

                                val message = "$tag[${platform}-${language.uppercase()}] ${playerName}[${playerId}] : {FFCD00}$translated {c4c4c4}// {ffffff}${sourceText}"
                                return@translateTextAsync onChatMessage(message, color)
                            } else {
                                languagesPlayerIds[playerId] = null
                            }
                        }
                        onChatMessage(text, color)
                    }
                }
                1 -> {
                    val tag = groups[1]
                    val playerName = groups[2]
                    val playerId = groups[3].toInt()
                    val sourceText = groups[4]

                    translateTextAsync(query = sourceText, targetLanguage = mainLanguage) { result ->
                        if (result != null) {
                            val (translated, language) = result

                            if(ignoreLanguages.none { it == language }) {
                                languagesPlayerIds[playerId] = language

                                val message = "${language.uppercase()} | $tag(( $playerName[$playerId]: $translated )) {c4c4c4}// {ffffff}${sourceText}"
                                return@translateTextAsync onChatMessage(message, color)
                            }
                        }
                        onChatMessage(text, color)
                    }
                }
                2 -> {
                    val tag = groups[1]
                    val sourceText = groups[2]
                    val playerColor = groups[3]
                    val playerName = groups[4]
                    val playerId = groups[5].toInt()

                    translateTextAsync(query = sourceText, targetLanguage = mainLanguage) { result ->
                        if (result != null) {
                            val (translated, language) = result

                            languagesPlayerIds[playerId] = language

                            if(ignoreLanguages.none { it == language }) {
                                val message = "${language.uppercase()} | $tag- $translated $playerColor($playerName)[$playerId] {c4c4c4}// {ffffff}$sourceText"
                                return@translateTextAsync onChatMessage(message, color)
                            }
                        }
                        onChatMessage(text, color)
                    }
                }
                else -> onChatMessage("unknown #$index | $text", color)
            }
        }
    }
    return onChatMessage(text, color)
}
