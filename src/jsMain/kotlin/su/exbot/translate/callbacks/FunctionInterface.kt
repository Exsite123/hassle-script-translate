package su.exbot.translate.callbacks


import su.exbot.translate.*
import kotlin.js.json

fun onInterface(methodName: String): dynamic {
    val func = functionInterface(methodName)

    if(methodName == "Report") {
        func.loadChatMessages = { data: String ->
            val text = translatedText[data] ?: run { // Костыль из-за вызовов interface("Report")
                val array = JSON.parse<Array<Array<Any>>>(data)
                array.forEachIndexed { index, item ->
                    val text = item[1].unsafeCast<String>()
                    val isAdmin = item[2].unsafeCast<Boolean>()
                    val playerLanguage = item[5].unsafeCast<String?>()

                    if(!(isAdmin && playerLanguage.isNullOrBlank())) {
                        translateTextAsync(query = text, targetLanguage = mainLanguage) { result ->
                            if(result != null) {
                                val (translated, language) = result
                                if(ignoreLanguages.none { it == language }) {
                                    array[index][1] = "${language.uppercase()} | $translated // $text"
                                }
                                array[index][5] = language

                                JSON.stringify(array).also { str ->
                                    translatedText[data] = str
                                    updateMessagesReport(func, str)
                                }
                            }
                        }
                    }
                }
                JSON.stringify(array).also { str ->
                    translatedText[data] = str
                }
            }
            updateMessagesReport(func, text)
        }

        func.sendMessage = fun() {
            val beingSentMessage = func.chat.message.unsafeCast<String>()

            if(beingSentMessage.toCharArray()[0] == '!') {
                val messages = func.chat.messages.unsafeCast<Array<dynamic>>()

                val languages = messages.filter { !it["isAdmin"].unsafeCast<Boolean>() }
                    .mapNotNull { it["language"].unsafeCast<String?>() }
                val topLanguage = languages.groupBy { it }.entries.maxByOrNull { it.value.size }?.key
                    ?: return onChatMessage("$PREFIX Не удалось получить язык игрока", "00FFFFFF")

                return translateTextAsync(beingSentMessage.drop(1), "ru", topLanguage) { result ->
                    if(result != null) {
                        func.chat.message = ""
                        sendClientEventHandle(0, "OnSendChatMessageTicket", result.first)
                    } else {
                        onChatMessage("$PREFIX Не удалось получить перевод текста", "00FFFFFF")
                    }
                }
            }
            func.chat.message = ""
            return sendClientEventHandle(0, "OnSendChatMessageTicket", beingSentMessage)
        }
    }
    return func
}

fun updateMessagesReport(func: dynamic, data: String) {
    val array = JSON.parse<Array<dynamic>>(data)
    val list = array.map { item ->
        json(
            "type" to item[0].unsafeCast<Int>(),
            "text" to item[1].unsafeCast<String>(),
            "isAdmin" to item[2].unsafeCast<Boolean>(),
            "time" to item[3].unsafeCast<Int>(),
            "name" to item[4].unsafeCast<String>(),
            "language" to item[5].unsafeCast<String?>(),
        )
    }
    func.chat.messages = list.toTypedArray()
}