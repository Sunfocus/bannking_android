package com.bannking.app.network.okhttploginterceptor

import android.util.Log
import com.bannking.app.network.okhttploginterceptor.printer.DefaultLogPrinter
import com.bannking.app.network.okhttploginterceptor.printer.IPrinter
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.random.Random

/**
 * OkHttp interceptor, which has the function of printing request header, request body, response header, and response body. Since 3.0.0, the configuration has been simplified, and the printing mode has been simplified to 2 types. Detailed configuration
 * See parameter description.
 * @param showLog Whether to display Log
 * @param isShowAll `false`: Print everything except request parameters, request headers, and response headers. `true`: print everything
 * @param tag Log的tag
 * @param priority [Log] priority
 * @param visualFormat `true`: format json and xml strings; `false`: only control the maximum length of each line
 * @param maxLineLength the maximum number of strings per line
 * @param printer additional custom processing Log
 */
class LoggingInterceptor @JvmOverloads constructor(
    var showLog: Boolean = true,
    var isShowAll: Boolean = false,
    var tag: String = DEFAULT_TAG,
    var priority: Priority = Priority.I,
    var visualFormat: Boolean = true,
    var maxLineLength: Int = MAX_LINE_LENGTH,
    var printer: IPrinter? = null
) :
    Interceptor {
    var requestTag = "$tag-$REQUEST"
    var responseTag = "$tag-$RESPONSE"

    /**
     * Whether to ignore the response body that is too long, the default is true; the default is more than 16MB, and the response body is not printed.
     * Note: Only when [isParsable] and [ignoreLongBody] are true at the same time will the response body be printed.
     *
     */
    var ignoreLongBody = true

    /**
     * If it exceeds [ignoreBodyIfMoreThan] bytes, the response body will be ignored and not printed. Note: the unit here is bytes, and the default number of bytes is 16MB.
     */
    var ignoreBodyIfMoreThan = DEFAULT_IGNORE_LENGTH

    init {
        if (maxLineLength <= 0) {
            maxLineLength = MAX_LINE_LENGTH
        }
    }

    private val defaultPrinter = DefaultLogPrinter()

    private val singleExecutor = Executors.newSingleThreadExecutor()

    private fun canPrintBody(bodyLength: Int): Boolean {
        if (!ignoreLongBody) {
            return true
        }

        return ignoreLongBody && bodyLength < ignoreBodyIfMoreThan
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        return logIntercept(chain)
    }

    @Throws(IOException::class)
    private fun logIntercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        if (!showLog) {
            return chain.proceed(request)
        }

        printRequest(request)

        val startNs = System.nanoTime()

        val response: Response = try {
            chain.proceed(request)
        } catch (e: IOException) {
            printException(request, e)
            throw e
        }

        val tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs)
        // Special note: Don't put printResponse in asynchronous execution, close the data before it is finished
        printResponse(response, tookMs)
        return response
    }

    private fun printException(request: Request, e: IOException) {
        val list = mutableListOf<String>()
        val starter = "${LT}[Response][${request.method}] ${request.url} ".appendLine()
        list.add(starter)
        list.add("$L Exception:$e")
        list.add(FOOTER)
        printResponseList(list)
    }

    private fun printResponse(response: Response, tookMs: Long) {
        val request = response.request
        val responseBody = response.body

        val list = mutableListOf<String>()
        val starter =
            "${LT}[Response][${request.method} ${response.code} ${response.message} ${tookMs}ms] ${request.url} ".appendLine()
        list.add(starter)

        val headers = response.headers
        if (isShowAll) {
            list.add("$L Protocol: ${response.protocol}")
            if (headers.size > 0) {
                list.add("$L Headers:")
            }
            headers.map {
                list.add("$L ${it.first}: ${it.second}")
            }
            responseBody?.apply {
                contentType()?.let {
                    if (headers["Content-Type"] == null) {
                        list.add("$L Content-Type: $it")
                    }
                }
                if (contentLength() != -1L) {
                    if (headers["Content-Length"] == null) {
                        list.add("$L Content-Length: ${contentLength()}")
                    }
                }
            }
        }

        responseBody?.also {
            val peekBody = response.peekBody(Long.MAX_VALUE)
            if (isShowAll && responseBody.contentLength() == -1L) {
                list.add("$L Content-Length: ${peekBody.contentLength()}")
            }

            peekBody.contentType()?.apply {
                /**
                 * Can be parsed, and the length is exceeded, the response body will be printed. Not safe, can not judge [MultipartBody] uploaded files
                 */
                if (isParsable() && canPrintBody(peekBody.contentLength().toInt())
                    && !isUnreadable()
                ) {
                    list.add("$L Body:")
                    list.addAll(peekBody.formatAsPossible(visualFormat, maxLineLength).map {
                        "$L $it"
                    })
                    list.add(FOOTER)
                } else {
                    list.add(BODY_OMITTED)
                }
            }
        }

        printResponseList(list)
    }

    private fun printRequest(r: Request) {
        val request = r.newBuilder().build()
        val requestBody = request.body

        val list = mutableListOf<String>()
        val header = "${LT}[Request][${request.method}] ${request.url} ".appendLine()
        list.add(header)

        if (isShowAll) {
            val querySize = request.url.querySize
            if (querySize > 0) {
                list.add("$L Query: ${request.url.query}")
            }

            val headers = request.headers
            if (headers.size > 0) {
                list.add("$L Headers:")
            }
            list.addAll(headers.map {
                "$L ${it.first}: ${it.second}"
            })
            requestBody?.apply {
                contentType()?.let {
                    if (headers["Content-Type"] == null) {
                        list.add("$L Content-Type: $it")
                    }
                }
                if (contentLength() != -1L) {
                    if (headers["Content-Length"] == null) {
                        list.add("$L Content-Length: ${contentLength()}")
                    }
                }
            }
        }

        requestBody?.also {
            if (it is MultipartBody) {
                list.add("$L Multipart: size=${it.parts.size}")
                it.parts.mapIndexed { i, part ->
                    val body = part.body
                    list.add("$L Multipart.parts[$i]: ${body.contentType()}; ${body.contentLength()}; headers:${part.headers}")
                }
                list.add(FOOTER)
            } else {
                if (bodyHasUnknownEncoding(request.headers) ||
                    requestBody.isDuplex() ||
                    requestBody.isOneShot()
                ) {
                    list.add(BODY_OMITTED)
                } else {
                    list.add("$L Body:")

                    list.addAll(requestBody.formatAsPossible(visualFormat, maxLineLength).map {
                        "$L $it"
                    })

                    list.add(FOOTER)
                }
            }
        } ?: list.add(FOOTER)

        printRequestList(list)
    }

    private fun printRequestList(list: MutableList<String>) {
        singleExecutor.execute {
            list.map {
                print(requestTag, it)
            }
        }
    }

    private fun printResponseList(list: MutableList<String>) {
        singleExecutor.execute {
            list.map {
                print(responseTag, it)
            }
        }
    }


    /**
     * Print the string.
     *
     * In order to solve the problem that the logs output by Logcat above AndroidStudio v3.1 cannot be aligned
     * <p>
     * The reason for this problem, according to JessYan's guess, may be that AndroidStudio v3.1 and above will output with the same tag in a very short time
     * Multiple logs are automatically merged into one output, resulting in an asymmetrical problem with the originally symmetrical output log.
     * AndroidStudio v3.1's optimization of the output log accidentally made all log frameworks with log format output function on the market unable to work properly
     * There are currently two solutions that can be thought of temporarily: 1. Change the tag of each line (a changeable token is added to each line tag) 2. Delay the interval between each line of log printing
     *
     * At present, the random sleep is 0-2ms, which should be able to solve the problem of losing the last n lines of the multi-line log at the same time.
     *
     * @param msg the string to print
     */
    private fun print(tag: String, msg: String) {
        val millisecond = Random.nextInt(0, 3)
        Thread.sleep(millisecond.toLong())
        defaultPrinter.print(priority, tag, msg)
        printer?.print(priority, tag, msg)
    }


    private fun bodyHasUnknownEncoding(headers: Headers): Boolean {
        val contentEncoding = headers["Content-Encoding"] ?: return false
        return !contentEncoding.equals("identity", ignoreCase = true) &&
                !contentEncoding.equals("gzip", ignoreCase = true)
    }

    companion object {
        // Maximum number of characters in a line
        const val MAX_LINE_LENGTH = 1024
        const val LT = "┏"
        const val FOOTER = "┗[END]━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"
        const val LB = "┗"
        const val BODY_OMITTED = "┗[END]Unreadable Body Omitted━"
        const val L = "┃"
        const val CLINE = ""

        const val DEFAULT_TAG = "OkHttp"

        const val REQUEST = "Request"
        const val RESPONSE = "Response"

        /**
         * @since 3.0.6 100KB
         * @since 3.0.5 16MB
         */
        const val DEFAULT_IGNORE_LENGTH = 102400

        val gson: Gson by lazy {
            GsonBuilder().setPrettyPrinting().create()
        }

    }

}
