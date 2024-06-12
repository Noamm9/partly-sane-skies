//
// Written by Su386.
// See LICENSE for copyright and license notices.
//


package me.partlysanestudios.partlysaneskies.data.api

import me.partlysanestudios.partlysaneskies.PartlySaneSkies
import me.partlysanestudios.partlysaneskies.utils.ChatUtils.sendClientMessage
import me.partlysanestudios.partlysaneskies.utils.SystemUtils.log
import org.apache.logging.log4j.Level
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL
import java.security.cert.X509Certificate
import javax.net.ssl.*

/**
 * @param url The requests URL
 * @param function The function that will activate when the response is finished. [RequestRunnable]s take the instance of the request as a parameter
 * @param inMainThread If true, the request will execute while in the main thread of the client freezing it and stopping anything else from happening while awaiting a response. Only really useful in specific cases.
 * @param executeOnNextFrame If true, the runnable will execute on the next frame of the main thread. Useful for when modifying things with the client
 * @param acceptAllCertificates If true, the request will accept all certificates for response
 * @author Su386
 */
abstract class Request(
    internal val url: URL,
    internal val function: RequestRunnable?,
    internal val inMainThread: Boolean = false,
    internal val executeOnNextFrame: Boolean = false,
    internal val acceptAllCertificates: Boolean = false
) {
    //    Constructor with string url and certificate option
    constructor(
        url: String,
        function: RequestRunnable?,
        inMainThread: Boolean = false,
        executeOnNextFrame: Boolean = false,
        acceptAllCertificates: Boolean = false
    ) : this(URL(url), function, inMainThread, executeOnNextFrame, acceptAllCertificates)

    //    Constructor without certificate option
    constructor(
        url: URL,
        function: RequestRunnable?,
        inMainThread: Boolean = false,
        executeOnNextFrame: Boolean = false
    ) : this(url, function, inMainThread, executeOnNextFrame, false)

    //    Constructor without certificates option
    constructor(
        url: String,
        function: RequestRunnable?,
        inMainThread: Boolean = false,
        executeOnNextFrame: Boolean = false
    ) : this(URL(url), function, inMainThread, executeOnNextFrame, false)

    // A string that contains the response message (not body)
    internal var responseMessage = ""

    // An int that contains the response code
    internal var responseCode = -1

    // A boolean to determining if there was an unknown failure
    // Gets set to true when setFailed(reason) is called
    internal var hasFailed = false

    // The String that contains the JSON response
    internal var response = ""

    // A list that contains all the HTTP codes where the request should rerun
    internal var tryAgainOnCodes = ArrayList<Int>()


    /**
     * Flags the request as failed
     *
     * @param reason optional failure message
     */
    fun setFailed(reason: String = "") {
        if (reason != "") {

            this.responseMessage = reason
        }
        this.hasFailed = true
    }

    /**
     * @return if the request has failed
     */
    fun hasSucceeded(): Boolean {
        return if (this.responseCode != HttpsURLConnection.HTTP_OK) {
            false
        } else !hasFailed
    }


    /**
     * Sends the get request
     */
    @Throws(IOException::class)
    abstract fun startRequest()

    /**
     * @return the request runnable
     */
    fun getWhatToRunWhenFinished(): RequestRunnable? {
        return function
    }

    /**
     * @return the request url
     */
    fun getURL(): URL {
        return url
    }

    /**
     * Adds codes to the try again list. Whenever the response encounters one of these codes, it will send a new request.
     *
     * @param responseCode the code to add
     * @return an instance of itself for easy chaining
     */
    fun addTryAgainResponse(responseCode: Int): Request {
        tryAgainOnCodes.add(responseCode)
        return this
    }

    /**
     * @return a string with the response, or an empty json object if the response is empty.
     */
    open fun getResponse(): String {
        if (this.response.isEmpty()) {
            return "{}"
        }
        return this.response
    }

    /**
     * @return if the request will run in the main thread
     */
    fun isMainThread(): Boolean {
        return inMainThread
    }

    /**
     * @return if the RequestManager will run the RequestRunnable in the main thread on the next frame
     */
    fun isRunNextFrame(): Boolean {
        return executeOnNextFrame
    }

    /**
     * @return the error message and response code
     */
    fun getErrorMessage(): String {
        return "Error: " + this.responseMessage + ":" + this.responseCode
    }
}