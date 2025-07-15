package ng.mpayer.softpos.utils

import android.util.Log


object LoggingUtils {
    private const val TAG_PREFIX = "SoftPOS"

    fun logDebug(tag: String, message: String) {
        if (BuildConfig.DEBUG) {
            Log.d("$TAG_PREFIX-$tag", message)
        }
    }

    fun logInfo(tag: String, message: String) {
        Log.i("$TAG_PREFIX-$tag", message)
    }

    fun logWarning(tag: String, message: String, throwable: Throwable? = null) {
        Log.w("$TAG_PREFIX-$tag", message, throwable)
    }

    fun logError(tag: String, message: String, throwable: Throwable? = null) {
        Log.e("$TAG_PREFIX-$tag", message, throwable)
    }

    fun logTransaction(transactionType: String, amount: String, result: String) {
        val message = "Transaction: $transactionType, Amount: $amount, Result: $result"
        logInfo("TRANSACTION", message)
    }

    fun logNFCEvent(event: String, details: String = "") {
        val message = "NFC Event: $event${if (details.isNotEmpty()) " - $details" else ""}"
        logDebug("NFC", message)
    }

    fun logNetworkEvent(event: String, details: String = "") {
        val message = "Network: $event${if (details.isNotEmpty()) " - $details" else ""}"
        logDebug("NETWORK", message)
    }

    fun logSecurityEvent(event: String) {
        logWarning("SECURITY", "Security Event: $event")
    }

    // Utility to mask sensitive data in logs
    fun maskSensitiveData(data: String, visibleChars: Int = 4): String {
        return if (data.length <= visibleChars * 2) {
            "*".repeat(data.length)
        } else {
            "${data.take(visibleChars)}${"*".repeat(data.length - visibleChars * 2)}${data.takeLast(visibleChars)}"
        }
    }
}