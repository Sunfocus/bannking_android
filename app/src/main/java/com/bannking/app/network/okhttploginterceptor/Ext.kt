package com.bannking.app.network.okhttploginterceptor

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import okio.Buffer
import java.io.StringReader
import java.io.StringWriter
import java.nio.charset.Charset
import java.util.*
import javax.xml.transform.OutputKeys
import javax.xml.transform.Source
import javax.xml.transform.TransformerException
import javax.xml.transform.TransformerFactory
import javax.xml.transform.stream.StreamResult
import javax.xml.transform.stream.StreamSource


internal fun String.appendLine(): String {
    if (length >= LoggingInterceptor.MAX_LINE_LENGTH) {
        return this
    }

    val sb = StringBuilder(this)
    repeat(LoggingInterceptor.MAX_LINE_LENGTH - length) {
        sb.append(LoggingInterceptor.CLINE)
    }
    return sb.toString()
}

fun MediaType.isParsable(): Boolean {
    return (isText() || isPlain() || isJson() || isForm() || isHtml() || isXml())
}

fun MediaType.isText(): Boolean {
    return subtype.lowercase(Locale.ROOT).contains("text")
}

fun MediaType.isPlain(): Boolean {
    return subtype.lowercase(Locale.ROOT).contains("plain")
}

fun MediaType.isJson(): Boolean {
    return subtype.lowercase(Locale.ROOT).contains("json")
}

fun MediaType.isXml(): Boolean {
    return subtype.lowercase(Locale.ROOT).contains("xml")
}

fun MediaType.isHtml(): Boolean {
    return subtype.lowercase(Locale.ROOT).contains("html")
}

fun MediaType.isForm(): Boolean {
    return subtype.lowercase(Locale.ROOT).contains("x-www-form-urlencoded")
}

fun MediaType.isFile(): Boolean {
    return type.lowercase(Locale.ROOT) == "file"
}

fun MediaType.isMedia(): Boolean {
    val type = type.lowercase(Locale.ROOT)
    return type == "audio" || type == "video"
}

fun MediaType.isZip(): Boolean {
    return subtype.lowercase(Locale.ROOT).contains("zip")
}

fun MediaType.isUnreadable(): Boolean {
    return isFile() || isMedia() || isZip()
}

fun RequestBody.bodyString(): String {
    val charset: Charset = contentType()?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
    val buffer = Buffer()
    writeTo(buffer)
    return buffer.readString(charset)
}

fun ResponseBody.bodyString(): String {
    val charset: Charset = contentType()?.charset(Charsets.UTF_8) ?: Charsets.UTF_8
    val buffer = source().buffer.clone()
    return buffer.readString(charset)
}

fun RequestBody.formatAsPossible(
    visualFormat: Boolean = true,
    maxLineLength: Int = LoggingInterceptor.MAX_LINE_LENGTH
): List<String> {
    return bodyString().formatAsPossible(visualFormat, contentType(), maxLineLength)
}

fun ResponseBody.formatAsPossible(
    visualFormat: Boolean = true,
    maxLineLength: Int = LoggingInterceptor.MAX_LINE_LENGTH
): List<String> {
    return bodyString().formatAsPossible(visualFormat, contentType(), maxLineLength)
}

fun String.formatAsPossible(
    visualFormat: Boolean,
    contentType: MediaType?,
    maxLineLength: Int = LoggingInterceptor.MAX_LINE_LENGTH
): List<String> {
    if (isNullOrEmpty()) {
        return listOf("[Empty]")
    }

    if (!visualFormat) {
        return separateByLength(maxLineLength)
    }

    try {
        if (contentType == null) {
            if (isGuessJson()) {
                return jsonFormat()
            }
        } else {
            if (contentType.isJson() || isGuessJson()) {
                return jsonFormat()
            } else if (contentType.isXml() && startsWith("<") && endsWith(">")) {
                val list = xmlFormat()
                if (list.isEmpty()) {
                    return separateByLength(maxLineLength)
                }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return separateByLength(maxLineLength)
}

private val separateChars by lazy { charArrayOf(',', '\n', ' ', '>', ']', '.', '。', '，', '}') }

private fun String.separateByLength(maxLineLength: Int = LoggingInterceptor.MAX_LINE_LENGTH): List<String> {
    if (isNullOrEmpty()) {
        return emptyList()
    }
    if (length <= maxLineLength) {
        return listOf(this)
    }

    val list = mutableListOf<String>()

    var hasNextLine = true
    var startIndex = 0
    while (hasNextLine) {
        val endIndex = startIndex + maxLineLength
        if (endIndex <= length) {
            val line = substring(startIndex, endIndex)
            val subLine = line.substring(line.length - 30)
            val index = subLine.lastIndexOfAny(separateChars)
            if (index >= 0) {
                val tempIndex = endIndex - 30 + index + 1
                list.add(substring(startIndex, tempIndex))
                startIndex = tempIndex
            } else {
                list.add(line)
                startIndex += maxLineLength
            }
        } else {
            hasNextLine = false
            list.add(substring(startIndex, length))
        }
    }

    return list
}

fun String.jsonFormat(): List<String> {
    val jsonParser = JsonParser()
    val jsonObject: JsonObject = jsonParser.parse(this).asJsonObject
    val gson = LoggingInterceptor.gson
    return StringReader(gson.toJson(jsonObject)).readLines()
}

/**
 * xml格式化不完善，暂时不改了
 */
fun String.xmlFormat(): List<String> {
    if (isNullOrEmpty()) {
        return listOf("[Empty]")
    }

    return try {
        val xmlInput: Source = StreamSource(StringReader(this))
        val xmlOutput = StreamResult(StringWriter())
        val transformer = TransformerFactory.newInstance().newTransformer()
        transformer.setOutputProperty(OutputKeys.INDENT, "yes")
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2")
        transformer.transform(xmlInput, xmlOutput)
        StringReader(xmlOutput.writer.toString().replaceFirst(">".toRegex(), ">\n"))
            .readLines()
    } catch (e: TransformerException) {
        e.printStackTrace()
        emptyList()
    }
}

fun String.isGuessJson(): Boolean {
    val trim = trim()
    return (trim.startsWith("{") && trim.endsWith("}")) ||
            ((trim.startsWith("[") && trim.endsWith("]")))
}
