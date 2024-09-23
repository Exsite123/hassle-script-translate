package su.exbot.translate

import js.uri.encodeURIComponent
import org.w3c.xhr.XMLHttpRequest

fun translateTextAsync(
    query: String,
    sourceLanguage: String = "auto",
    targetLanguage: String = mainLanguage,
    callback: (Pair<String, String>?) -> Unit
) {
    val url = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=$sourceLanguage&tl=$targetLanguage&dt=t&q=${encodeURIComponent(query)}"

    val request = XMLHttpRequest()
    request.open("GET", url, true)

    request.onload = {
        if (request.status == 200.toShort()) {
            val response = JSON.parse<dynamic>(request.responseText)
            val translated = response[0].unsafeCast<Array<dynamic>>().joinToString("") { it[0].toString() }
            val language = response[2] as String
            callback(translated to language)
        } else {
            callback(null)
        }
    }

    request.onerror = {
        callback(null)
    }
    request.send()
}